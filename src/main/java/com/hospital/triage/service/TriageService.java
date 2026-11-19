package com.hospital.triage.service;

import com.hospital.triage.dto.TriageAssessmentRequest;
import com.hospital.triage.dto.VitalSignsRecheckRequest;
import com.hospital.triage.model.*;
import com.hospital.triage.patterns.observer.HospitalEvent;
import com.hospital.triage.patterns.observer.HospitalEventSubject;
import com.hospital.triage.patterns.observer.HospitalEventType;
import com.hospital.triage.patterns.strategy.PediatricTriageStrategy;
import com.hospital.triage.patterns.strategy.StandardAITriageStrategy;
import com.hospital.triage.patterns.strategy.TriageAssessmentResult;
import com.hospital.triage.patterns.strategy.TriageScoringStrategy;
import com.hospital.triage.repository.PatientRepository;
import com.hospital.triage.repository.TriageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TriageService {

    private final PatientRepository patientRepository;
    private final TriageRecordRepository triageRecordRepository;
    private final StandardAITriageStrategy standardStrategy;
    private final PediatricTriageStrategy pediatricStrategy;
    private final HospitalEventSubject eventSubject;
    private final AuditLogService auditLogService;

    public TriageService(PatientRepository patientRepository,
                         TriageRecordRepository triageRecordRepository,
                         StandardAITriageStrategy standardStrategy,
                         PediatricTriageStrategy pediatricStrategy,
                         HospitalEventSubject eventSubject,
                         AuditLogService auditLogService) {
        this.patientRepository = patientRepository;
        this.triageRecordRepository = triageRecordRepository;
        this.standardStrategy = standardStrategy;
        this.pediatricStrategy = pediatricStrategy;
        this.eventSubject = eventSubject;
        this.auditLogService = auditLogService;
    }

    /**
     * Dynamically selects the appropriate Strategy Pattern implementation
     * based on patient profile (Pediatric vs Adult standard).
     */
    public TriageScoringStrategy resolveStrategy(Patient patient) {
        if (patient != null && patient.getAge() != null && patient.getAge() < 12) {
            return pediatricStrategy;
        }
        return standardStrategy;
    }

    @Transactional
    public TriageRecord assessPatient(TriageAssessmentRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + req.getPatientId()));

        TriageScoringStrategy strategy = resolveStrategy(patient);
        String symptoms = req.getSymptomsDescription() != null && !req.getSymptomsDescription().trim().isEmpty()
                ? req.getSymptomsDescription() : patient.getSymptoms();

        TriageAssessmentResult result = strategy.calculateScore(
                patient,
                req.getHeartRate(),
                req.getSpo2(),
                req.getSystolicBp(),
                req.getDiastolicBp(),
                symptoms
        );

        TriageRecord record = new TriageRecord(
                "trg-" + UUID.randomUUID().toString().substring(0, 8),
                patient,
                req.getHeartRate(),
                req.getSpo2(),
                req.getSystolicBp(),
                req.getDiastolicBp(),
                symptoms,
                result.getHrPoints(),
                result.getSpo2Points(),
                result.getBpPoints(),
                result.getSymptomPoints(),
                result.getTotalScore(),
                result.getPriority(),
                result.getClinicalExplanation(),
                AssessmentType.INITIAL_REGISTRATION,
                req.getStaffId() != null ? req.getStaffId() : "stf-002"
        );

        TriageRecord savedRecord = triageRecordRepository.save(record);

        // Update patient priority
        patient.setCurrentPriority(result.getPriority());
        patient.setCurrentStatus(PatientStatus.WAITING);
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        // Notify if critical
        if (result.getPriority() == TriagePriority.P1) {
            eventSubject.notifyObservers(new HospitalEvent(
                    HospitalEventType.CRITICAL_PATIENT_ARRIVED,
                    "CRITICAL P1 PATIENT: " + patient.getFullName(),
                    String.format("Patient %s registered with triage score %d/12. Urgent resuscitation bay required immediately.",
                            patient.getFullName(), result.getTotalScore()),
                    patient,
                    savedRecord
            ));
        }

        return savedRecord;
    }

    /**
     * Module 5: Re-check Waiting Patients
     * Updates vital signs, recalculates score, compares old vs new priority,
     * automatically detects increased urgency, fires escalation alert, and reorders queue.
     */
    @Transactional
    public TriageRecord recheckWaitingPatient(String patientId, VitalSignsRecheckRequest req) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        TriagePriority oldPriority = patient.getCurrentPriority();

        TriageScoringStrategy strategy = resolveStrategy(patient);
        String symptoms = (req.getUpdatedSymptoms() != null && !req.getUpdatedSymptoms().trim().isEmpty())
                ? req.getUpdatedSymptoms() : patient.getSymptoms();

        TriageAssessmentResult result = strategy.calculateScore(
                patient,
                req.getHeartRate(),
                req.getSpo2(),
                req.getSystolicBp(),
                req.getDiastolicBp(),
                symptoms
        );

        TriagePriority newPriority = result.getPriority();
        boolean isEscalated = newPriority.getUrgencyRank() < oldPriority.getUrgencyRank();

        AssessmentType type = isEscalated ? AssessmentType.URGENCY_ESCALATION : AssessmentType.ROUTINE_RECHECK;

        String explanation = result.getClinicalExplanation();
        if (isEscalated) {
            explanation = String.format("[CLINICAL DETERIORATION DETECTED] Priority escalated from %s to %s. %s",
                    oldPriority.name(), newPriority.name(), explanation);
        }

        TriageRecord record = new TriageRecord(
                "trg-" + UUID.randomUUID().toString().substring(0, 8),
                patient,
                req.getHeartRate(),
                req.getSpo2(),
                req.getSystolicBp(),
                req.getDiastolicBp(),
                symptoms,
                result.getHrPoints(),
                result.getSpo2Points(),
                result.getBpPoints(),
                result.getSymptomPoints(),
                result.getTotalScore(),
                newPriority,
                explanation,
                type,
                req.getStaffId() != null ? req.getStaffId() : "stf-002"
        );

        TriageRecord savedRecord = triageRecordRepository.save(record);

        // Update patient
        patient.setCurrentPriority(newPriority);
        if (req.getUpdatedSymptoms() != null && !req.getUpdatedSymptoms().trim().isEmpty()) {
            patient.setSymptoms(req.getUpdatedSymptoms());
        }
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        // Audit Log
        auditLogService.logAction(
                isEscalated ? "PRIORITY_RECHECK_ESCALATION" : "ROUTINE_RECHECK",
                "PATIENT",
                patient.getPatientId(),
                null,
                oldPriority.name(),
                newPriority.name(),
                String.format("Vitals re-check: HR %d, SpO2 %d%%, BP %d/%d", req.getHeartRate(), req.getSpo2(), req.getSystolicBp(), req.getDiastolicBp()),
                "127.0.0.1"
        );

        // Trigger Alert if urgency increased
        if (isEscalated) {
            eventSubject.notifyObservers(new HospitalEvent(
                    HospitalEventType.WAITING_PATIENT_DETERIORATED,
                    "CLINICAL DETERIORATION ALERT: " + patient.getFullName(),
                    String.format("Waiting patient %s escalated from %s to %s (New Score: %d/12). Immediate review indicated.",
                            patient.getFullName(), oldPriority.name(), newPriority.name(), result.getTotalScore()),
                    patient,
                    savedRecord
            ));
        }

        return savedRecord;
    }

    public List<TriageRecord> getPatientTriageHistory(String patientId) {
        return triageRecordRepository.findByPatient_PatientIdOrderByAssessmentTimeDesc(patientId);
    }

    public List<TriageRecord> getRecentTriageRecords() {
        return triageRecordRepository.findTop10ByOrderByAssessmentTimeDesc();
    }
}
