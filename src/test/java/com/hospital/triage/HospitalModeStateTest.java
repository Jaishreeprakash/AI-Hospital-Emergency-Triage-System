package com.hospital.triage;

import com.hospital.triage.model.HospitalMode;
import com.hospital.triage.patterns.state.HospitalModeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOAD State Pattern - Hospital Operating Mode State Tests")
public class HospitalModeStateTest {

    private HospitalModeContext context;

    @BeforeEach
    void setUp() {
        context = new HospitalModeContext(HospitalMode.NORMAL);
    }

    @Test
    @DisplayName("Occupancy below 80% should remain in NORMAL mode")
    void testNormalMode() {
        assertEquals(HospitalMode.NORMAL, context.getMode());
        boolean transitioned = context.updateMode(75.0, 1);
        assertFalse(transitioned);
        assertEquals(HospitalMode.NORMAL, context.getMode());
    }

    @Test
    @DisplayName("Occupancy reaching 80% should transition from NORMAL to BUSY mode")
    void testTransitionToBusyMode() {
        boolean transitioned = context.updateMode(80.0, 2);
        assertTrue(transitioned);
        assertEquals(HospitalMode.BUSY, context.getMode());
        assertTrue(context.getOperationalDirective().contains("expedite bed cleaning"));
    }

    @Test
    @DisplayName("Occupancy reaching 95% should transition to SURGE mode")
    void testTransitionToSurgeByOccupancy() {
        boolean transitioned = context.updateMode(95.0, 2);
        assertTrue(transitioned);
        assertEquals(HospitalMode.SURGE, context.getMode());
        assertTrue(context.getOperationalDirective().contains("CRITICAL SURGE PROTOCOL"));
    }

    @Test
    @DisplayName("Five urgent cases in one hour should trigger SURGE mode even if occupancy < 95%")
    void testTransitionToSurgeByUrgentArrivals() {
        // Occupancy is 78%, but 5 urgent arrivals occur
        boolean transitioned = context.updateMode(78.0, 5);
        assertTrue(transitioned);
        assertEquals(HospitalMode.SURGE, context.getMode());
    }
}
