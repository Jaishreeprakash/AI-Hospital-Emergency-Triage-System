package com.hospital.triage.controller;

import com.hospital.triage.model.Alert;
import com.hospital.triage.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAlerts(@RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(alertService.getAllAlerts());
        }
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @PatchMapping("/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(
            @PathVariable String alertId,
            @RequestBody(required = false) Map<String, String> body) {
        String staffId = body != null ? body.get("staffId") : "stf-001";
        Alert resolved = alertService.resolveAlert(alertId, staffId);
        if (resolved == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resolved);
    }
}
