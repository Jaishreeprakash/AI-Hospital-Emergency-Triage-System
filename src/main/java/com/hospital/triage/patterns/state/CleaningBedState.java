package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

import java.time.LocalDateTime;

public class CleaningBedState implements BedState {

    @Override
    public BedStatus getStatus() {
        return BedStatus.CLEANING;
    }

    @Override
    public void reserve(BedStateContext context, Bed bed, String patientId) {
        throw new IllegalStateException("Bed " + bed.getBedNumber() + " cannot be reserved: Sanitization in progress.");
    }

    @Override
    public void occupy(BedStateContext context, Bed bed, String patientId) {
        throw new IllegalStateException("Bed " + bed.getBedNumber() + " cannot be occupied: Sanitization in progress.");
    }

    @Override
    public void release(BedStateContext context, Bed bed) {
        // Cleaning completes and returns to available
        completeCleaning(context, bed);
    }

    @Override
    public void completeCleaning(BedStateContext context, Bed bed) {
        bed.setStatus(BedStatus.AVAILABLE);
        bed.setCurrentPatientId(null);
        bed.setLastCleanedAt(LocalDateTime.now());
        context.setState(new AvailableBedState());
    }
}
