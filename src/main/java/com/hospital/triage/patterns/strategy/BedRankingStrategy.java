package com.hospital.triage.patterns.strategy;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.Patient;

import java.util.List;

/**
 * Strategy Pattern Interface for Bed Recommendation and Ranking algorithms.
 */
public interface BedRankingStrategy {
    String getStrategyName();
    List<BedCandidateScore> rankBedsForPatient(Patient patient, List<Bed> candidateBeds);
}
