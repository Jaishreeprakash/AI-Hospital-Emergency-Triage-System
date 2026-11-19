package com.hospital.triage;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;
import com.hospital.triage.model.Ward;
import com.hospital.triage.model.WardType;
import com.hospital.triage.patterns.state.BedStateContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOAD State Pattern - Bed Lifecycle State Tests")
public class BedStatePatternTest {

    private Bed bed;
    private BedStateContext context;

    @BeforeEach
    void setUp() {
        Ward ward = new Ward("w1", "ER", "ER Ward", WardType.EMERGENCY, 10, 1, "East", 20);
        bed = new Bed("bed-test", "ER-999", ward, BedStatus.AVAILABLE, false, true, true, false);
        context = new BedStateContext(bed);
    }

    @Test
    @DisplayName("Should transition from AVAILABLE -> RESERVED -> OCCUPIED -> CLEANING -> AVAILABLE")
    void testCompleteBedLifecycleTransitions() {
        // 1. Initial State: AVAILABLE
        assertEquals(BedStatus.AVAILABLE, bed.getStatus());

        // 2. Reserve bed for patient
        context.reserve(bed, "pat-001");
        assertEquals(BedStatus.RESERVED, bed.getStatus());
        assertEquals("pat-001", bed.getCurrentPatientId());

        // 3. Occupy bed
        context.occupy(bed, "pat-001");
        assertEquals(BedStatus.OCCUPIED, bed.getStatus());
        assertEquals("pat-001", bed.getCurrentPatientId());

        // 4. Release bed (Transitions to CLEANING)
        context.release(bed);
        assertEquals(BedStatus.CLEANING, bed.getStatus());
        assertNull(bed.getCurrentPatientId());

        // 5. Complete Cleaning (Transitions back to AVAILABLE)
        context.completeCleaning(bed);
        assertEquals(BedStatus.AVAILABLE, bed.getStatus());
        assertNull(bed.getCurrentPatientId());
    }

    @Test
    @DisplayName("Occupied bed should prevent double occupation via State Pattern exception")
    void testIllegalTransitionThrowsException() {
        context.occupy(bed, "pat-001");
        assertEquals(BedStatus.OCCUPIED, bed.getStatus());

        // Attempting to occupy an already occupied bed must throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> context.occupy(bed, "pat-002"));
    }
}
