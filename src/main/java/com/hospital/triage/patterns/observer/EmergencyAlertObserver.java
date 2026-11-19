package com.hospital.triage.patterns.observer;

import com.hospital.triage.model.Alert;
import com.hospital.triage.model.AlertSeverity;
import com.hospital.triage.model.AlertType;
import com.hospital.triage.repository.AlertRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Observer that captures critical clinical and operational events
 * and generates actionable system alerts.
 */
@Component
public class EmergencyAlertObserver implements HospitalEventObserver {

    private final HospitalEventSubject subject;
    private final AlertRepository alertRepository;

    public EmergencyAlertObserver(HospitalEventSubject subject, AlertRepository alertRepository) {
        this.subject = subject;
        this.alertRepository = alertRepository;
    }

    @PostConstruct
    public void init() {
        subject.registerObserver(this);
    }

    @Override
    public String getObserverName() {
        return "Emergency Alert Observer";
    }

    @Override
    public void onHospitalEvent(HospitalEvent event) {
        AlertType alertType;
        AlertSeverity severity;

        switch (event.getEventType()) {
            case CRITICAL_PATIENT_ARRIVED:
                alertType = AlertType.CRITICAL_PATIENT;
                severity = AlertSeverity.CRITICAL;
                break;
            case WAITING_PATIENT_DETERIORATED:
                alertType = AlertType.PRIORITY_ESCALATED;
                severity = AlertSeverity.HIGH;
                break;
            case HOSPITAL_MODE_CHANGED:
                alertType = AlertType.HOSPITAL_SURGE;
                severity = AlertSeverity.CRITICAL;
                break;
            case BED_CAPACITY_LOW:
                alertType = AlertType.CAPACITY_WARNING;
                severity = AlertSeverity.HIGH;
                break;
            case NEW_PATIENT_REGISTERED:
                alertType = AlertType.NEW_REGISTRATION;
                severity = AlertSeverity.INFO;
                break;
            default:
                return;
        }

        Alert alert = new Alert(
                "alt-" + UUID.randomUUID().toString().substring(0, 8),
                event.getPatient(),
                alertType,
                severity,
                event.getTitle(),
                event.getMessage()
        );

        alertRepository.save(alert);
    }
}
