package com.hospital.triage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @Column(name = "alert_id", length = 36)
    private String alertId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AlertSeverity severity;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;

    @Column(name = "resolved_by_staff_id", length = 36)
    private String resolvedByStaffId;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Alert() {}

    public Alert(String alertId, Patient patient, AlertType alertType, AlertSeverity severity, String title, String message) {
        this.alertId = alertId;
        this.patient = patient;
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.isResolved = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }

    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }

    public String getResolvedByStaffId() { return resolvedByStaffId; }
    public void setResolvedByStaffId(String resolvedByStaffId) { this.resolvedByStaffId = resolvedByStaffId; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
