package com.hospital.triage.model;

public enum AssessmentType {
    INITIAL_REGISTRATION("Initial Triage"),
    ROUTINE_RECHECK("Routine Re-check"),
    DOCTOR_OVERRIDE("Doctor Override"),
    URGENCY_ESCALATION("Urgency Escalation Detected");

    private final String description;

    AssessmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
