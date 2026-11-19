package com.hospital.triage.repository;

import com.hospital.triage.model.Ward;
import com.hospital.triage.model.WardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    Optional<Ward> findByWardCode(String wardCode);
    List<Ward> findByWardType(WardType wardType);
}
