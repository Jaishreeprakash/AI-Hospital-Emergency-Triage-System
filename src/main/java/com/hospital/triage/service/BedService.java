package com.hospital.triage.service;

import com.hospital.triage.dto.BedAssignRequest;
import com.hospital.triage.dto.BedStatusUpdateRequest;
import com.hospital.triage.model.*;
import com.hospital.triage.patterns.observer.HospitalEvent;
import com.hospital.triage.patterns.observer.HospitalEventSubject;
import com.hospital.triage.patterns.observer.HospitalEventType;
import com.hospital.triage.patterns.state.BedStateContext;
import com.hospital.triage.patterns.strategy.BedCandidateScore;
import com.hospital.triage.patterns.strategy.WeightedMultiCriteriaBedStrategy;
import com.hospital.triage.repository.BedRepository;
import com.hospital.triage.repository.PatientRepository;
import com.hospital.triage.repository.StaffRepository;
import com.hospital.triage.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BedService {

    private final BedRepository bedRepository;
    private final WardRepository wardRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final WeightedMultiCriteriaBedStrategy bedRankingStrategy;
    private final AuditLogService auditLogService;
    private final HospitalEventSubject eventSubject;

    public BedService(BedRepository bedRepository,
                      WardRepository wardRepository,
                      PatientRepository patientRepository,
                      StaffRepository staffRepository,
                      WeightedMultiCriteriaBedStrategy bedRankingStrategy,
                      AuditLogService auditLogService,
                      HospitalEventSubject eventSubject) {
        this.bedRepository = bedRepository;
        this.wardRepository = wardRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.bedRankingStrategy = bedRankingStrategy;
        this.auditLogService = auditLogService;
        this.eventSubject = eventSubject;
    }

    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    public List<Bed> getBedsByWard(String wardId) {
        return bedRepository.findByWard_WardId(wardId);
    }

    public List<Bed> getBedsByStatus(BedStatus status) {
        return bedRepository.findByStatus(status);
    }

    /**
     * Recommends beds for a patient using the Strategy Pattern
     * (40% priority + 30% equipment + 20% ward type + 10% distance).
     */
    public List<BedCandidateScore> recommendBedsForPatient(String patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        List<Bed> allBeds = bedRepository.findAll();
        List<BedCandidateScore> recommendations = bedRankingStrategy.rankBedsForPatient(patient, allBeds);

        if (recommendations.isEmpty()) {
            eventSubject.notifyObservers(new HospitalEvent(
                    HospitalEventType.BED_CAPACITY_LOW,
                    "NO SUITABLE BED AVAILABLE: " + patient.getFullName(),
                    "No available bed matched criteria for patient " + patient.getFullName() + " (" + patient.getCurrentPriority() + ")",
                    patient,
                    null
            ));
        }

        return recommendations;
    }

    /**
     * Assigns and occupies a bed for a patient using the State Pattern.
     */
    @Transactional
    public Bed assignBedToPatient(String bedId, BedAssignRequest request) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new IllegalArgumentException("Bed not found: " + bedId));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + request.getPatientId()));

        BedStatus oldStatus = bed.getStatus();

        // Enforce State Pattern transition
        BedStateContext context = new BedStateContext(bed);
        context.occupy(bed, patient.getPatientId());

        bed.setUpdatedAt(LocalDateTime.now());
        Bed savedBed = bedRepository.save(bed);

        // Update patient status
        patient.setAssignedBedId(savedBed.getBedId());
        patient.setCurrentStatus(PatientStatus.BED_ASSIGNED);
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        // Audit Log
        Staff staff = request.getStaffId() != null ? staffRepository.findById(request.getStaffId()).orElse(null) : null;
        auditLogService.logAction(
                "BED_ASSIGNMENT",
                "BED",
                bed.getBedId(),
                staff,
                oldStatus.name(),
                "OCCUPIED (" + patient.getFullName() + ")",
                request.getClinicalNotes() != null ? request.getClinicalNotes() : "Bed assigned to patient " + patient.getPatientCode(),
                "127.0.0.1"
        );

        return savedBed;
    }

    /**
     * Updates bed state (e.g., transition to CLEANING, mark AVAILABLE, or release)
     * using the State Pattern.
     */
    @Transactional
    public Bed updateBedStatus(String bedId, BedStatusUpdateRequest request) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new IllegalArgumentException("Bed not found: " + bedId));

        BedStatus oldStatus = bed.getStatus();
        BedStatus newStatus = request.getNewStatus();

        BedStateContext context = new BedStateContext(bed);

        switch (newStatus) {
            case AVAILABLE:
                if (oldStatus == BedStatus.CLEANING) {
                    context.completeCleaning(bed);
                } else if (oldStatus == BedStatus.RESERVED) {
                    context.release(bed);
                } else {
                    bed.setStatus(BedStatus.AVAILABLE);
                    bed.setCurrentPatientId(null);
                }
                break;

            case RESERVED:
                context.reserve(bed, request.getPatientId());
                break;

            case OCCUPIED:
                context.occupy(bed, request.getPatientId());
                break;

            case CLEANING:
                context.release(bed);
                break;
        }

        bed.setUpdatedAt(LocalDateTime.now());
        Bed savedBed = bedRepository.save(bed);

        // If patient was released from bed
        if (oldStatus == BedStatus.OCCUPIED && newStatus == BedStatus.CLEANING && bed.getCurrentPatientId() != null) {
            patientRepository.findById(bed.getCurrentPatientId()).ifPresent(p -> {
                p.setAssignedBedId(null);
                p.setCurrentStatus(PatientStatus.IN_TREATMENT);
                patientRepository.save(p);
            });
        }

        Staff staff = request.getStaffId() != null ? staffRepository.findById(request.getStaffId()).orElse(null) : null;
        auditLogService.logAction(
                "BED_STATUS_UPDATE",
                "BED",
                bed.getBedId(),
                staff,
                oldStatus.name(),
                newStatus.name(),
                "Bed status transitioned via State pattern",
                "127.0.0.1"
        );

        return savedBed;
    }
}
