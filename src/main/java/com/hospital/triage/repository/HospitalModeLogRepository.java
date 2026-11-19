package com.hospital.triage.repository;

import com.hospital.triage.model.HospitalModeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalModeLogRepository extends JpaRepository<HospitalModeLog, String> {
    List<HospitalModeLog> findAllByOrderByCreatedAtDesc();
    HospitalModeLog findTopByOrderByCreatedAtDesc();
}
