package com.hospital.triage.patterns.state;

import com.hospital.triage.model.HospitalMode;

public class BusyModeState implements HospitalModeState {

    @Override
    public HospitalMode getMode() {
        return HospitalMode.BUSY;
    }

    @Override
    public HospitalModeState evaluateNextState(double occupancyPercentage, int urgentCasesLastHour) {
        if (occupancyPercentage >= 95.0 || urgentCasesLastHour >= 5) {
            return new SurgeModeState();
        }
        if (occupancyPercentage < 80.0 && urgentCasesLastHour < 3) {
            return new NormalModeState();
        }
        return this; // Stay in Busy
    }

    @Override
    public String getOperationalDirective() {
        return "Hospital occupancy >= 80%. Prioritize rapid discharge evaluations, expedite bed cleaning, alert on-call nursing.";
    }
}
