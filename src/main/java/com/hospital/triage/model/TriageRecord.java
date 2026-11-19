package com.hospital.triage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "triage_records")
public class TriageRecord {

    @Id
    @Column(name = "record_id", length = 36)
    private String recordId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Patient patient;

    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate;

    @Column(name = "spo2", nullable = false)
    private Integer spo2;

    @Column(name = "systolic_bp", nullable = false)
    private Integer systolicBp;

    @Column(name = "diastolic_bp", nullable = false)
    private Integer diastolicBp;

    @Column(name = "symptoms_description", nullable = false, columnDefinition = "TEXT")
    private String symptomsDescription;

    @Column(name = "hr_points", nullable = false)
    private Integer hrPoints;

    @Column(name = "spo2_points", nullable = false)
    private Integer spo2Points;

    @Column(name = "bp_points", nullable = false)
    private Integer bpPoints;

    @Column(name = "symptom_points", nullable = false)
    private Integer symptomPoints;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculated_priority", nullable = false, length = 10)
    private TriagePriority calculatedPriority;

    @Column(name = "clinical_explanation", nullable = false, columnDefinition = "TEXT")
    private String clinicalExplanation;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false, length = 30)
    private AssessmentType assessmentType = AssessmentType.INITIAL_REGISTRATION;

    @Column(name = "assessed_by_staff_id", nullable = false, length = 36)
    private String assessedByStaffId;

    @Column(name = "assessment_time", updatable = false)
    private LocalDateTime assessmentTime = LocalDateTime.now();

    public TriageRecord() {}

    public TriageRecord(String recordId, Patient patient, Integer heartRate, Integer spo2, Integer systolicBp, Integer diastolicBp, String symptomsDescription, Integer hrPoints, Integer spo2Points, Integer bpPoints, Integer symptomPoints, Integer totalScore, TriagePriority calculatedPriority, String clinicalExplanation, AssessmentType assessmentType, String assessedByStaffId) {
        this.recordId = recordId;
        this.patient = patient;
        this.heartRate = heartRate;
        this.spo2 = spo2;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.symptomsDescription = symptomsDescription;
        this.hrPoints = hrPoints;
        this.spo2Points = spo2Points;
        this.bpPoints = bpPoints;
        this.symptomPoints = symptomPoints;
        this.totalScore = totalScore;
        this.calculatedPriority = calculatedPriority;
        this.clinicalExplanation = clinicalExplanation;
        this.assessmentType = assessmentType;
        this.assessedByStaffId = assessedByStaffId;
        this.assessmentTime = LocalDateTime.now();
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public String getSymptomsDescription() { return symptomsDescription; }
    public void setSymptomsDescription(String symptomsDescription) { this.symptomsDescription = symptomsDescription; }

    public Integer getHrPoints() { return hrPoints; }
    public void setHrPoints(Integer hrPoints) { this.hrPoints = hrPoints; }

    public Integer getSpo2Points() { return spo2Points; }
    public void setSpo2Points(Integer spo2Points) { this.spo2Points = spo2Points; }

    public Integer getBpPoints() { return bpPoints; }
    public void setBpPoints(Integer bpPoints) { this.bpPoints = bpPoints; }

    public Integer getSymptomPoints() { return symptomPoints; }
    public void setSymptomPoints(Integer symptomPoints) { this.symptomPoints = symptomPoints; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public TriagePriority getCalculatedPriority() { return calculatedPriority; }
    public void setCalculatedPriority(TriagePriority calculatedPriority) { this.calculatedPriority = calculatedPriority; }

    public String getClinicalExplanation() { return clinicalExplanation; }
    public void setClinicalExplanation(String clinicalExplanation) { this.clinicalExplanation = clinicalExplanation; }

    public AssessmentType getAssessmentType() { return assessmentType; }
    public void setAssessmentType(AssessmentType assessmentType) { this.assessmentType = assessmentType; }

    public String getAssessedByStaffId() { return assessedByStaffId; }
    public void setAssessedByStaffId(String assessedByStaffId) { this.assessedByStaffId = assessedByStaffId; }

    public LocalDateTime getAssessmentTime() { return assessmentTime; }
    public void setAssessmentTime(LocalDateTime assessmentTime) { this.assessmentTime = assessmentTime; }
}
