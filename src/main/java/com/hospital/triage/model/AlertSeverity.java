package com.hospital.triage.model;

public enum AlertSeverity {
    CRITICAL("#e11d48"),
    HIGH("#ea580c"),
    MEDIUM("#d97706"),
    INFO("#0284c7");

    private final String color;

    AlertSeverity(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
