package com.hospital.triage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "beds")
public class Bed {

    @Id
    @Column(name = "bed_id", length = 36)
    private String bedId;

    @Column(name = "bed_number", nullable = false, unique = true, length = 20)
    private String bedNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ward_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ward ward;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BedStatus status = BedStatus.AVAILABLE;

    @Column(name = "has_ventilator", nullable = false)
    private Boolean hasVentilator = false;

    @Column(name = "has_cardiac_monitor", nullable = false)
    private Boolean hasCardiacMonitor = false;

    @Column(name = "has_oxygen_supply", nullable = false)
    private Boolean hasOxygenSupply = true;

    @Column(name = "has_dialysis_port", nullable = false)
    private Boolean hasDialysisPort = false;

    @Column(name = "current_patient_id", length = 36)
    private String currentPatientId;

    @Column(name = "last_cleaned_at")
    private LocalDateTime lastCleanedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Bed() {}

    public Bed(String bedId, String bedNumber, Ward ward, BedStatus status, Boolean hasVentilator, Boolean hasCardiacMonitor, Boolean hasOxygenSupply, Boolean hasDialysisPort) {
        this.bedId = bedId;
        this.bedNumber = bedNumber;
        this.ward = ward;
        this.status = status;
        this.hasVentilator = hasVentilator;
        this.hasCardiacMonitor = hasCardiacMonitor;
        this.hasOxygenSupply = hasOxygenSupply;
        this.hasDialysisPort = hasDialysisPort;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getBedId() { return bedId; }
    public void setBedId(String bedId) { this.bedId = bedId; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }

    public BedStatus getStatus() { return status; }
    public void setStatus(BedStatus status) { this.status = status; }

    public Boolean getHasVentilator() { return hasVentilator; }
    public void setHasVentilator(Boolean hasVentilator) { this.hasVentilator = hasVentilator; }

    public Boolean getHasCardiacMonitor() { return hasCardiacMonitor; }
    public void setHasCardiacMonitor(Boolean hasCardiacMonitor) { this.hasCardiacMonitor = hasCardiacMonitor; }

    public Boolean getHasOxygenSupply() { return hasOxygenSupply; }
    public void setHasOxygenSupply(Boolean hasOxygenSupply) { this.hasOxygenSupply = hasOxygenSupply; }

    public Boolean getHasDialysisPort() { return hasDialysisPort; }
    public void setHasDialysisPort(Boolean hasDialysisPort) { this.hasDialysisPort = hasDialysisPort; }

    public String getCurrentPatientId() { return currentPatientId; }
    public void setCurrentPatientId(String currentPatientId) { this.currentPatientId = currentPatientId; }

    public LocalDateTime getLastCleanedAt() { return lastCleanedAt; }
    public void setLastCleanedAt(LocalDateTime lastCleanedAt) { this.lastCleanedAt = lastCleanedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
