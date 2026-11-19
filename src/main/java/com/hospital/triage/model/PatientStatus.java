package com.hospital.triage.model;

public enum PatientStatus {
    WAITING("In Waiting Room"),
    TRIAGED("Triaged & Scored"),
    BED_ASSIGNED("Bed Assigned"),
    IN_TREATMENT("In Active Treatment"),
    DISCHARGED("Discharged");

    private final String label;

    PatientStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
