package com.hospital.triage.service;

import com.hospital.triage.model.Alert;
import com.hospital.triage.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByCreatedAtDesc();
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert resolveAlert(String alertId, String staffId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setIsResolved(true);
            alert.setResolvedByStaffId(staffId);
            alert.setResolvedAt(LocalDateTime.now());
            return alertRepository.save(alert);
        }
        return null;
    }

    public long getActiveAlertCount() {
        return alertRepository.countByIsResolvedFalse();
    }
}
