package com.hospital.triage.repository;

import com.hospital.triage.model.Staff;
import com.hospital.triage.model.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByEmployeeCode(String employeeCode);
    List<Staff> findByRole(StaffRole role);
    List<Staff> findByIsActiveTrue();
}
