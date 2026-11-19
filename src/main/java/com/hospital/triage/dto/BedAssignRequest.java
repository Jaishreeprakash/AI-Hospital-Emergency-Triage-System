package com.hospital.triage.dto;

import jakarta.validation.constraints.NotBlank;

public class BedAssignRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private String staffId;
    private String clinicalNotes;

    public BedAssignRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }
}
