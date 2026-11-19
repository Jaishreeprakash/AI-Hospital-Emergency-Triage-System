package com.hospital.triage.patterns.observer;

/**
 * Observer Pattern Interface for hospital-wide event monitoring.
 */
public interface HospitalEventObserver {
    String getObserverName();
    void onHospitalEvent(HospitalEvent event);
}
