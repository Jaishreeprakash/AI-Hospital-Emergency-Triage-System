package com.hospital.triage.repository;

import com.hospital.triage.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType);
    List<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, String entityId);
}
