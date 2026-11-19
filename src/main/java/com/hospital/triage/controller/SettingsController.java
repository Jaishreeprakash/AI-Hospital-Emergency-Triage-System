package com.hospital.triage.controller;

import com.hospital.triage.dto.HospitalSettingsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {

    private HospitalSettingsDTO currentSettings = new HospitalSettingsDTO();

    @GetMapping
    public ResponseEntity<HospitalSettingsDTO> getSettings() {
        return ResponseEntity.ok(currentSettings);
    }

    @PutMapping
    public ResponseEntity<HospitalSettingsDTO> updateSettings(@RequestBody HospitalSettingsDTO updatedSettings) {
        if (updatedSettings != null) {
            this.currentSettings = updatedSettings;
        }
        return ResponseEntity.ok(this.currentSettings);
    }
}
