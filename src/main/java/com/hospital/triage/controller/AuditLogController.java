package com.hospital.triage.controller;

import com.hospital.triage.model.AuditLog;
import com.hospital.triage.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs(@RequestParam(required = false) String action) {
        if (action != null && !action.trim().isEmpty()) {
            return ResponseEntity.ok(auditLogService.getLogsByAction(action));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }
}
