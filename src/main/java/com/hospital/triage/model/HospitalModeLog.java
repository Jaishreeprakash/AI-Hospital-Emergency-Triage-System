package com.hospital.triage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mode_log")
public class HospitalModeLog {

    @Id
    @Column(name = "mode_log_id", length = 36)
    private String modeLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_mode", nullable = false, length = 20)
    private HospitalMode previousMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_mode", nullable = false, length = 20)
    private HospitalMode newMode;

    @Column(name = "occupancy_percentage", nullable = false)
    private Double occupancyPercentage;

    @Column(name = "urgent_cases_last_hour", nullable = false)
    private Integer urgentCasesLastHour = 0;

    @Column(name = "trigger_reason", nullable = false, length = 255)
    private String triggerReason;

    @Column(name = "triggered_by_staff_id", length = 36)
    private String triggeredByStaffId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public HospitalModeLog() {}

    public HospitalModeLog(String modeLogId, HospitalMode previousMode, HospitalMode newMode, Double occupancyPercentage, Integer urgentCasesLastHour, String triggerReason, String triggeredByStaffId) {
        this.modeLogId = modeLogId;
        this.previousMode = previousMode;
        this.newMode = newMode;
        this.occupancyPercentage = occupancyPercentage;
        this.urgentCasesLastHour = urgentCasesLastHour;
        this.triggerReason = triggerReason;
        this.triggeredByStaffId = triggeredByStaffId;
        this.createdAt = LocalDateTime.now();
    }

    public String getModeLogId() { return modeLogId; }
    public void setModeLogId(String modeLogId) { this.modeLogId = modeLogId; }

    public HospitalMode getPreviousMode() { return previousMode; }
    public void setPreviousMode(HospitalMode previousMode) { this.previousMode = previousMode; }

    public HospitalMode getNewMode() { return newMode; }
    public void setNewMode(HospitalMode newMode) { this.newMode = newMode; }

    public Double getOccupancyPercentage() { return occupancyPercentage; }
    public void setOccupancyPercentage(Double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; }

    public Integer getUrgentCasesLastHour() { return urgentCasesLastHour; }
    public void setUrgentCasesLastHour(Integer urgentCasesLastHour) { this.urgentCasesLastHour = urgentCasesLastHour; }

    public String getTriggerReason() { return triggerReason; }
    public void setTriggerReason(String triggerReason) { this.triggerReason = triggerReason; }

    public String getTriggeredByStaffId() { return triggeredByStaffId; }
    public void setTriggeredByStaffId(String triggeredByStaffId) { this.triggeredByStaffId = triggeredByStaffId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
