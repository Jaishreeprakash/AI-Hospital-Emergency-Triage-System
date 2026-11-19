package com.hospital.triage;

import com.hospital.triage.model.*;
import com.hospital.triage.patterns.strategy.BedCandidateScore;
import com.hospital.triage.patterns.strategy.WeightedMultiCriteriaBedStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOAD Strategy Pattern - Weighted Bed Ranking Tests")
public class BedRankingStrategyTest {

    private WeightedMultiCriteriaBedStrategy strategy;
    private Ward icuWard;
    private Ward erWard;
    private Ward generalWard;

    @BeforeEach
    void setUp() {
        strategy = new WeightedMultiCriteriaBedStrategy();
        icuWard = new Ward("w-icu", "ICU", "ICU Ward", WardType.ICU, 10, 2, "North", 40);
        erWard = new Ward("w-er", "ER", "ER Bay", WardType.EMERGENCY, 10, 1, "East", 15);
        generalWard = new Ward("w-gm", "GM", "General Male", WardType.GENERAL_MALE, 14, 3, "South", 85);
    }

    @Test
    @DisplayName("Critical P1 patient should rank ICU/ER bed with ventilator highest")
    void testP1CriticalBedRecommendation() {
        Patient criticalPatient = new Patient("pat-p1", "PAT-001", "Cardiac Patient", 65, "MALE", "555-01", "555-02", "Chest pain", TriagePriority.P1);

        Bed icuBedWithVent = new Bed("b1", "ICU-101", icuWard, BedStatus.AVAILABLE, true, true, true, false);
        Bed erBedStandard = new Bed("b2", "ER-101", erWard, BedStatus.AVAILABLE, false, true, true, false);
        Bed gmBed = new Bed("b3", "GM-101", generalWard, BedStatus.AVAILABLE, false, false, true, false);

        List<BedCandidateScore> ranked = strategy.rankBedsForPatient(criticalPatient, Arrays.asList(gmBed, icuBedWithVent, erBedStandard));

        assertFalse(ranked.isEmpty());
        // Highest ranked should be ICU bed with full critical care equipment
        assertEquals("ICU-101", ranked.get(0).getBed().getBedNumber());
        assertTrue(ranked.get(0).getTotalFitScore() > ranked.get(1).getTotalFitScore());
        assertTrue(ranked.get(0).getPriorityFitScore() >= 95.0);
    }

    @Test
    @DisplayName("Gender matching must filter general ward appropriateness")
    void testGenderFitInGeneralWard() {
        Patient femalePatient = new Patient("pat-f1", "PAT-002", "Jane Smith", 32, "FEMALE", "555-03", "555-04", "Abdominal pain", TriagePriority.P3);

        Bed maleWardBed = new Bed("b-m", "GM-201", generalWard, BedStatus.AVAILABLE, false, false, true, false);
        Ward femaleWard = new Ward("w-gf", "GF", "General Female", WardType.GENERAL_FEMALE, 14, 3, "South", 85);
        Bed femaleWardBed = new Bed("b-f", "GF-201", femaleWard, BedStatus.AVAILABLE, false, false, true, false);

        List<BedCandidateScore> ranked = strategy.rankBedsForPatient(femalePatient, Arrays.asList(maleWardBed, femaleWardBed));

        assertEquals("GF-201", ranked.get(0).getBed().getBedNumber());
        assertTrue(ranked.get(0).getWardTypeScore() > ranked.get(1).getWardTypeScore());
    }
}
