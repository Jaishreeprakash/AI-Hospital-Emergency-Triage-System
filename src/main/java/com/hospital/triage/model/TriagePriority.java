package com.hospital.triage.model;

public enum TriagePriority {
    P1("Critical", 8, 12, "Immediate resuscitation/trauma care required", "#e11d48"),
    P2("High", 5, 7, "Urgent evaluation within 15 minutes", "#ea580c"),
    P3("Moderate", 3, 4, "Semi-urgent assessment within 60 minutes", "#d97706"),
    P4("Low", 0, 2, "Non-urgent / routine ambulatory care", "#10b981");

    private final String label;
    private final int minScore;
    private final int maxScore;
    private final String description;
    private final String badgeColor;

    TriagePriority(String label, int minScore, int maxScore, String description, String badgeColor) {
        this.label = label;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
        this.badgeColor = badgeColor;
    }

    public String getLabel() { return label; }
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
    public String getDescription() { return description; }
    public String getBadgeColor() { return badgeColor; }

    public static TriagePriority fromScore(int score) {
        if (score >= 8) return P1;
        if (score >= 5) return P2;
        if (score >= 3) return P3;
        return P4;
    }

    public int getUrgencyRank() {
        switch (this) {
            case P1: return 1;
            case P2: return 2;
            case P3: return 3;
            case P4: return 4;
            default: return 5;
        }
    }
}
