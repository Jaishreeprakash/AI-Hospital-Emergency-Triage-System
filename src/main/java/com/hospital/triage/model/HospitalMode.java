package com.hospital.triage.model;

public enum HospitalMode {
    NORMAL("Normal Operations", "Occupancy below 80%", "#10b981"),
    BUSY("Busy Hospital Mode", "Occupancy reached 80%", "#f59e0b"),
    SURGE("Surge Emergency Mode", "Occupancy >= 95% or 5 urgent cases/hr", "#e11d48");

    private final String title;
    private final String description;
    private final String color;

    HospitalMode(String title, String description, String color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
}
