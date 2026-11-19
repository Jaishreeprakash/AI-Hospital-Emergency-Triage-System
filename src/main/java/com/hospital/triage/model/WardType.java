package com.hospital.triage.model;

public enum WardType {
    ICU("Intensive Care Unit"),
    EMERGENCY("Emergency Resuscitation & Trauma"),
    GENERAL_MALE("General Medicine (Male)"),
    GENERAL_FEMALE("General Medicine (Female)"),
    PEDIATRIC("Pediatric Ward");

    private final String displayName;

    WardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
