package com.hospital.triage.service;

import com.hospital.triage.dto.BedForecastDTO;
import com.hospital.triage.model.BedStatus;
import com.hospital.triage.model.HospitalMode;
import com.hospital.triage.model.HospitalModeLog;
import com.hospital.triage.patterns.observer.HospitalEvent;
import com.hospital.triage.patterns.observer.HospitalEventSubject;
import com.hospital.triage.patterns.observer.HospitalEventType;
import com.hospital.triage.patterns.state.HospitalModeContext;
import com.hospital.triage.repository.BedRepository;
import com.hospital.triage.repository.HospitalModeLogRepository;
import com.hospital.triage.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HospitalModeService {

    private final BedRepository bedRepository;
    private final PatientRepository patientRepository;
    private final HospitalModeLogRepository modeLogRepository;
    private final HospitalEventSubject eventSubject;
    private final HospitalModeContext modeContext;

    @Value("${hospital.mode.busy-threshold:80.0}")
    private double busyThreshold = 80.0;

    @Value("${hospital.mode.surge-threshold:95.0}")
    private double surgeThreshold = 95.0;

    @Value("${hospital.mode.surge-urgent-cases-limit:5}")
    private int surgeUrgentLimit = 5;

    public HospitalModeService(BedRepository bedRepository,
                               PatientRepository patientRepository,
                               HospitalModeLogRepository modeLogRepository,
                               HospitalEventSubject eventSubject) {
        this.bedRepository = bedRepository;
        this.patientRepository = patientRepository;
        this.modeLogRepository = modeLogRepository;
        this.eventSubject = eventSubject;
        this.modeContext = new HospitalModeContext(HospitalMode.NORMAL);
    }

    public HospitalMode getCurrentMode() {
        evaluateAndSyncMode();
        return modeContext.getMode();
    }

    public String getCurrentDirective() {
        return modeContext.getOperationalDirective();
    }

    public synchronized HospitalMode evaluateAndSyncMode() {
        long totalBeds = bedRepository.count();
        if (totalBeds == 0) totalBeds = 50;

        long occupied = bedRepository.countOccupiedAndReservedBeds();
        double occupancyPercentage = ((double) occupied / totalBeds) * 100.0;
        occupancyPercentage = Math.round(occupancyPercentage * 10.0) / 10.0;

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int urgentCasesLastHour = patientRepository.countUrgentArrivalsSince(oneHourAgo);

        HospitalMode previousMode = modeContext.getMode();
        boolean transitioned = modeContext.updateMode(occupancyPercentage, urgentCasesLastHour);

        if (transitioned) {
            HospitalMode newMode = modeContext.getMode();
            String triggerReason = String.format("Occupancy at %.1f%% with %d urgent arrivals in past hour.",
                    occupancyPercentage, urgentCasesLastHour);

            HospitalModeLog log = new HospitalModeLog(
                    "mod-" + UUID.randomUUID().toString().substring(0, 8),
                    previousMode,
                    newMode,
                    occupancyPercentage,
                    urgentCasesLastHour,
                    triggerReason,
                    "sys"
            );
            modeLogRepository.save(log);

            // Notify via Observer Pattern
            eventSubject.notifyObservers(new HospitalEvent(
                    HospitalEventType.HOSPITAL_MODE_CHANGED,
                    "Hospital Operating Mode Changed: " + newMode.name(),
                    String.format("Hospital transitioned from %s to %s mode. %s", previousMode.name(), newMode.name(), triggerReason),
                    null,
                    newMode
            ));
        }

        return modeContext.getMode();
    }

    public BedForecastDTO calculateForecast(Integer expectedAdmissionsParam, Integer expectedDischargesParam) {
        long totalCapacity = bedRepository.count();
        if (totalCapacity == 0) totalCapacity = 60;

        long currentOccupied = bedRepository.countByStatus(BedStatus.OCCUPIED) +
                               bedRepository.countByStatus(BedStatus.RESERVED);

        double currentOccupancyPct = Math.round(((double) currentOccupied / totalCapacity) * 1000.0) / 10.0;

        // Realistic academic default: expected admissions based on waiting queue; discharges from occupied beds
        long waitingCount = patientRepository.countByCurrentStatus(com.hospital.triage.model.PatientStatus.WAITING);
        int expectedAdmissions = expectedAdmissionsParam != null ? expectedAdmissionsParam : (int) Math.max(4, waitingCount);
        int expectedDischarges = expectedDischargesParam != null ? expectedDischargesParam : (int) Math.max(2, currentOccupied / 8);

        long projectedOccupied = Math.max(0, currentOccupied + expectedAdmissions - expectedDischarges);
        projectedOccupied = Math.min(totalCapacity, projectedOccupied);

        double projectedOccupancyPct = Math.round(((double) projectedOccupied / totalCapacity) * 1000.0) / 10.0;

        BedForecastDTO forecast = new BedForecastDTO();
        forecast.setTotalCapacity(totalCapacity);
        forecast.setCurrentOccupiedBeds(currentOccupied);
        forecast.setCurrentOccupancyPercentage(currentOccupancyPct);
        forecast.setExpectedAdmissions(expectedAdmissions);
        forecast.setExpectedDischarges(expectedDischarges);
        forecast.setProjectedOccupiedBeds(projectedOccupied);
        forecast.setProjectedOccupancyPercentage(projectedOccupancyPct);

        boolean isWarning = projectedOccupancyPct >= busyThreshold;
        forecast.setCapacityWarning(isWarning);

        if (projectedOccupancyPct >= surgeThreshold) {
            forecast.setWarningMessage(String.format("CRITICAL: Projected occupancy reaches %.1f%% (>95%%). Surge protocol required within 4 hours.", projectedOccupancyPct));
        } else if (projectedOccupancyPct >= busyThreshold) {
            forecast.setWarningMessage(String.format("WARNING: Projected occupancy reaches %.1f%% (>=80%%). Expedited discharges recommended.", projectedOccupancyPct));
        } else {
            forecast.setWarningMessage("Capacity is within normal operational limits (<80%).");
        }

        // Generate 4-hour timeline intervals for Chart.js
        List<String> hourLabels = new ArrayList<>();
        List<Long> hourlyBeds = new ArrayList<>();
        List<Double> hourlyPcts = new ArrayList<>();

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();

        double stepBeds = (double) (projectedOccupied - currentOccupied) / 4.0;

        for (int h = 0; h <= 4; h++) {
            hourLabels.add(h == 0 ? "Now (" + now.format(timeFmt) + ")" : "+" + h + "h (" + now.plusHours(h).format(timeFmt) + ")");
            long bedsAtH = Math.round(currentOccupied + (stepBeds * h));
            bedsAtH = Math.max(0, Math.min(totalCapacity, bedsAtH));
            hourlyBeds.add(bedsAtH);
            hourlyPcts.add(Math.round(((double) bedsAtH / totalCapacity) * 1000.0) / 10.0);
        }

        forecast.setHourLabels(hourLabels);
        forecast.setHourlyProjectedBeds(hourlyBeds);
        forecast.setHourlyOccupancyPercentages(hourlyPcts);

        return forecast;
    }

    public List<HospitalModeLog> getModeHistory() {
        return modeLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
