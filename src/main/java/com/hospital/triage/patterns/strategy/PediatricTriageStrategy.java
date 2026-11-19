package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Patient;
import com.hospital.triage.model.TriagePriority;
import org.springframework.stereotype.Component;

/**
 * Strategy Pattern variant specialized for pediatric patients (< 12 years).
 * Demonstrates the extensibility of OOAD Strategy Pattern by adjusting
 * baseline heart rate expectations (normal pediatric HR is higher: 80-120 bpm)
 * and blood pressure baselines.
 */
@Component("pediatricTriageStrategy")
public class PediatricTriageStrategy implements TriageScoringStrategy {

    private final StandardAITriageStrategy standardStrategy;

    public PediatricTriageStrategy(StandardAITriageStrategy standardStrategy) {
        this.standardStrategy = standardStrategy;
    }

    @Override
    public String getStrategyName() {
        return "Pediatric Specialized AI Triage Strategy";
    }

    @Override
    public TriageAssessmentResult calculateScore(Patient patient, int heartRate, int spo2, int systolicBp, int diastolicBp, String symptomsDescription) {
        // If patient is 12 or older, delegate to standard strategy
        if (patient != null && patient.getAge() != null && patient.getAge() >= 12) {
            return standardStrategy.calculateScore(patient, heartRate, spo2, systolicBp, diastolicBp, symptomsDescription);
        }

        // Pediatric adjusted HR: 80-120 is normal
        int hrPoints;
        if (heartRate < 60 || heartRate >= 170) hrPoints = 3;
        else if (heartRate < 70 || heartRate >= 150) hrPoints = 2;
        else if (heartRate < 80 || heartRate >= 130) hrPoints = 1;
        else hrPoints = 0;

        int spo2Points = standardStrategy.calculateSpo2Points(spo2);

        // Pediatric BP (lower normal baseline: 85-110 systolic)
        int bpPoints;
        if (systolicBp >= 140 || systolicBp < 70) bpPoints = 3;
        else if (systolicBp >= 125 || systolicBp < 80) bpPoints = 2;
        else if (systolicBp >= 115 || systolicBp < 85) bpPoints = 1;
        else bpPoints = 0;

        int symptomPoints = standardStrategy.calculateSymptomPoints(symptomsDescription);

        int totalScore = hrPoints + spo2Points + bpPoints + symptomPoints;
        TriagePriority priority = TriagePriority.fromScore(totalScore);

        String explanation = String.format("Pediatric Assessment for age %d: Total Score %d/12 (HR: %d, SpO2: %d, BP: %d, Symptoms: %d). Priority %s.",
                patient != null && patient.getAge() != null ? patient.getAge() : 0,
                totalScore, hrPoints, spo2Points, bpPoints, symptomPoints, priority.name());

        return new TriageAssessmentResult(hrPoints, spo2Points, bpPoints, symptomPoints, totalScore, priority, explanation);
    }
}
