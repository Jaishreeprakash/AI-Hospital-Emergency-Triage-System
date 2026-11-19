package com.hospital.triage.controller;

import com.hospital.triage.dto.DoctorOverrideRequest;
import com.hospital.triage.model.Patient;
import com.hospital.triage.service.DoctorOverrideService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triage/override")
@CrossOrigin(origins = "*")
public class DoctorOverrideController {

    private final DoctorOverrideService doctorOverrideService;

    public DoctorOverrideController(DoctorOverrideService doctorOverrideService) {
        this.doctorOverrideService = doctorOverrideService;
    }

    @PostMapping
    public ResponseEntity<Patient> applyOverride(@Valid @RequestBody DoctorOverrideRequest request) {
        Patient updated = doctorOverrideService.applyDoctorOverride(request);
        return ResponseEntity.ok(updated);
    }
}
