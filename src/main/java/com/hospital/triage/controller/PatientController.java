package com.hospital.triage.controller;

import com.hospital.triage.dto.PatientRegistrationRequest;
import com.hospital.triage.model.Patient;
import com.hospital.triage.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/queue")
    public ResponseEntity<List<Patient>> getWaitingQueue() {
        return ResponseEntity.ok(patientService.getWaitingQueue());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        Patient savedPatient = patientService.registerPatient(request);
        return ResponseEntity.ok(savedPatient);
    }
}
