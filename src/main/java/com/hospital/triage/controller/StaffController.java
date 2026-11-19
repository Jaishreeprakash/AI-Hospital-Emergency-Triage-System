package com.hospital.triage.controller;

import com.hospital.triage.model.Staff;
import com.hospital.triage.repository.StaffRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffRepository staffRepository;

    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable String id) {
        return staffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
