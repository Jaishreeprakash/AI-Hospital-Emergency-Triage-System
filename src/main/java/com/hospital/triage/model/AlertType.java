package com.hospital.triage.model;

public enum AlertType {
    CRITICAL_PATIENT("Critical Patient Alert"),
    PRIORITY_ESCALATED("Increased Urgency Alert"),
    NO_SUITABLE_BED("No Suitable Bed Available"),
    HOSPITAL_SURGE("Hospital Surge Protocol Triggered"),
    CAPACITY_WARNING("Ward Bed Capacity Warning"),
    NEW_REGISTRATION("New Patient Registered");

    private final String display;

    AlertType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
