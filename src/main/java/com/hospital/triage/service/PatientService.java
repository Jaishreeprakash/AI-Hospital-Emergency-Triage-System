package com.hospital.triage.service;

import com.hospital.triage.dto.PatientRegistrationRequest;
import com.hospital.triage.dto.TriageAssessmentRequest;
import com.hospital.triage.model.*;
import com.hospital.triage.patterns.observer.HospitalEvent;
import com.hospital.triage.patterns.observer.HospitalEventSubject;
import com.hospital.triage.patterns.observer.HospitalEventType;
import com.hospital.triage.repository.PatientRepository;
import com.hospital.triage.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final TriageService triageService;
    private final StaffRepository staffRepository;
    private final AuditLogService auditLogService;
    private final HospitalEventSubject eventSubject;

    public PatientService(PatientRepository patientRepository,
                          TriageService triageService,
                          StaffRepository staffRepository,
                          AuditLogService auditLogService,
                          HospitalEventSubject eventSubject) {
        this.patientRepository = patientRepository;
        this.triageService = triageService;
        this.staffRepository = staffRepository;
        this.auditLogService = auditLogService;
        this.eventSubject = eventSubject;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(String patientId) {
        return patientRepository.findById(patientId);
    }

    public Optional<Patient> getPatientByCode(String code) {
        return patientRepository.findByPatientCode(code);
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return patientRepository.findAll();
        }
        return patientRepository.searchPatients(query.trim());
    }

    /**
     * Waiting queue sorted by clinical urgency (P1 first, then P2, P3, P4, registration time ASC).
     */
    public List<Patient> getWaitingQueue() {
        return patientRepository.findWaitingQueueOrdered();
    }

    /**
     * Module 3: Patient Registration
     * Generates sequential/timestamped Patient ID, registers demographic data,
     * performs initial triage assessment, persists to MySQL, and logs action.
     */
    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {
        String patientId = "pat-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Auto-generate Patient ID: PAT-YYYYMMDD-XXXX
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = String.format("%04d", (patientRepository.count() + 1) % 10000);
        String patientCode = "PAT-" + datePart + "-" + suffix;

        Patient patient = new Patient(
                patientId,
                patientCode,
                request.getFullName(),
                request.getAge(),
                request.getGender(),
                request.getContactNumber(),
                request.getEmergencyContact(),
                request.getSymptoms(),
                TriagePriority.P4 // Initial placeholder; updated by triage calculation
        );

        Patient savedPatient = patientRepository.save(patient);

        // Automatically run initial Triage Scoring calculation
        TriageAssessmentRequest triageReq = new TriageAssessmentRequest();
        triageReq.setPatientId(savedPatient.getPatientId());
        triageReq.setHeartRate(request.getHeartRate());
        triageReq.setSpo2(request.getSpo2());
        triageReq.setSystolicBp(request.getSystolicBp());
        triageReq.setDiastolicBp(request.getDiastolicBp());
        triageReq.setSymptomsDescription(request.getSymptoms());
        triageReq.setStaffId(request.getStaffId());

        triageService.assessPatient(triageReq);

        // Re-read updated patient with calculated priority
        Patient finalPatient = patientRepository.findById(savedPatient.getPatientId()).orElse(savedPatient);

        // Log audit trail
        Staff staff = request.getStaffId() != null ? staffRepository.findById(request.getStaffId()).orElse(null) : null;
        auditLogService.logAction(
                "PATIENT_REGISTRATION",
                "PATIENT",
                finalPatient.getPatientId(),
                staff,
                null,
                finalPatient.getPatientCode() + " (" + finalPatient.getFullName() + ")",
                "New patient emergency admission registration with priority " + finalPatient.getCurrentPriority().name(),
                "127.0.0.1"
        );

        // Fire observer event
        eventSubject.notifyObservers(new HospitalEvent(
                HospitalEventType.NEW_PATIENT_REGISTERED,
                "New Patient Intake: " + finalPatient.getFullName(),
                String.format("Patient %s registered as %s (%s). Vitals: HR %d, SpO2 %d%%, BP %d/%d.",
                        finalPatient.getFullName(), finalPatient.getCurrentPriority().name(), finalPatient.getCurrentPriority().getLabel(),
                        request.getHeartRate(), request.getSpo2(), request.getSystolicBp(), request.getDiastolicBp()),
                finalPatient,
                finalPatient
        ));

        return finalPatient;
    }
}
