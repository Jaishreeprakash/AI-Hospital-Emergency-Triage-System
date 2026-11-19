package com.hospital.triage.dto;

import com.hospital.triage.model.Alert;
import com.hospital.triage.model.HospitalMode;
import com.hospital.triage.model.Patient;

import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    private long totalPatients;
    private long criticalPatientsCount;      // P1
    private long highPriorityPatientsCount;  // P2
    private long moderatePatientsCount;      // P3
    private long lowPriorityPatientsCount;   // P4
    private long waitingQueueCount;
    private long totalBeds;
    private long availableBeds;
    private long occupiedBeds;
    private long cleaningBeds;
    private long reservedBeds;
    private double occupancyPercentage;
    private HospitalMode currentHospitalMode;
    private String modeDirective;
    private int urgentCasesLastHour;
    private List<Patient> recentEmergencyCases;
    private List<Alert> activeAlerts;
    private Map<String, Long> priorityDistribution;
    private Map<String, Long> wardOccupancy;
    private BedForecastDTO forecast;

    public DashboardStatsDTO() {}

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getCriticalPatientsCount() { return criticalPatientsCount; }
    public void setCriticalPatientsCount(long criticalPatientsCount) { this.criticalPatientsCount = criticalPatientsCount; }

    public long getHighPriorityPatientsCount() { return highPriorityPatientsCount; }
    public void setHighPriorityPatientsCount(long highPriorityPatientsCount) { this.highPriorityPatientsCount = highPriorityPatientsCount; }

    public long getModeratePatientsCount() { return moderatePatientsCount; }
    public void setModeratePatientsCount(long moderatePatientsCount) { this.moderatePatientsCount = moderatePatientsCount; }

    public long getLowPriorityPatientsCount() { return lowPriorityPatientsCount; }
    public void setLowPriorityPatientsCount(long lowPriorityPatientsCount) { this.lowPriorityPatientsCount = lowPriorityPatientsCount; }

    public long getWaitingQueueCount() { return waitingQueueCount; }
    public void setWaitingQueueCount(long waitingQueueCount) { this.waitingQueueCount = waitingQueueCount; }

    public long getTotalBeds() { return totalBeds; }
    public void setTotalBeds(long totalBeds) { this.totalBeds = totalBeds; }

    public long getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(long availableBeds) { this.availableBeds = availableBeds; }

    public long getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(long occupiedBeds) { this.occupiedBeds = occupiedBeds; }

    public long getCleaningBeds() { return cleaningBeds; }
    public void setCleaningBeds(long cleaningBeds) { this.cleaningBeds = cleaningBeds; }

    public long getReservedBeds() { return reservedBeds; }
    public void setReservedBeds(long reservedBeds) { this.reservedBeds = reservedBeds; }

    public double getOccupancyPercentage() { return occupancyPercentage; }
    public void setOccupancyPercentage(double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; }

    public HospitalMode getCurrentHospitalMode() { return currentHospitalMode; }
    public void setCurrentHospitalMode(HospitalMode currentHospitalMode) { this.currentHospitalMode = currentHospitalMode; }

    public String getModeDirective() { return modeDirective; }
    public void setModeDirective(String modeDirective) { this.modeDirective = modeDirective; }

    public int getUrgentCasesLastHour() { return urgentCasesLastHour; }
    public void setUrgentCasesLastHour(int urgentCasesLastHour) { this.urgentCasesLastHour = urgentCasesLastHour; }

    public List<Patient> getRecentEmergencyCases() { return recentEmergencyCases; }
    public void setRecentEmergencyCases(List<Patient> recentEmergencyCases) { this.recentEmergencyCases = recentEmergencyCases; }

    public List<Alert> getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(List<Alert> activeAlerts) { this.activeAlerts = activeAlerts; }

    public Map<String, Long> getPriorityDistribution() { return priorityDistribution; }
    public void setPriorityDistribution(Map<String, Long> priorityDistribution) { this.priorityDistribution = priorityDistribution; }

    public Map<String, Long> getWardOccupancy() { return wardOccupancy; }
    public void setWardOccupancy(Map<String, Long> wardOccupancy) { this.wardOccupancy = wardOccupancy; }

    public BedForecastDTO getForecast() { return forecast; }
    public void setForecast(BedForecastDTO forecast) { this.forecast = forecast; }
}
