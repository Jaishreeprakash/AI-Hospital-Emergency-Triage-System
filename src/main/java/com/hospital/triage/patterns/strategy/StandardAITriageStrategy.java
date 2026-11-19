package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Patient;
import com.hospital.triage.model.TriagePriority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy implementing the academic multi-factor emergency scoring model.
 * Factors:
 * 1. Heart Rate Points (0-3)
 * 2. Oxygen Saturation (SpO2) Points (0-3)
 * 3. Blood Pressure Points (0-3)
 * 4. Symptoms Points (0-3)
 *
 * Scoring classification:
 * P1 (8+): Critical (Immediate)
 * P2 (5-7): High (Urgent within 15 mins)
 * P3 (3-4): Moderate (Semi-urgent within 60 mins)
 * P4 (0-2): Low (Non-urgent)
 *
 * Note: Demonstration academic model for OOAD project. Requires clinical validation for real-world usage.
 */
@Component("standardAITriageStrategy")
public class StandardAITriageStrategy implements TriageScoringStrategy {

    @Override
    public String getStrategyName() {
        return "Standard Multi-Factor AI Clinical Triage Strategy";
    }

    @Override
    public TriageAssessmentResult calculateScore(Patient patient, int heartRate, int spo2, int systolicBp, int diastolicBp, String symptomsDescription) {
        int hrPoints = calculateHeartRatePoints(heartRate);
        int spo2Points = calculateSpo2Points(spo2);
        int bpPoints = calculateBloodPressurePoints(systolicBp, diastolicBp);
        int symptomPoints = calculateSymptomPoints(symptomsDescription);

        int totalScore = hrPoints + spo2Points + bpPoints + symptomPoints;
        TriagePriority priority = TriagePriority.fromScore(totalScore);

        List<String> rationales = new ArrayList<>();
        rationales.add(String.format("HR %d bpm: %d pts (%s)", heartRate, hrPoints, getHrDescriptor(hrPoints)));
        rationales.add(String.format("SpO2 %d%%: %d pts (%s)", spo2, spo2Points, getSpo2Descriptor(spo2Points)));
        rationales.add(String.format("BP %d/%d mmHg: %d pts (%s)", systolicBp, diastolicBp, bpPoints, getBpDescriptor(bpPoints)));
        rationales.add(String.format("Symptoms: %d pts (%s)", symptomPoints, getSymptomDescriptor(symptomPoints)));

        String explanation = String.format("Assigned Priority %s (%s) based on Total Score %d/12. Breakdown: [%s]. %s",
                priority.name(), priority.getLabel(), totalScore, String.join("; ", rationales), priority.getDescription());

        return new TriageAssessmentResult(hrPoints, spo2Points, bpPoints, symptomPoints, totalScore, priority, explanation);
    }

    public int calculateHeartRatePoints(int hr) {
        if (hr < 40 || hr >= 140) return 3;      // Severe bradycardia or extreme tachycardia
        if (hr < 50 || hr >= 120) return 2;      // Moderate bradycardia or tachycardia
        if (hr < 60 || hr >= 101) return 1;      // Mild variation
        return 0;                                 // Normal adult range: 60-100 bpm
    }

    public int calculateSpo2Points(int spo2) {
        if (spo2 < 88) return 3;                 // Critical hypoxemia
        if (spo2 <= 91) return 2;                // Moderate hypoxemia
        if (spo2 <= 94) return 1;                // Mild hypoxemia
        return 0;                                 // Normal oxygenation: >= 95%
    }

    public int calculateBloodPressurePoints(int sys, int dia) {
        if (sys >= 180 || sys < 80 || dia >= 120 || dia < 50) return 3; // Hypertensive crisis or severe hypotension / shock
        if (sys >= 160 || sys < 90 || dia >= 100) return 2;             // Stage 2 hypertension or moderate hypotension
        if (sys >= 140 || sys < 100 || dia >= 90) return 1;             // Mild elevation or borderline low
        return 0;                                                         // Normal BP: 100-139 / 60-89
    }

    public int calculateSymptomPoints(String description) {
        if (description == null || description.trim().isEmpty()) return 0;
        String desc = description.toLowerCase();

        // 3 Points: Critical life-threatening emergencies
        if (desc.contains("cardiac arrest") || desc.contains("unconscious") || desc.contains("stroke") ||
            desc.contains("stemi") || desc.contains("chest pain") || desc.contains("massive bleeding") ||
            desc.contains("anaphylaxis") || desc.contains("unresponsive") || desc.contains("seizure") ||
            desc.contains("respiratory failure")) {
            return 3;
        }

        // 2 Points: Severe acute distress
        if (desc.contains("fracture") || desc.contains("severe pain") || desc.contains("asthma") ||
            desc.contains("dyspnea") || desc.contains("shortness of breath") || desc.contains("wheezing") ||
            desc.contains("high fever") || desc.contains("peritonitis") || desc.contains("appendicitis") ||
            desc.contains("swelling") || desc.contains("dvt")) {
            return 2;
        }

        // 1 Point: Moderate discomfort
        if (desc.contains("fever") || desc.contains("vomiting") || desc.contains("nausea") ||
            desc.contains("dizziness") || desc.contains("dehydration") || desc.contains("moderate pain") ||
            desc.contains("cough") || desc.contains("abdominal pain")) {
            return 1;
        }

        // 0 Points: Minor / non-urgent
        return 0;
    }

    private String getHrDescriptor(int pts) {
        switch (pts) {
            case 3: return "Critical Dysrhythmia";
            case 2: return "Significant Tachy/Bradycardia";
            case 1: return "Mild Tachy/Bradycardia";
            default: return "Normal Heart Rate";
        }
    }

    private String getSpo2Descriptor(int pts) {
        switch (pts) {
            case 3: return "Severe Hypoxemia";
            case 2: return "Moderate Hypoxemia";
            case 1: return "Mild Desaturation";
            default: return "Optimal Oxygenation";
        }
    }

    private String getBpDescriptor(int pts) {
        switch (pts) {
            case 3: return "Hypertensive Crisis/Shock";
            case 2: return "Stage 2 HTN / Hypotension";
            case 1: return "Elevated / Pre-HTN";
            default: return "Normotensive";
        }
    }

    private String getSymptomDescriptor(int pts) {
        switch (pts) {
            case 3: return "Life-threatening Acute Presentation";
            case 2: return "Severe Distress / Urgent Care Required";
            case 1: return "Moderate Presentation";
            default: return "Mild / Ambulatory";
        }
    }
}
