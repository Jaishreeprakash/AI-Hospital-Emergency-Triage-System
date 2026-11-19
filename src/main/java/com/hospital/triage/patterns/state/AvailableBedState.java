package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

public class AvailableBedState implements BedState {

    @Override
    public BedStatus getStatus() {
        return BedStatus.AVAILABLE;
    }

    @Override
    public void reserve(BedStateContext context, Bed bed, String patientId) {
        bed.setStatus(BedStatus.RESERVED);
        bed.setCurrentPatientId(patientId);
        context.setState(new ReservedBedState());
    }

    @Override
    public void occupy(BedStateContext context, Bed bed, String patientId) {
        bed.setStatus(BedStatus.OCCUPIED);
        bed.setCurrentPatientId(patientId);
        context.setState(new OccupiedBedState());
    }

    @Override
    public void release(BedStateContext context, Bed bed) {
        throw new IllegalStateException("Cannot release bed " + bed.getBedNumber() + ": Bed is already AVAILABLE.");
    }

    @Override
    public void completeCleaning(BedStateContext context, Bed bed) {
        // Bed is already cleaned and available
    }
}
