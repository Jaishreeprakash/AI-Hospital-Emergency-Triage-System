package com.hospital.triage.dto;

import jakarta.validation.constraints.*;

public class PatientRegistrationRequest {

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 130, message = "Age must be valid")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private String emergencyContact;

    @NotBlank(message = "Symptoms description is required")
    private String symptoms;

    // Optional initial vitals at registration
    @NotNull(message = "Heart rate is required")
    @Min(20) @Max(260)
    private Integer heartRate = 80;

    @NotNull(message = "SpO2 is required")
    @Min(50) @Max(100)
    private Integer spo2 = 98;

    @NotNull(message = "Systolic BP is required")
    @Min(50) @Max(280)
    private Integer systolicBp = 120;

    @NotNull(message = "Diastolic BP is required")
    @Min(30) @Max(180)
    private Integer diastolicBp = 80;

    private String staffId = "stf-001";

    public PatientRegistrationRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
