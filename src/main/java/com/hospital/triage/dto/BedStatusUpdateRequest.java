package com.hospital.triage.dto;

import com.hospital.triage.model.BedStatus;
import jakarta.validation.constraints.NotNull;

public class BedStatusUpdateRequest {

    @NotNull(message = "New bed status is required")
    private BedStatus newStatus;

    private String patientId;
    private String staffId;

    public BedStatusUpdateRequest() {}

    public BedStatus getNewStatus() { return newStatus; }
    public void setNewStatus(BedStatus newStatus) { this.newStatus = newStatus; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
