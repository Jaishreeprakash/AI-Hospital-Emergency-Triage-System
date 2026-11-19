package com.hospital.triage.repository;

import com.hospital.triage.model.Patient;
import com.hospital.triage.model.PatientStatus;
import com.hospital.triage.model.TriagePriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByPatientCode(String patientCode);
    List<Patient> findByCurrentStatus(PatientStatus status);
    List<Patient> findByCurrentPriority(TriagePriority priority);
    
    // Waiting queue sorted by urgency (P1 first, then registration time ASC)
    @Query("SELECT p FROM Patient p WHERE p.currentStatus = 'WAITING' ORDER BY CASE p.currentPriority WHEN 'P1' THEN 1 WHEN 'P2' THEN 2 WHEN 'P3' THEN 3 WHEN 'P4' THEN 4 ELSE 5 END ASC, p.registrationTime ASC")
    List<Patient> findWaitingQueueOrdered();

    long countByCurrentPriority(TriagePriority priority);
    long countByCurrentStatus(PatientStatus status);

    @Query("SELECT COUNT(p) FROM Patient p WHERE (p.currentPriority = 'P1' OR p.currentPriority = 'P2') AND p.registrationTime >= :sinceTime")
    int countUrgentArrivalsSince(@Param("sinceTime") LocalDateTime sinceTime);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> searchPatients(@Param("query") String query);
}
