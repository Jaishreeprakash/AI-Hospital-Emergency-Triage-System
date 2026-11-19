package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Bed;

public class BedCandidateScore implements Comparable<BedCandidateScore> {
    private final Bed bed;
    private final double totalFitScore;     // 0 to 100%
    private final double priorityFitScore;  // 0 to 100% (weight: 40%)
    private final double equipmentScore;    // 0 to 100% (weight: 30%)
    private final double wardTypeScore;     // 0 to 100% (weight: 20%)
    private final double distanceScore;     // 0 to 100% (weight: 10%)
    private final String matchReason;

    public BedCandidateScore(Bed bed, double totalFitScore, double priorityFitScore, double equipmentScore, double wardTypeScore, double distanceScore, String matchReason) {
        this.bed = bed;
        this.totalFitScore = Math.round(totalFitScore * 10.0) / 10.0;
        this.priorityFitScore = Math.round(priorityFitScore * 10.0) / 10.0;
        this.equipmentScore = Math.round(equipmentScore * 10.0) / 10.0;
        this.wardTypeScore = Math.round(wardTypeScore * 10.0) / 10.0;
        this.distanceScore = Math.round(distanceScore * 10.0) / 10.0;
        this.matchReason = matchReason;
    }

    public Bed getBed() { return bed; }
    public double getTotalFitScore() { return totalFitScore; }
    public double getPriorityFitScore() { return priorityFitScore; }
    public double getEquipmentScore() { return equipmentScore; }
    public double getWardTypeScore() { return wardTypeScore; }
    public double getDistanceScore() { return distanceScore; }
    public String getMatchReason() { return matchReason; }

    @Override
    public int compareTo(BedCandidateScore o) {
        return Double.compare(o.totalFitScore, this.totalFitScore); // Highest score first
    }
}
