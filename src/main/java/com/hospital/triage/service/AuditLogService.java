package com.hospital.triage.service;

import com.hospital.triage.model.AuditLog;
import com.hospital.triage.model.Staff;
import com.hospital.triage.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog logAction(String actionType, String entityName, String entityId, Staff staff, String oldValue, String newValue, String reason, String ip) {
        AuditLog log = new AuditLog(
                "aud-" + UUID.randomUUID().toString().substring(0, 8),
                actionType,
                entityName,
                entityId,
                staff != null ? staff.getStaffId() : "sys",
                staff != null ? staff.getFullName() : "System / Automated",
                staff != null && staff.getRole() != null ? staff.getRole().name() : "SYSTEM",
                oldValue,
                newValue,
                reason,
                ip != null ? ip : "127.0.0.1"
        );
        log.setCreatedAt(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getLogsByAction(String actionType) {
        return auditLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
    }
}
