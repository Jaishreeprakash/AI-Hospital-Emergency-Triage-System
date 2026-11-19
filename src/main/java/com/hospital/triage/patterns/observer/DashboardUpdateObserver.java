package com.hospital.triage.patterns.observer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer that broadcasts real-time telemetry updates for dashboard clients.
 */
@Component
public class DashboardUpdateObserver implements HospitalEventObserver {

    private final HospitalEventSubject subject;

    public DashboardUpdateObserver(HospitalEventSubject subject) {
        this.subject = subject;
    }

    @PostConstruct
    public void init() {
        subject.registerObserver(this);
    }

    @Override
    public String getObserverName() {
        return "Dashboard Live Telemetry Observer";
    }

    @Override
    public void onHospitalEvent(HospitalEvent event) {
        // Dispatches metrics update logging
        System.out.println("[DashboardObserver] Broadcasted live event: " + event.getEventType() + " - " + event.getTitle());
    }
}
