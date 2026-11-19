package com.hospital.triage.controller;

import com.hospital.triage.dto.BedForecastDTO;
import com.hospital.triage.dto.DashboardStatsDTO;
import com.hospital.triage.service.DashboardService;
import com.hospital.triage.service.HospitalModeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final HospitalModeService hospitalModeService;

    public DashboardController(DashboardService dashboardService, HospitalModeService hospitalModeService) {
        this.dashboardService = dashboardService;
        this.hospitalModeService = hospitalModeService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/forecast")
    public ResponseEntity<BedForecastDTO> getForecast(
            @RequestParam(required = false) Integer expectedAdmissions,
            @RequestParam(required = false) Integer expectedDischarges) {
        return ResponseEntity.ok(hospitalModeService.calculateForecast(expectedAdmissions, expectedDischarges));
    }
}
