package com.hospital.triage.patterns.observer;

import com.hospital.triage.repository.PatientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer tracking patient status changes and dynamically
 * updating the priority-ordered waiting queue.
 */
@Component
public class WaitingQueueObserver implements HospitalEventObserver {

    private final HospitalEventSubject subject;
    private final PatientRepository patientRepository;

    public WaitingQueueObserver(HospitalEventSubject subject, PatientRepository patientRepository) {
        this.subject = subject;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        subject.registerObserver(this);
    }

    @Override
    public String getObserverName() {
        return "Waiting Queue Urgency Observer";
    }

    @Override
    public void onHospitalEvent(HospitalEvent event) {
        if (event.getEventType() == HospitalEventType.WAITING_PATIENT_DETERIORATED ||
            event.getEventType() == HospitalEventType.NEW_PATIENT_REGISTERED ||
            event.getEventType() == HospitalEventType.DOCTOR_OVERRIDE_PERFORMED) {
            
            // Queue re-sorting is query-driven; logging state update
            System.out.println("[WaitingQueueObserver] Queue re-ordered following event: " + event.getTitle());
        }
    }
}
