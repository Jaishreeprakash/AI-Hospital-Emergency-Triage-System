package com.hospital.triage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(name = "patient_id", length = 36)
    private String patientId;

    @Column(name = "patient_code", nullable = false, unique = true, length = 30)
    private String patientCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false, length = 20)
    private String gender;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    @Column(name = "symptoms", nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_priority", nullable = false, length = 10)
    private TriagePriority currentPriority = TriagePriority.P4;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 30)
    private PatientStatus currentStatus = PatientStatus.WAITING;

    @Column(name = "assigned_bed_id", length = 36)
    private String assignedBedId;

    @Column(name = "registration_time", updatable = false)
    private LocalDateTime registrationTime = LocalDateTime.now();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Patient() {}

    public Patient(String patientId, String patientCode, String fullName, Integer age, String gender, String contactNumber, String emergencyContact, String symptoms, TriagePriority currentPriority) {
        this.patientId = patientId;
        this.patientCode = patientCode;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.emergencyContact = emergencyContact;
        this.symptoms = symptoms;
        this.currentPriority = currentPriority != null ? currentPriority : TriagePriority.P4;
        this.currentStatus = PatientStatus.WAITING;
        this.registrationTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String patientCode) { this.patientCode = patientCode; }

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

    public TriagePriority getCurrentPriority() { return currentPriority; }
    public void setCurrentPriority(TriagePriority currentPriority) { this.currentPriority = currentPriority; }

    public PatientStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(PatientStatus currentStatus) { this.currentStatus = currentStatus; }

    public String getAssignedBedId() { return assignedBedId; }
    public void setAssignedBedId(String assignedBedId) { this.assignedBedId = assignedBedId; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
