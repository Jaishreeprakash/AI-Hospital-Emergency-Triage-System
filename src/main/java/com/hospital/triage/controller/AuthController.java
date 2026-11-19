package com.hospital.triage.controller;

import com.hospital.triage.dto.LoginRequest;
import com.hospital.triage.dto.LoginResponse;
import com.hospital.triage.model.Staff;
import com.hospital.triage.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(401).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Staff staff = authService.getStaffByToken(token);
        if (staff == null) {
            return ResponseEntity.status(401).body("{\"authenticated\": false, \"message\": \"No active session found.\"}");
        }
        return ResponseEntity.ok(staff);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.ok("{\"success\": true, \"message\": \"Logged out successfully.\"}");
    }
}
