package com.hospital.triage.repository;

import com.hospital.triage.model.Alert;
import com.hospital.triage.model.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    List<Alert> findByIsResolvedFalseOrderByCreatedAtDesc();
    List<Alert> findBySeverityAndIsResolvedFalse(AlertSeverity severity);
    List<Alert> findByPatient_PatientId(String patientId);
    long countByIsResolvedFalse();
}
