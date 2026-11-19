package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

public class BedStateContext {
    private BedState currentState;

    public BedStateContext(Bed bed) {
        this.currentState = stateForStatus(bed.getStatus());
    }

    public static BedState stateForStatus(BedStatus status) {
        if (status == null) return new AvailableBedState();
        switch (status) {
            case AVAILABLE: return new AvailableBedState();
            case RESERVED: return new ReservedBedState();
            case OCCUPIED: return new OccupiedBedState();
            case CLEANING: return new CleaningBedState();
            default: return new AvailableBedState();
        }
    }

    public void setState(BedState state) {
        this.currentState = state;
    }

    public BedState getState() {
        return currentState;
    }

    public void reserve(Bed bed, String patientId) {
        currentState.reserve(this, bed, patientId);
    }

    public void occupy(Bed bed, String patientId) {
        currentState.occupy(this, bed, patientId);
    }

    public void release(Bed bed) {
        currentState.release(this, bed);
    }

    public void completeCleaning(Bed bed) {
        currentState.completeCleaning(this, bed);
    }
}
