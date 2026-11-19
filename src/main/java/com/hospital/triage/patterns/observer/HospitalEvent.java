package com.hospital.triage.patterns.observer;

import com.hospital.triage.model.Patient;

import java.time.LocalDateTime;

public class HospitalEvent {
    private final HospitalEventType eventType;
    private final String title;
    private final String message;
    private final Patient patient;
    private final Object payload;
    private final LocalDateTime timestamp;

    public HospitalEvent(HospitalEventType eventType, String title, String message, Patient patient, Object payload) {
        this.eventType = eventType;
        this.title = title;
        this.message = message;
        this.patient = patient;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public HospitalEventType getEventType() { return eventType; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Patient getPatient() { return patient; }
    public Object getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
