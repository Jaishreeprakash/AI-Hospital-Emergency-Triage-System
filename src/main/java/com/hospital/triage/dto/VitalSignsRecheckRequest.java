package com.hospital.triage.dto;

import jakarta.validation.constraints.*;

public class VitalSignsRecheckRequest {

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

    private String updatedSymptoms;

    private String staffId;

    public VitalSignsRecheckRequest() {}

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public String getUpdatedSymptoms() { return updatedSymptoms; }
    public void setUpdatedSymptoms(String updatedSymptoms) { this.updatedSymptoms = updatedSymptoms; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
