package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

public class ReservedBedState implements BedState {

    @Override
    public BedStatus getStatus() {
        return BedStatus.RESERVED;
    }

    @Override
    public void reserve(BedStateContext context, Bed bed, String patientId) {
        throw new IllegalStateException("Bed " + bed.getBedNumber() + " is already RESERVED for patient " + bed.getCurrentPatientId());
    }

    @Override
    public void occupy(BedStateContext context, Bed bed, String patientId) {
        bed.setStatus(BedStatus.OCCUPIED);
        bed.setCurrentPatientId(patientId);
        context.setState(new OccupiedBedState());
    }

    @Override
    public void release(BedStateContext context, Bed bed) {
        // Cancel reservation directly back to available
        bed.setStatus(BedStatus.AVAILABLE);
        bed.setCurrentPatientId(null);
        context.setState(new AvailableBedState());
    }

    @Override
    public void completeCleaning(BedStateContext context, Bed bed) {
        throw new IllegalStateException("Bed " + bed.getBedNumber() + " is currently RESERVED, not undergoing cleaning.");
    }
}
