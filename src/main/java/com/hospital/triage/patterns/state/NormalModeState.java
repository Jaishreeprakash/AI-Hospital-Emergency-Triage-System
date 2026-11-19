package com.hospital.triage.patterns.state;

import com.hospital.triage.model.HospitalMode;

public class NormalModeState implements HospitalModeState {

    @Override
    public HospitalMode getMode() {
        return HospitalMode.NORMAL;
    }

    @Override
    public HospitalModeState evaluateNextState(double occupancyPercentage, int urgentCasesLastHour) {
        if (occupancyPercentage >= 95.0 || urgentCasesLastHour >= 5) {
            return new SurgeModeState();
        }
        if (occupancyPercentage >= 80.0) {
            return new BusyModeState();
        }
        return this; // Stay in Normal
    }

    @Override
    public String getOperationalDirective() {
        return "Standard clinical operating procedures. Regular shift staffing and elective admissions permitted.";
    }
}
