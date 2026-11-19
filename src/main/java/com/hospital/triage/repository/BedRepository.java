package com.hospital.triage.repository;

import com.hospital.triage.model.Bed;
import com.hospital.triage.model.BedStatus;
import com.hospital.triage.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {
    Optional<Bed> findByBedNumber(String bedNumber);
    List<Bed> findByStatus(BedStatus status);
    List<Bed> findByWard(Ward ward);
    List<Bed> findByWard_WardId(String wardId);
    List<Bed> findByStatusAndWard_WardId(BedStatus status, String wardId);
    
    long countByStatus(BedStatus status);
    
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.status = 'OCCUPIED' OR b.status = 'RESERVED'")
    long countOccupiedAndReservedBeds();

    Optional<Bed> findByCurrentPatientId(String patientId);
}
