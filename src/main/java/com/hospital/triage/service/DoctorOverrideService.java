package com.hospital.triage.service;

import com.hospital.triage.dto.DoctorOverrideRequest;
import com.hospital.triage.model.*;
import com.hospital.triage.patterns.observer.HospitalEvent;
import com.hospital.triage.patterns.observer.HospitalEventSubject;
import com.hospital.triage.patterns.observer.HospitalEventType;
import com.hospital.triage.repository.PatientRepository;
import com.hospital.triage.repository.StaffRepository;
import com.hospital.triage.repository.TriageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DoctorOverrideService {

    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final TriageRecordRepository triageRecordRepository;
    private final AuditLogService auditLogService;
    private final HospitalEventSubject eventSubject;

    public DoctorOverrideService(PatientRepository patientRepository,
                                 StaffRepository staffRepository,
                                 TriageRecordRepository triageRecordRepository,
                                 AuditLogService auditLogService,
                                 HospitalEventSubject eventSubject) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.triageRecordRepository = triageRecordRepository;
        this.auditLogService = auditLogService;
        this.eventSubject = eventSubject;
    }

    @Transactional
    public Patient applyDoctorOverride(DoctorOverrideRequest request) {
        if (request.getMandatoryReason() == null || request.getMandatoryReason().trim().length() < 5) {
            throw new IllegalArgumentException("A mandatory clinical reason is required to perform a doctor priority override.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + request.getPatientId()));

        TriagePriority oldPriority = patient.getCurrentPriority();
        TriagePriority newPriority = request.getNewPriority();

        Staff doctor = null;
        if (request.getDoctorStaffId() != null) {
            doctor = staffRepository.findById(request.getDoctorStaffId()).orElse(null);
        }
        if (doctor == null) {
            // Default to senior ER physician
            doctor = staffRepository.findByRole(StaffRole.DOCTOR).stream().findFirst().orElse(null);
        }

        String doctorName = doctor != null ? doctor.getFullName() : "Dr. Marcus Vance, MD (Attending)";
        String staffId = doctor != null ? doctor.getStaffId() : "stf-003";

        // Update patient
        patient.setCurrentPriority(newPriority);
        patient.setUpdatedAt(LocalDateTime.now());
        Patient updatedPatient = patientRepository.save(patient);

        // Record a triage entry reflecting the override
        TriageRecord overrideRecord = new TriageRecord(
                "trg-" + UUID.randomUUID().toString().substring(0, 8),
                patient,
                100, 98, 120, 80, // Default baseline markers if manual override
                0, 0, 0, 0,
                newPriority.getMinScore(),
                newPriority,
                String.format("DOCTOR OVERRIDE: Priority manually set from %s to %s by %s. Mandatory Reason: %s",
                        oldPriority.name(), newPriority.name(), doctorName, request.getMandatoryReason()),
                AssessmentType.DOCTOR_OVERRIDE,
                staffId
        );
        triageRecordRepository.save(overrideRecord);

        // Save detailed record in the audit log
        auditLogService.logAction(
                "DOCTOR_OVERRIDE",
                "PATIENT",
                patient.getPatientId(),
                doctor,
                oldPriority.name() + " (" + oldPriority.getLabel() + ")",
                newPriority.name() + " (" + newPriority.getLabel() + ")",
                request.getMandatoryReason(),
                "127.0.0.1"
        );

        // Fire observer event
        eventSubject.notifyObservers(new HospitalEvent(
                HospitalEventType.DOCTOR_OVERRIDE_PERFORMED,
                "Doctor Priority Override: " + patient.getFullName(),
                String.format("%s overrode patient %s from %s to %s. Reason: %s",
                        doctorName, patient.getFullName(), oldPriority.name(), newPriority.name(), request.getMandatoryReason()),
                patient,
                request
        ));

        return updatedPatient;
    }
}
