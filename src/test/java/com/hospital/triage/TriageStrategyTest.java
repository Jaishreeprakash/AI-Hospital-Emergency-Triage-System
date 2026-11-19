package com.hospital.triage;

import com.hospital.triage.model.Patient;
import com.hospital.triage.model.TriagePriority;
import com.hospital.triage.patterns.strategy.PediatricTriageStrategy;
import com.hospital.triage.patterns.strategy.StandardAITriageStrategy;
import com.hospital.triage.patterns.strategy.TriageAssessmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOAD Strategy Pattern - Emergency Triage Scoring Tests")
public class TriageStrategyTest {

    private StandardAITriageStrategy standardStrategy;
    private PediatricTriageStrategy pediatricStrategy;
    private Patient adultPatient;
    private Patient childPatient;

    @BeforeEach
    void setUp() {
        standardStrategy = new StandardAITriageStrategy();
        pediatricStrategy = new PediatricTriageStrategy(standardStrategy);

        adultPatient = new Patient("pat-t1", "PAT-TEST-001", "John Doe", 45, "MALE", "555-001", "555-002", "Chest pain", TriagePriority.P4);
        childPatient = new Patient("pat-t2", "PAT-TEST-002", "Tommy Doe", 6, "MALE", "555-003", "555-004", "High fever and cough", TriagePriority.P4);
    }

    @Test
    @DisplayName("Should classify P1 Critical when vitals reflect extreme cardiovascular failure")
    void testCriticalP1Assessment() {
        // HR: 145 (3 pts), SpO2: 84% (3 pts), BP: 190/115 (3 pts), Symptoms: crushing chest pain (3 pts)
        // Total = 12 points
        TriageAssessmentResult result = standardStrategy.calculateScore(
                adultPatient,
                145, 84, 190, 115,
                "Crushing chest pain radiating to jaw and diaphoresis"
        );

        assertEquals(3, result.getHrPoints());
        assertEquals(3, result.getSpo2Points());
        assertEquals(3, result.getBpPoints());
        assertEquals(3, result.getSymptomPoints());
        assertEquals(12, result.getTotalScore());
        assertEquals(TriagePriority.P1, result.getPriority());
        assertTrue(result.getClinicalExplanation().contains("P1"));
    }

    @Test
    @DisplayName("Should classify P2 High when score is between 5 and 7")
    void testHighPriorityP2Assessment() {
        // HR: 125 (2 pts), SpO2: 93% (1 pt), BP: 145/92 (1 pt), Symptoms: acute asthma (2 pts)
        // Total = 6 points -> P2
        TriageAssessmentResult result = standardStrategy.calculateScore(
                adultPatient,
                125, 93, 145, 92,
                "Acute asthma exacerbation with audible wheezing"
        );

        assertEquals(2, result.getHrPoints());
        assertEquals(1, result.getSpo2Points());
        assertEquals(1, result.getBpPoints());
        assertEquals(2, result.getSymptomPoints());
        assertEquals(6, result.getTotalScore());
        assertEquals(TriagePriority.P2, result.getPriority());
    }

    @Test
    @DisplayName("Should classify P3 Moderate when score is between 3 and 4")
    void testModeratePriorityP3Assessment() {
        // HR: 105 (1 pt), SpO2: 97% (0 pt), BP: 135/85 (0 pt), Symptoms: high fever and abdominal pain (2 pts)
        // Total = 3 points -> P3
        TriageAssessmentResult result = standardStrategy.calculateScore(
                adultPatient,
                105, 97, 135, 85,
                "High fever and localized abdominal pain"
        );

        assertEquals(1, result.getHrPoints());
        assertEquals(0, result.getSpo2Points());
        assertEquals(0, result.getBpPoints());
        assertEquals(2, result.getSymptomPoints());
        assertEquals(3, result.getTotalScore());
        assertEquals(TriagePriority.P3, result.getPriority());
    }

    @Test
    @DisplayName("Should classify P4 Low when vitals are normal and symptoms are mild")
    void testLowPriorityP4Assessment() {
        // HR: 72 (0 pt), SpO2: 99% (0 pt), BP: 120/80 (0 pt), Symptoms: mild ankle sprain (0 pt)
        // Total = 0 points -> P4
        TriageAssessmentResult result = standardStrategy.calculateScore(
                adultPatient,
                72, 99, 120, 80,
                "Mild ankle strain from soccer, able to walk"
        );

        assertEquals(0, result.getTotalScore());
        assertEquals(TriagePriority.P4, result.getPriority());
    }

    @Test
    @DisplayName("Pediatric Strategy should normalize normal pediatric elevated heart rate")
    void testPediatricStrategyHRNormalization() {
        // Pediatric normal HR is ~95 bpm. Standard strategy might count as mild tachycardia,
        // but pediatric strategy considers 80-120 normal (0 pts).
        TriageAssessmentResult childResult = pediatricStrategy.calculateScore(
                childPatient,
                95, 98, 100, 65,
                "Minor runny nose and cough"
        );

        assertEquals(0, childResult.getHrPoints());
        assertEquals(TriagePriority.P4, childResult.getPriority());
    }
}
