package com.hospital.triage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @Column(name = "staff_id", length = 36)
    private String staffId;

    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private StaffRole role;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Staff() {}

    public Staff(String staffId, String employeeCode, String fullName, StaffRole role, String department, String email, String passwordHash, String contactPhone) {
        this.staffId = staffId;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.role = role;
        this.department = department;
        this.email = email;
        this.passwordHash = passwordHash;
        this.contactPhone = contactPhone;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
