-- ============================================================================
-- AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
-- CSA1104 - Object Oriented Analysis and Design (OOAD) Project
-- MySQL 8.0 Database Schema
-- ============================================================================

CREATE DATABASE IF NOT EXISTS hospital_triage_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE hospital_triage_db;

-- Drop tables in reverse foreign key order for clean recreation
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS mode_log;
DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS triage_records;
DROP TABLE IF EXISTS beds;
DROP TABLE IF EXISTS wards;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS staff;

-- ----------------------------------------------------------------------------
-- Table 1: staff
-- Stores hospital personnel (Receptionist, Nurse, Doctor, Administrator)
-- ----------------------------------------------------------------------------
CREATE TABLE staff (
    staff_id VARCHAR(36) PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('RECEPTIONIST', 'NURSE', 'DOCTOR', 'ADMINISTRATOR') NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_staff_role (role),
    INDEX idx_staff_code (employee_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 2: wards
-- Stores hospital ward information and capacity
-- ----------------------------------------------------------------------------
CREATE TABLE wards (
    ward_id VARCHAR(36) PRIMARY KEY,
    ward_code VARCHAR(20) NOT NULL UNIQUE,
    ward_name VARCHAR(100) NOT NULL,
    ward_type ENUM('ICU', 'EMERGENCY', 'GENERAL_MALE', 'GENERAL_FEMALE', 'PEDIATRIC') NOT NULL,
    total_beds INT NOT NULL DEFAULT 10,
    floor_number INT NOT NULL DEFAULT 1,
    wing VARCHAR(20) NOT NULL,
    distance_from_er_meters INT NOT NULL DEFAULT 50,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ward_type (ward_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 3: beds
-- Stores hospital bed statuses and assigned equipment
-- ----------------------------------------------------------------------------
CREATE TABLE beds (
    bed_id VARCHAR(36) PRIMARY KEY,
    bed_number VARCHAR(20) NOT NULL UNIQUE,
    ward_id VARCHAR(36) NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED', 'OCCUPIED', 'CLEANING') NOT NULL DEFAULT 'AVAILABLE',
    has_ventilator BOOLEAN NOT NULL DEFAULT FALSE,
    has_cardiac_monitor BOOLEAN NOT NULL DEFAULT FALSE,
    has_oxygen_supply BOOLEAN NOT NULL DEFAULT TRUE,
    has_dialysis_port BOOLEAN NOT NULL DEFAULT FALSE,
    current_patient_id VARCHAR(36) NULL,
    last_cleaned_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ward_id) REFERENCES wards(ward_id) ON DELETE RESTRICT,
    INDEX idx_bed_status (status),
    INDEX idx_bed_ward (ward_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 4: patients
-- Stores patient demographic and registration data
-- ----------------------------------------------------------------------------
CREATE TABLE patients (
    patient_id VARCHAR(36) PRIMARY KEY,
    patient_code VARCHAR(30) NOT NULL UNIQUE, -- E.g. PAT-2026-0001
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    emergency_contact VARCHAR(20),
    symptoms TEXT NOT NULL,
    current_priority ENUM('P1', 'P2', 'P3', 'P4') NOT NULL DEFAULT 'P4',
    current_status ENUM('WAITING', 'TRIAGED', 'BED_ASSIGNED', 'IN_TREATMENT', 'DISCHARGED') NOT NULL DEFAULT 'WAITING',
    assigned_bed_id VARCHAR(36) NULL,
    registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_bed_id) REFERENCES beds(bed_id) ON DELETE SET NULL,
    INDEX idx_patient_code (patient_code),
    INDEX idx_patient_priority (current_priority),
    INDEX idx_patient_status (current_status),
    INDEX idx_patient_reg_time (registration_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add foreign key back to patients for beds.current_patient_id
ALTER TABLE beds
ADD CONSTRAINT fk_beds_current_patient
FOREIGN KEY (current_patient_id) REFERENCES patients(patient_id) ON DELETE SET NULL;

-- ----------------------------------------------------------------------------
-- Table 5: triage_records
-- Stores objective vital sign scores, priority calculations & history
-- ----------------------------------------------------------------------------
CREATE TABLE triage_records (
    record_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    heart_rate INT NOT NULL,
    spo2 INT NOT NULL,
    systolic_bp INT NOT NULL,
    diastolic_bp INT NOT NULL,
    symptoms_description TEXT NOT NULL,
    
    -- Sub-score points (0 - 3 each)
    hr_points INT NOT NULL CHECK (hr_points BETWEEN 0 AND 3),
    spo2_points INT NOT NULL CHECK (spo2_points BETWEEN 0 AND 3),
    bp_points INT NOT NULL CHECK (bp_points BETWEEN 0 AND 3),
    symptom_points INT NOT NULL CHECK (symptom_points BETWEEN 0 AND 3),
    
    total_score INT NOT NULL CHECK (total_score BETWEEN 0 AND 12),
    calculated_priority ENUM('P1', 'P2', 'P3', 'P4') NOT NULL,
    clinical_explanation TEXT NOT NULL,
    assessment_type ENUM('INITIAL_REGISTRATION', 'ROUTINE_RECHECK', 'DOCTOR_OVERRIDE', 'URGENCY_ESCALATION') NOT NULL DEFAULT 'INITIAL_REGISTRATION',
    assessed_by_staff_id VARCHAR(36) NOT NULL,
    assessment_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (assessed_by_staff_id) REFERENCES staff(staff_id) ON DELETE RESTRICT,
    INDEX idx_triage_patient (patient_id),
    INDEX idx_triage_priority (calculated_priority),
    INDEX idx_triage_time (assessment_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 6: alerts
-- Stores hospital-wide and patient-specific emergency alerts
-- ----------------------------------------------------------------------------
CREATE TABLE alerts (
    alert_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NULL,
    alert_type ENUM(
        'CRITICAL_PATIENT',
        'PRIORITY_ESCALATED',
        'NO_SUITABLE_BED',
        'HOSPITAL_SURGE',
        'CAPACITY_WARNING',
        'NEW_REGISTRATION'
    ) NOT NULL,
    severity ENUM('CRITICAL', 'HIGH', 'MEDIUM', 'INFO') NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_by_staff_id VARCHAR(36) NULL,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE SET NULL,
    FOREIGN KEY (resolved_by_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    INDEX idx_alerts_resolved (is_resolved),
    INDEX idx_alerts_severity (severity),
    INDEX idx_alerts_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 7: audit_log
-- Immutable log for clinical decisions, doctor overrides and security actions
-- ----------------------------------------------------------------------------
CREATE TABLE audit_log (
    log_id VARCHAR(36) PRIMARY KEY,
    action_type VARCHAR(50) NOT NULL, -- E.g. 'DOCTOR_OVERRIDE', 'BED_ALLOCATION', 'MODE_TRANSITION'
    entity_name VARCHAR(50) NOT NULL, -- E.g. 'PATIENT', 'BED', 'HOSPITAL'
    entity_id VARCHAR(50) NOT NULL,
    staff_id VARCHAR(36) NULL,
    staff_name VARCHAR(100) NOT NULL,
    staff_role VARCHAR(30) NOT NULL,
    old_value TEXT NULL,
    new_value TEXT NULL,
    mandatory_reason TEXT NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    INDEX idx_audit_action (action_type),
    INDEX idx_audit_entity (entity_name, entity_id),
    INDEX idx_audit_timestamp (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table 8: mode_log
-- Tracks transitions between hospital modes (Normal, Busy, Surge)
-- ----------------------------------------------------------------------------
CREATE TABLE mode_log (
    mode_log_id VARCHAR(36) PRIMARY KEY,
    previous_mode ENUM('NORMAL', 'BUSY', 'SURGE') NOT NULL,
    new_mode ENUM('NORMAL', 'BUSY', 'SURGE') NOT NULL,
    occupancy_percentage DECIMAL(5,2) NOT NULL,
    urgent_cases_last_hour INT NOT NULL DEFAULT 0,
    trigger_reason VARCHAR(255) NOT NULL,
    triggered_by_staff_id VARCHAR(36) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (triggered_by_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    INDEX idx_modelog_mode (new_mode),
    INDEX idx_modelog_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
