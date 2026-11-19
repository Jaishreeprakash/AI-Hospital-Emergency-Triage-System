package com.hospital.triage.dto;

import jakarta.validation.constraints.*;

public class TriageAssessmentRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotNull(message = "Heart rate is required")
    @Min(20) @Max(260)
    private Integer heartRate;

    @NotNull(message = "SpO2 is required")
    @Min(50) @Max(100)
    private Integer spo2;

    @NotNull(message = "Systolic BP is required")
    @Min(50) @Max(280)
    private Integer systolicBp;

    @NotNull(message = "Diastolic BP is required")
    @Min(30) @Max(180)
    private Integer diastolicBp;

    private String symptomsDescription;

    private String staffId;

    public TriageAssessmentRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public String getSymptomsDescription() { return symptomsDescription; }
    public void setSymptomsDescription(String symptomsDescription) { this.symptomsDescription = symptomsDescription; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
