package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.TriagePriority;

public class TriageAssessmentResult {
    private final int hrPoints;
    private final int spo2Points;
    private final int bpPoints;
    private final int symptomPoints;
    private final int totalScore;
    private final TriagePriority priority;
    private final String clinicalExplanation;

    public TriageAssessmentResult(int hrPoints, int spo2Points, int bpPoints, int symptomPoints, int totalScore, TriagePriority priority, String clinicalExplanation) {
        this.hrPoints = hrPoints;
        this.spo2Points = spo2Points;
        this.bpPoints = bpPoints;
        this.symptomPoints = symptomPoints;
        this.totalScore = totalScore;
        this.priority = priority;
        this.clinicalExplanation = clinicalExplanation;
    }

    public int getHrPoints() { return hrPoints; }
    public int getSpo2Points() { return spo2Points; }
    public int getBpPoints() { return bpPoints; }
    public int getSymptomPoints() { return symptomPoints; }
    public int getTotalScore() { return totalScore; }
    public TriagePriority getPriority() { return priority; }
    public String getClinicalExplanation() { return clinicalExplanation; }
}
