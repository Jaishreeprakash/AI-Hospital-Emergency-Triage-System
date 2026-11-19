package com.hospital.triage.patterns.state;

import com.hospital.triage.model.HospitalMode;

/**
 * State Pattern Interface for Hospital Operational Modes.
 * Normal: Occupancy < 80%
 * Busy: Occupancy >= 80% and < 95%
 * Surge: Occupancy >= 95% OR 5 urgent cases within 1 hour
 */
public interface HospitalModeState {
    HospitalMode getMode();
    HospitalModeState evaluateNextState(double occupancyPercentage, int urgentCasesLastHour);
    String getOperationalDirective();
}
