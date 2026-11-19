package com.hospital.triage.patterns.state;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;

/**
 * State Pattern Interface for Bed Status Management.
 * Enforces valid lifecycle transitions:
 * AVAILABLE -> RESERVED -> OCCUPIED -> CLEANING -> AVAILABLE
 */
public interface BedState {
    BedStatus getStatus();
    void reserve(BedStateContext context, Bed bed, String patientId);
    void occupy(BedStateContext context, Bed bed, String patientId);
    void release(BedStateContext context, Bed bed);
    void completeCleaning(BedStateContext context, Bed bed);
}
