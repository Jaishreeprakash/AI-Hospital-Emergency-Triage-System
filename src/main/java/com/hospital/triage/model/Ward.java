package com.hospital.triage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wards")
public class Ward {

    @Id
    @Column(name = "ward_id", length = 36)
    private String wardId;

    @Column(name = "ward_code", nullable = false, unique = true, length = 20)
    private String wardCode;

    @Column(name = "ward_name", nullable = false, length = 100)
    private String wardName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ward_type", nullable = false, length = 30)
    private WardType wardType;

    @Column(name = "total_beds", nullable = false)
    private Integer totalBeds = 10;

    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber = 1;

    @Column(name = "wing", nullable = false, length = 20)
    private String wing;

    @Column(name = "distance_from_er_meters", nullable = false)
    private Integer distanceFromErMeters = 50;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Ward() {}

    public Ward(String wardId, String wardCode, String wardName, WardType wardType, Integer totalBeds, Integer floorNumber, String wing, Integer distanceFromErMeters) {
        this.wardId = wardId;
        this.wardCode = wardCode;
        this.wardName = wardName;
        this.wardType = wardType;
        this.totalBeds = totalBeds;
        this.floorNumber = floorNumber;
        this.wing = wing;
        this.distanceFromErMeters = distanceFromErMeters;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getWardId() { return wardId; }
    public void setWardId(String wardId) { this.wardId = wardId; }

    public String getWardCode() { return wardCode; }
    public void setWardCode(String wardCode) { this.wardCode = wardCode; }

    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }

    public WardType getWardType() { return wardType; }
    public void setWardType(WardType wardType) { this.wardType = wardType; }

    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getWing() { return wing; }
    public void setWing(String wing) { this.wing = wing; }

    public Integer getDistanceFromErMeters() { return distanceFromErMeters; }
    public void setDistanceFromErMeters(Integer distanceFromErMeters) { this.distanceFromErMeters = distanceFromErMeters; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
