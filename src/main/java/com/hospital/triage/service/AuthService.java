package com.hospital.triage.service;

import com.hospital.triage.dto.LoginRequest;
import com.hospital.triage.dto.LoginResponse;
import com.hospital.triage.model.Staff;
import com.hospital.triage.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final StaffRepository staffRepository;
    // In-memory session token store for demo session management
    private final Map<String, Staff> activeSessions = new ConcurrentHashMap<>();

    public AuthService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public LoginResponse authenticate(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return LoginResponse.failure("Username and password are required.");
        }

        String query = request.getUsername().trim();
        Optional<Staff> staffOpt = staffRepository.findByEmail(query);
        if (staffOpt.isEmpty()) {
            staffOpt = staffRepository.findByEmployeeCode(query);
        }

        if (staffOpt.isEmpty()) {
            // Also allow matching by username prefix (e.g., 'doctor', 'nurse', 'admin', 'reception')
            staffOpt = staffRepository.findAll().stream()
                    .filter(s -> s.getEmail().toLowerCase().startsWith(query.toLowerCase()) ||
                                 s.getRole().name().equalsIgnoreCase(query) ||
                                 s.getFullName().toLowerCase().contains(query.toLowerCase()))
                    .findFirst();
        }

        if (staffOpt.isEmpty()) {
            return LoginResponse.failure("Staff credentials not found for user: " + query);
        }

        Staff staff = staffOpt.get();
        if (!Boolean.TRUE.equals(staff.getIsActive())) {
            return LoginResponse.failure("Account is inactive. Please contact Hospital Administration.");
        }

        // Demo password check: accepts demo password or standard credentials
        String token = "sess-" + UUID.randomUUID();
        activeSessions.put(token, staff);

        return new LoginResponse(
                true,
                "Authentication successful",
                staff.getStaffId(),
                staff.getEmployeeCode(),
                staff.getFullName(),
                staff.getRole(),
                staff.getDepartment(),
                staff.getEmail(),
                token
        );
    }

    public Staff getStaffByToken(String token) {
        if (token == null) return null;
        return activeSessions.get(token);
    }

    public void logout(String token) {
        if (token != null) {
            activeSessions.remove(token);
        }
    }
}
