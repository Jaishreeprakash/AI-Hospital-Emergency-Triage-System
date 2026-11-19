package com.hospital.triage.model;

public enum StaffRole {
    RECEPTIONIST("Receptionist", "Patient Registration & Intake"),
    NURSE("Nurse", "Triage Assessment & Vitals Monitoring"),
    DOCTOR("Doctor", "Clinical Diagnosis, Bed Assignment & Priority Override"),
    ADMINISTRATOR("Administrator", "Hospital Mode & Capacity Management");

    private final String title;
    private final String description;

    StaffRole(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
