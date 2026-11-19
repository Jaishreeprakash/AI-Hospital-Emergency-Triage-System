package com.hospital.triage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @Column(name = "log_id", length = 36)
    private String logId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "entity_name", nullable = false, length = 50)
    private String entityName;

    @Column(name = "entity_id", nullable = false, length = 50)
    private String entityId;

    @Column(name = "staff_id", length = 36)
    private String staffId;

    @Column(name = "staff_name", nullable = false, length = 100)
    private String staffName;

    @Column(name = "staff_role", nullable = false, length = 30)
    private String staffRole;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "mandatory_reason", columnDefinition = "TEXT")
    private String mandatoryReason;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(String logId, String actionType, String entityName, String entityId, String staffId, String staffName, String staffRole, String oldValue, String newValue, String mandatoryReason, String ipAddress) {
        this.logId = logId;
        this.actionType = actionType;
        this.entityName = entityName;
        this.entityId = entityId;
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffRole = staffRole;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.mandatoryReason = mandatoryReason;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getStaffRole() { return staffRole; }
    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getMandatoryReason() { return mandatoryReason; }
    public void setMandatoryReason(String mandatoryReason) { this.mandatoryReason = mandatoryReason; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
