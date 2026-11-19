package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

import java.time.LocalDateTime;

public class OccupiedBedState implements BedState {

    @Override
    public BedStatus getStatus() {
        return BedStatus.OCCUPIED;
    }

    @Override
    public void reserve(BedStateContext context, Bed bed, String patientId) {
        throw new IllegalStateException("Cannot reserve bed " + bed.getBedNumber() + ": Bed is currently OCCUPIED.");
    }

    @Override
    public void occupy(BedStateContext context, Bed bed, String patientId) {
        throw new IllegalStateException("Bed " + bed.getBedNumber() + " is already OCCUPIED by patient " + bed.getCurrentPatientId());
    }

    @Override
    public void release(BedStateContext context, Bed bed) {
        // Discharging patient sends bed into CLEANING protocol
        bed.setStatus(BedStatus.CLEANING);
        bed.setCurrentPatientId(null);
        bed.setLastCleanedAt(LocalDateTime.now());
        context.setState(new CleaningBedState());
    }

    @Override
    public void completeCleaning(BedStateContext context, Bed bed) {
        throw new IllegalStateException("Cannot clean bed " + bed.getBedNumber() + " while it is OCCUPIED by a patient.");
    }
}
