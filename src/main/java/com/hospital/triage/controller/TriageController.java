package com.hospital.triage.controller;

import com.hospital.triage.dto.TriageAssessmentRequest;
import com.hospital.triage.dto.VitalSignsRecheckRequest;
import com.hospital.triage.model.TriageRecord;
import com.hospital.triage.service.TriageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/triage")
@CrossOrigin(origins = "*")
public class TriageController {

    private final TriageService triageService;

    public TriageController(TriageService triageService) {
        this.triageService = triageService;
    }

    @PostMapping("/assess")
    public ResponseEntity<TriageRecord> assessPatient(@Valid @RequestBody TriageAssessmentRequest request) {
        return ResponseEntity.ok(triageService.assessPatient(request));
    }

    @PostMapping("/recheck/{patientId}")
    public ResponseEntity<TriageRecord> recheckPatient(
            @PathVariable String patientId,
            @Valid @RequestBody VitalSignsRecheckRequest request) {
        return ResponseEntity.ok(triageService.recheckWaitingPatient(patientId, request));
    }

    @GetMapping("/history/{patientId}")
    public ResponseEntity<List<TriageRecord>> getPatientHistory(@PathVariable String patientId) {
        return ResponseEntity.ok(triageService.getPatientTriageHistory(patientId));
    }

    @GetMapping("/records")
    public ResponseEntity<List<TriageRecord>> getRecentRecords() {
        return ResponseEntity.ok(triageService.getRecentTriageRecords());
    }
}
