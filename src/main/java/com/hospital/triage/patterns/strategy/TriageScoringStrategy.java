package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Patient;

/**
 * Strategy Pattern Interface for Triage Scoring Algorithms.
 * Allows pluggable scoring rules (e.g., adult standard, pediatric-specific, disaster triage).
 */
public interface TriageScoringStrategy {
    String getStrategyName();
    TriageAssessmentResult calculateScore(Patient patient, int heartRate, int spo2, int systolicBp, int diastolicBp, String symptomsDescription);
}
