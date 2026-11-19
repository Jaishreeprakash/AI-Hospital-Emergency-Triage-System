package com.hospital.triage.dto;

import com.hospital.triage.model.StaffRole;

public class LoginResponse {
    private boolean success;
    private String message;
    private String staffId;
    private String employeeCode;
    private String fullName;
    private StaffRole role;
    private String department;
    private String email;
    private String sessionToken;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String staffId, String employeeCode, String fullName, StaffRole role, String department, String email, String sessionToken) {
        this.success = success;
        this.message = message;
        this.staffId = staffId;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.role = role;
        this.department = department;
        this.email = email;
        this.sessionToken = sessionToken;
    }

    public static LoginResponse failure(String message) {
        LoginResponse res = new LoginResponse();
        res.setSuccess(false);
        res.setMessage(message);
        return res;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

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

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}
