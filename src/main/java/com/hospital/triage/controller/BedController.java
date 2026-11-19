package com.hospital.triage.controller;

import com.hospital.triage.dto.BedAssignRequest;
import com.hospital.triage.dto.BedStatusUpdateRequest;
import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;
import com.hospital.triage.model.Ward;
import com.hospital.triage.patterns.strategy.BedCandidateScore;
import com.hospital.triage.service.BedService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds(@RequestParam(required = false) BedStatus status) {
        if (status != null) {
            return ResponseEntity.ok(bedService.getBedsByStatus(status));
        }
        return ResponseEntity.ok(bedService.getAllBeds());
    }

    @GetMapping("/wards")
    public ResponseEntity<List<Ward>> getWards() {
        return ResponseEntity.ok(bedService.getAllWards());
    }

    @GetMapping("/wards/{wardId}")
    public ResponseEntity<List<Bed>> getBedsByWard(@PathVariable String wardId) {
        return ResponseEntity.ok(bedService.getBedsByWard(wardId));
    }

    @GetMapping("/recommend/{patientId}")
    public ResponseEntity<List<BedCandidateScore>> recommendBeds(@PathVariable String patientId) {
        return ResponseEntity.ok(bedService.recommendBedsForPatient(patientId));
    }

    @PostMapping("/{bedId}/assign")
    public ResponseEntity<Bed> assignBed(@PathVariable String bedId, @Valid @RequestBody BedAssignRequest request) {
        return ResponseEntity.ok(bedService.assignBedToPatient(bedId, request));
    }

    @PatchMapping("/{bedId}/status")
    public ResponseEntity<Bed> updateStatus(@PathVariable String bedId, @Valid @RequestBody BedStatusUpdateRequest request) {
        return ResponseEntity.ok(bedService.updateBedStatus(bedId, request));
    }
}
