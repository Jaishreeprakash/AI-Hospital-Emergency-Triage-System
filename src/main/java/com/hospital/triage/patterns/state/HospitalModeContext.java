package com.hospital.triage.patterns.state;

import com.hospital.triage.model.HospitalMode;

public class HospitalModeContext {
    private HospitalModeState currentState;

    public HospitalModeContext(HospitalMode initialMode) {
        this.currentState = stateForMode(initialMode);
    }

    public static HospitalModeState stateForMode(HospitalMode mode) {
        if (mode == null) return new NormalModeState();
        switch (mode) {
            case NORMAL: return new NormalModeState();
            case BUSY: return new BusyModeState();
            case SURGE: return new SurgeModeState();
            default: return new NormalModeState();
        }
    }

    public HospitalMode getMode() {
        return currentState.getMode();
    }

    public HospitalModeState getState() {
        return currentState;
    }

    public boolean updateMode(double occupancyPercentage, int urgentCasesLastHour) {
        HospitalModeState nextState = currentState.evaluateNextState(occupancyPercentage, urgentCasesLastHour);
        if (nextState.getMode() != currentState.getMode()) {
            this.currentState = nextState;
            return true; // Mode transitioned
        }
        return false;
    }

    public String getOperationalDirective() {
        return currentState.getOperationalDirective();
    }
}
