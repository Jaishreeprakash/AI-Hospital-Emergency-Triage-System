package com.hospital.triage.patterns.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Concrete Subject in the OOAD Observer Pattern.
 * Manages registered observers and dispatches hospital events.
 */
@Component
public class HospitalEventSubject {

    private final List<HospitalEventObserver> observers = new CopyOnWriteArrayList<>();

    public void registerObserver(HospitalEventObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unregisterObserver(HospitalEventObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(HospitalEvent event) {
        for (HospitalEventObserver observer : observers) {
            try {
                observer.onHospitalEvent(event);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getObserverName() + ": " + e.getMessage());
            }
        }
    }

    public List<HospitalEventObserver> getObservers() {
        return observers;
    }
}
