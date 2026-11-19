package com.hospital.triage.dto;

import java.util.List;

public class BedForecastDTO {
    private long totalCapacity;
    private long currentOccupiedBeds;
    private double currentOccupancyPercentage;
    
    // Four-hour projection inputs/outputs
    private int expectedAdmissions;
    private int expectedDischarges;
    private long projectedOccupiedBeds;
    private double projectedOccupancyPercentage;
    private boolean isCapacityWarning;
    private String warningMessage;

    // Hourly intervals for Chart.js [Current, +1h, +2h, +3h, +4h]
    private List<String> hourLabels;
    private List<Long> hourlyProjectedBeds;
    private List<Double> hourlyOccupancyPercentages;

    public BedForecastDTO() {}

    public long getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(long totalCapacity) { this.totalCapacity = totalCapacity; }

    public long getCurrentOccupiedBeds() { return currentOccupiedBeds; }
    public void setCurrentOccupiedBeds(long currentOccupiedBeds) { this.currentOccupiedBeds = currentOccupiedBeds; }

    public double getCurrentOccupancyPercentage() { return currentOccupancyPercentage; }
    public void setCurrentOccupancyPercentage(double currentOccupancyPercentage) { this.currentOccupancyPercentage = currentOccupancyPercentage; }

    public int getExpectedAdmissions() { return expectedAdmissions; }
    public void setExpectedAdmissions(int expectedAdmissions) { this.expectedAdmissions = expectedAdmissions; }

    public int getExpectedDischarges() { return expectedDischarges; }
    public void setExpectedDischarges(int expectedDischarges) { this.expectedDischarges = expectedDischarges; }

    public long getProjectedOccupiedBeds() { return projectedOccupiedBeds; }
    public void setProjectedOccupiedBeds(long projectedOccupiedBeds) { this.projectedOccupiedBeds = projectedOccupiedBeds; }

    public double getProjectedOccupancyPercentage() { return projectedOccupancyPercentage; }
    public void setProjectedOccupancyPercentage(double projectedOccupancyPercentage) { this.projectedOccupancyPercentage = projectedOccupancyPercentage; }

    public boolean isCapacityWarning() { return isCapacityWarning; }
    public void setCapacityWarning(boolean capacityWarning) { isCapacityWarning = capacityWarning; }

    public String getWarningMessage() { return warningMessage; }
    public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }

    public List<String> getHourLabels() { return hourLabels; }
    public void setHourLabels(List<String> hourLabels) { this.hourLabels = hourLabels; }

    public List<Long> getHourlyProjectedBeds() { return hourlyProjectedBeds; }
    public void setHourlyProjectedBeds(List<Long> hourlyProjectedBeds) { this.hourlyProjectedBeds = hourlyProjectedBeds; }

    public List<Double> getHourlyOccupancyPercentages() { return hourlyOccupancyPercentages; }
    public void setHourlyOccupancyPercentages(List<Double> hourlyOccupancyPercentages) { this.hourlyOccupancyPercentages = hourlyOccupancyPercentages; }
}
