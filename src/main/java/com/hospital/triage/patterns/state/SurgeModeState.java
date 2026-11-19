package com.hospital.triage.patterns.state;

import com.hospital.triage.model.HospitalMode;

public class SurgeModeState implements HospitalModeState {

    @Override
    public HospitalMode getMode() {
        return HospitalMode.SURGE;
    }

    @Override
    public HospitalModeState evaluateNextState(double occupancyPercentage, int urgentCasesLastHour) {
        if (occupancyPercentage < 80.0 && urgentCasesLastHour < 2) {
            return new NormalModeState();
        }
        if (occupancyPercentage < 95.0 && urgentCasesLastHour < 5) {
            return new BusyModeState();
        }
        return this; // Stay in Surge
    }

    @Override
    public String getOperationalDirective() {
        return "CRITICAL SURGE PROTOCOL: Emergency divert active for non-trauma arrivals. Convert step-down beds, mobilize emergency disaster response staff.";
    }
}
