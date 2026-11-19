-- ============================================================================
-- AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
-- Spring Boot Classpath Schema
-- ============================================================================

DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS mode_log;
DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS triage_records;
DROP TABLE IF EXISTS beds;
DROP TABLE IF EXISTS wards;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS staff;

CREATE TABLE staff (
    staff_id VARCHAR(36) PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wards (
    ward_id VARCHAR(36) PRIMARY KEY,
    ward_code VARCHAR(20) NOT NULL UNIQUE,
    ward_name VARCHAR(100) NOT NULL,
    ward_type VARCHAR(30) NOT NULL,
    total_beds INT NOT NULL DEFAULT 10,
    floor_number INT NOT NULL DEFAULT 1,
    wing VARCHAR(20) NOT NULL,
    distance_from_er_meters INT NOT NULL DEFAULT 50,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE beds (
    bed_id VARCHAR(36) PRIMARY KEY,
    bed_number VARCHAR(20) NOT NULL UNIQUE,
    ward_id VARCHAR(36) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    has_ventilator BOOLEAN NOT NULL DEFAULT FALSE,
    has_cardiac_monitor BOOLEAN NOT NULL DEFAULT FALSE,
    has_oxygen_supply BOOLEAN NOT NULL DEFAULT TRUE,
    has_dialysis_port BOOLEAN NOT NULL DEFAULT FALSE,
    current_patient_id VARCHAR(36) NULL,
    last_cleaned_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ward_id) REFERENCES wards(ward_id)
);

CREATE TABLE patients (
    patient_id VARCHAR(36) PRIMARY KEY,
    patient_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    emergency_contact VARCHAR(20),
    symptoms TEXT NOT NULL,
    current_priority VARCHAR(10) NOT NULL DEFAULT 'P4',
    current_status VARCHAR(30) NOT NULL DEFAULT 'WAITING',
    assigned_bed_id VARCHAR(36) NULL,
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_bed_id) REFERENCES beds(bed_id)
);

CREATE TABLE triage_records (
    record_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    heart_rate INT NOT NULL,
    spo2 INT NOT NULL,
    systolic_bp INT NOT NULL,
    diastolic_bp INT NOT NULL,
    symptoms_description TEXT NOT NULL,
    hr_points INT NOT NULL,
    spo2_points INT NOT NULL,
    bp_points INT NOT NULL,
    symptom_points INT NOT NULL,
    total_score INT NOT NULL,
    calculated_priority VARCHAR(10) NOT NULL,
    clinical_explanation TEXT NOT NULL,
    assessment_type VARCHAR(30) NOT NULL DEFAULT 'INITIAL_REGISTRATION',
    assessed_by_staff_id VARCHAR(36) NOT NULL,
    assessment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (assessed_by_staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE alerts (
    alert_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_by_staff_id VARCHAR(36) NULL,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (resolved_by_staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE audit_log (
    log_id VARCHAR(36) PRIMARY KEY,
    action_type VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    staff_id VARCHAR(36) NULL,
    staff_name VARCHAR(100) NOT NULL,
    staff_role VARCHAR(30) NOT NULL,
    old_value TEXT NULL,
    new_value TEXT NULL,
    mandatory_reason TEXT NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE mode_log (
    mode_log_id VARCHAR(36) PRIMARY KEY,
    previous_mode VARCHAR(20) NOT NULL,
    new_mode VARCHAR(20) NOT NULL,
    occupancy_percentage DECIMAL(5,2) NOT NULL,
    urgent_cases_last_hour INT NOT NULL DEFAULT 0,
    trigger_reason VARCHAR(255) NOT NULL,
    triggered_by_staff_id VARCHAR(36) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (triggered_by_staff_id) REFERENCES staff(staff_id)
);
