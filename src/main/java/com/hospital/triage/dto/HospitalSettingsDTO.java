package com.hospital.triage.dto;

public class HospitalSettingsDTO {
    private double busyThreshold = 80.0;
    private double surgeThreshold = 95.0;
    private int surgeUrgentLimit = 5;
    private double weightPriority = 0.40;
    private double weightEquipment = 0.30;
    private double weightWard = 0.20;
    private double weightDistance = 0.10;
    private String activeTriageStrategy = "standardAITriageStrategy";
    private boolean simulateAiPredictions = true;

    public HospitalSettingsDTO() {}

    public double getBusyThreshold() { return busyThreshold; }
    public void setBusyThreshold(double busyThreshold) { this.busyThreshold = busyThreshold; }

    public double getSurgeThreshold() { return surgeThreshold; }
    public void setSurgeThreshold(double surgeThreshold) { this.surgeThreshold = surgeThreshold; }

    public int getSurgeUrgentLimit() { return surgeUrgentLimit; }
    public void setSurgeUrgentLimit(int surgeUrgentLimit) { this.surgeUrgentLimit = surgeUrgentLimit; }

    public double getWeightPriority() { return weightPriority; }
    public void setWeightPriority(double weightPriority) { this.weightPriority = weightPriority; }

    public double getWeightEquipment() { return weightEquipment; }
    public void setWeightEquipment(double weightEquipment) { this.weightEquipment = weightEquipment; }

    public double getWeightWard() { return weightWard; }
    public void setWeightWard(double weightWard) { this.weightWard = weightWard; }

    public double getWeightDistance() { return weightDistance; }
    public void setWeightDistance(double weightDistance) { this.weightDistance = weightDistance; }

    public String getActiveTriageStrategy() { return activeTriageStrategy; }
    public void setActiveTriageStrategy(String activeTriageStrategy) { this.activeTriageStrategy = activeTriageStrategy; }

    public boolean isSimulateAiPredictions() { return simulateAiPredictions; }
    public void setSimulateAiPredictions(boolean simulateAiPredictions) { this.simulateAiPredictions = simulateAiPredictions; }
}
