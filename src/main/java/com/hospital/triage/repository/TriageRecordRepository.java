package com.hospital.triage.repository;

import com.hospital.triage.model.Patient;
import com.hospital.triage.model.TriageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriageRecordRepository extends JpaRepository<TriageRecord, String> {
    List<TriageRecord> findByPatientOrderByAssessmentTimeDesc(Patient patient);
    List<TriageRecord> findByPatient_PatientIdOrderByAssessmentTimeDesc(String patientId);
    List<TriageRecord> findTop10ByOrderByAssessmentTimeDesc();
}
