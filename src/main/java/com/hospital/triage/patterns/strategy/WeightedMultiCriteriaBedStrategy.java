package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;
import com.hospital.triage.model.Patient;
import com.hospital.triage.model.TriagePriority;
import com.hospital.triage.model.WardType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Bed Ranking Strategy implementing the OOAD project requirements:
 * 1. Priority fit: 40%
 * 2. Required equipment: 30%
 * 3. Ward type: 20%
 * 4. Distance from ER: 10%
 */
@Component("weightedMultiCriteriaBedStrategy")
public class WeightedMultiCriteriaBedStrategy implements BedRankingStrategy {

    @Value("${hospital.bed-strategy.weight-priority:0.40}")
    private double weightPriority = 0.40;

    @Value("${hospital.bed-strategy.weight-equipment:0.30}")
    private double weightEquipment = 0.30;

    @Value("${hospital.bed-strategy.weight-ward:0.20}")
    private double weightWard = 0.20;

    @Value("${hospital.bed-strategy.weight-distance:0.10}")
    private double weightDistance = 0.10;

    @Override
    public String getStrategyName() {
        return "Weighted Multi-Criteria Bed Allocation Strategy (40-30-20-10)";
    }

    @Override
    public List<BedCandidateScore> rankBedsForPatient(Patient patient, List<Bed> candidateBeds) {
        List<BedCandidateScore> scores = new ArrayList<>();
        if (candidateBeds == null || candidateBeds.isEmpty()) {
            return scores;
        }

        TriagePriority priority = patient != null && patient.getCurrentPriority() != null
                ? patient.getCurrentPriority() : TriagePriority.P4;

        for (Bed bed : candidateBeds) {
            // Only available beds can be recommended
            if (bed.getStatus() != BedStatus.AVAILABLE) {
                continue;
            }

            double priorityScore = evaluatePriorityFit(priority, bed);
            double equipmentScore = evaluateEquipmentFit(priority, bed);
            double wardScore = evaluateWardTypeFit(patient, bed);
            double distanceScore = evaluateDistanceFit(bed);

            double totalScore = (priorityScore * weightPriority) +
                                (equipmentScore * weightEquipment) +
                                (wardScore * weightWard) +
                                (distanceScore * weightDistance);

            String matchReason = buildMatchReason(priority, bed, totalScore);

            scores.add(new BedCandidateScore(
                    bed,
                    totalScore,
                    priorityScore,
                    equipmentScore,
                    wardScore,
                    distanceScore,
                    matchReason
            ));
        }

        Collections.sort(scores);
        return scores;
    }

    private double evaluatePriorityFit(TriagePriority priority, Bed bed) {
        WardType wardType = bed.getWard() != null ? bed.getWard().getWardType() : WardType.GENERAL_MALE;

        switch (priority) {
            case P1:
                if (wardType == WardType.ICU) return 100.0;
                if (wardType == WardType.EMERGENCY) return 95.0;
                return 40.0;
            case P2:
                if (wardType == WardType.EMERGENCY) return 100.0;
                if (wardType == WardType.ICU) return 80.0;
                return 75.0;
            case P3:
                if (wardType == WardType.GENERAL_MALE || wardType == WardType.GENERAL_FEMALE || wardType == WardType.PEDIATRIC) return 100.0;
                if (wardType == WardType.EMERGENCY) return 70.0;
                return 30.0; // Reserve ICU for P1/P2
            case P4:
            default:
                if (wardType == WardType.GENERAL_MALE || wardType == WardType.GENERAL_FEMALE || wardType == WardType.PEDIATRIC) return 100.0;
                if (wardType == WardType.EMERGENCY) return 50.0;
                return 15.0;
        }
    }

    private double evaluateEquipmentFit(TriagePriority priority, Bed bed) {
        boolean hasVent = Boolean.TRUE.equals(bed.getHasVentilator());
        boolean hasMonitor = Boolean.TRUE.equals(bed.getHasCardiacMonitor());
        boolean hasO2 = Boolean.TRUE.equals(bed.getHasOxygenSupply());

        switch (priority) {
            case P1:
                double p1Score = 0.0;
                if (hasVent) p1Score += 45.0;
                if (hasMonitor) p1Score += 35.0;
                if (hasO2) p1Score += 20.0;
                return p1Score;
            case P2:
                double p2Score = 30.0;
                if (hasMonitor) p2Score += 40.0;
                if (hasO2) p2Score += 30.0;
                return Math.min(100.0, p2Score);
            case P3:
            case P4:
            default:
                // Normal ward equipment
                double basicScore = 70.0;
                if (hasO2) basicScore += 30.0;
                return basicScore;
        }
    }

    private double evaluateWardTypeFit(Patient patient, Bed bed) {
        if (patient == null || bed.getWard() == null) return 80.0;
        WardType wardType = bed.getWard().getWardType();

        // Pediatric check
        if (patient.getAge() != null && patient.getAge() < 14) {
            return wardType == WardType.PEDIATRIC ? 100.0 : 40.0;
        }

        // Gender check for general wards
        String gender = patient.getGender() != null ? patient.getGender().toUpperCase() : "OTHER";
        if (wardType == WardType.GENERAL_MALE) {
            return gender.equals("MALE") ? 100.0 : 0.0;
        }
        if (wardType == WardType.GENERAL_FEMALE) {
            return gender.equals("FEMALE") ? 100.0 : 0.0;
        }

        // ICU and Emergency are co-ed / universal
        return 100.0;
    }

    private double evaluateDistanceFit(Bed bed) {
        if (bed.getWard() == null) return 80.0;
        int distanceMeters = bed.getWard().getDistanceFromErMeters();
        // 0 to 100m -> mapped to 100% to 50%
        double score = 100.0 - (distanceMeters * 0.5);
        return Math.max(20.0, Math.min(100.0, score));
    }

    private String buildMatchReason(TriagePriority priority, Bed bed, double totalScore) {
        String wardName = bed.getWard() != null ? bed.getWard().getWardName() : "General";
        List<String> highlights = new ArrayList<>();
        if (Boolean.TRUE.equals(bed.getHasVentilator())) highlights.add("Ventilator");
        if (Boolean.TRUE.equals(bed.getHasCardiacMonitor())) highlights.add("Cardiac Monitor");
        if (Boolean.TRUE.equals(bed.getHasOxygenSupply())) highlights.add("O2 Port");

        String equipStr = highlights.isEmpty() ? "Standard Care" : String.join(", ", highlights);
        return String.format("Score: %.1f%% | %s in %s with [%s]", totalScore, bed.getBedNumber(), wardName, equipStr);
    }
}
