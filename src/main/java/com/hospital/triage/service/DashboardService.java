package com.hospital.triage.service;

import com.hospital.triage.dto.DashboardStatsDTO;
import com.hospital.triage.model.*;
import com.hospital.triage.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final WardRepository wardRepository;
    private final AlertRepository alertRepository;
    private final HospitalModeService hospitalModeService;

    public DashboardService(PatientRepository patientRepository,
                            BedRepository bedRepository,
                            WardRepository wardRepository,
                            AlertRepository alertRepository,
                            HospitalModeService hospitalModeService) {
        this.patientRepository = patientRepository;
        this.bedRepository = bedRepository;
        this.wardRepository = wardRepository;
        this.alertRepository = alertRepository;
        this.hospitalModeService = hospitalModeService;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        long totalPatients = patientRepository.count();
        long p1Count = patientRepository.countByCurrentPriority(TriagePriority.P1);
        long p2Count = patientRepository.countByCurrentPriority(TriagePriority.P2);
        long p3Count = patientRepository.countByCurrentPriority(TriagePriority.P3);
        long p4Count = patientRepository.countByCurrentPriority(TriagePriority.P4);
        long waitingCount = patientRepository.countByCurrentStatus(PatientStatus.WAITING);

        long totalBeds = bedRepository.count();
        if (totalBeds == 0) totalBeds = 60;

        long availableBeds = bedRepository.countByStatus(BedStatus.AVAILABLE);
        long occupiedBeds = bedRepository.countByStatus(BedStatus.OCCUPIED);
        long reservedBeds = bedRepository.countByStatus(BedStatus.RESERVED);
        long cleaningBeds = bedRepository.countByStatus(BedStatus.CLEANING);

        double occupancyPct = Math.round(((double) (occupiedBeds + reservedBeds) / totalBeds) * 1000.0) / 10.0;

        HospitalMode currentMode = hospitalModeService.getCurrentMode();
        String directive = hospitalModeService.getCurrentDirective();

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int urgentLastHour = patientRepository.countUrgentArrivalsSince(oneHourAgo);

        List<Patient> recentCases = patientRepository.findAll().stream()
                .sorted((a, b) -> b.getRegistrationTime().compareTo(a.getRegistrationTime()))
                .limit(6)
                .toList();

        List<Alert> activeAlerts = alertRepository.findByIsResolvedFalseOrderByCreatedAtDesc();

        Map<String, Long> priorityDist = new HashMap<>();
        priorityDist.put("P1_CRITICAL", p1Count);
        priorityDist.put("P2_HIGH", p2Count);
        priorityDist.put("P3_MODERATE", p3Count);
        priorityDist.put("P4_LOW", p4Count);

        Map<String, Long> wardOcc = new HashMap<>();
        for (Ward ward : wardRepository.findAll()) {
            long occupiedInWard = bedRepository.findByStatusAndWard_WardId(BedStatus.OCCUPIED, ward.getWardId()).size() +
                                  bedRepository.findByStatusAndWard_WardId(BedStatus.RESERVED, ward.getWardId()).size();
            wardOcc.put(ward.getWardName(), occupiedInWard);
        }

        stats.setTotalPatients(totalPatients);
        stats.setCriticalPatientsCount(p1Count);
        stats.setHighPriorityPatientsCount(p2Count);
        stats.setModeratePatientsCount(p3Count);
        stats.setLowPriorityPatientsCount(p4Count);
        stats.setWaitingQueueCount(waitingCount);
        stats.setTotalBeds(totalBeds);
        stats.setAvailableBeds(availableBeds);
        stats.setOccupiedBeds(occupiedBeds);
        stats.setReservedBeds(reservedBeds);
        stats.setCleaningBeds(cleaningBeds);
        stats.setOccupancyPercentage(occupancyPct);
        stats.setCurrentHospitalMode(currentMode);
        stats.setModeDirective(directive);
        stats.setUrgentCasesLastHour(urgentLastHour);
        stats.setRecentEmergencyCases(recentCases);
        stats.setActiveAlerts(activeAlerts);
        stats.setPriorityDistribution(priorityDist);
        stats.setWardOccupancy(wardOcc);
        stats.setForecast(hospitalModeService.calculateForecast(null, null));

        return stats;
    }
}
