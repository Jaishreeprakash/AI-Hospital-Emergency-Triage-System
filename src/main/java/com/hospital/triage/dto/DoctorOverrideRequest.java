package com.hospital.triage.dto;

import com.hospital.triage.model.TriagePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DoctorOverrideRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotNull(message = "New priority is required")
    private TriagePriority newPriority;

    @NotBlank(message = "Mandatory clinical justification is required for doctor override")
    @Size(min = 10, max = 500, message = "Reason must provide adequate clinical rationale (10-500 characters)")
    private String mandatoryReason;

    private String doctorStaffId;

    public DoctorOverrideRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public TriagePriority getNewPriority() { return newPriority; }
    public void setNewPriority(TriagePriority newPriority) { this.newPriority = newPriority; }

    public String getMandatoryReason() { return mandatoryReason; }
    public void setMandatoryReason(String mandatoryReason) { this.mandatoryReason = mandatoryReason; }

    public String getDoctorStaffId() { return doctorStaffId; }
    public void setDoctorStaffId(String doctorStaffId) { this.doctorStaffId = doctorStaffId; }
}
