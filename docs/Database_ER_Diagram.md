# Database Entity-Relationship (ER) Diagram
## AI-Enabled Hospital Resource and Emergency Triage System
**Course:** CSA1104 – Object Oriented Analysis and Design (OOAD)

---

### 1. Database Architecture
The relational database schema is designed for MySQL 8.0, fully normalized to Third Normal Form (3NF), ensuring ACID compliance, relational integrity, foreign key cascading constraints, and optimized indexing for emergency queries.

The architecture comprises eight core tables:
1. `staff`: Personnel credentials and Role-Based Access Control (RBAC).
2. `wards`: Hospital units, physical layout, and ER distances.
3. `beds`: Sanitization states, lifecycle, and specialized equipment telemetry.
4. `patients`: Demographics, chief complaints, current urgency priority, and admission state.
5. `triage_records`: Objective vital sign scores, mathematical factor breakdowns, and historical triage assessments.
6. `alerts`: Clinical alarms, deterioration alerts, and capacity threshold notifications.
7. `audit_log`: Immutable forensic trail for doctor overrides, status transitions, and administrative actions.
8. `mode_log`: History of operational hospital mode transitions (Normal $\leftrightarrow$ Busy $\leftrightarrow$ Surge).

---

### 2. Mermaid ER Diagram

```mermaid
erDiagram
    STAFF ||--o{ TRIAGE_RECORDS : "assesses"
    STAFF ||--o{ ALERTS : "resolves"
    STAFF ||--o{ AUDIT_LOG : "executes"
    STAFF ||--o{ MODE_LOG : "triggers"

    WARDS ||--|{ BEDS : "contains"
    
    PATIENTS ||--o{ TRIAGE_RECORDS : "evaluated in"
    PATIENTS ||--o{ ALERTS : "subject of"
    PATIENTS ||--o| BEDS : "allocated to"
    BEDS ||--o| PATIENTS : "currently occupies"

    STAFF {
        varchar(36) staff_id PK
        varchar(20) employee_code UK
        varchar(100) full_name
        enum role "RECEPTIONIST, NURSE, DOCTOR, ADMINISTRATOR"
        varchar(50) department
        varchar(100) email UK
        varchar(255) password_hash
        varchar(20) contact_phone
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    WARDS {
        varchar(36) ward_id PK
        varchar(20) ward_code UK
        varchar(100) ward_name
        enum ward_type "ICU, EMERGENCY, GENERAL_MALE, GENERAL_FEMALE, PEDIATRIC"
        int total_beds
        int floor_number
        varchar(20) wing
        int distance_from_er_meters
        timestamp created_at
        timestamp updated_at
    }

    BEDS {
        varchar(36) bed_id PK
        varchar(20) bed_number UK
        varchar(36) ward_id FK
        enum status "AVAILABLE, RESERVED, OCCUPIED, CLEANING"
        boolean has_ventilator
        boolean has_cardiac_monitor
        boolean has_oxygen_supply
        boolean has_dialysis_port
        varchar(36) current_patient_id FK
        timestamp last_cleaned_at
        timestamp created_at
        timestamp updated_at
    }

    PATIENTS {
        varchar(36) patient_id PK
        varchar(30) patient_code UK
        varchar(100) full_name
        int age
        enum gender "MALE, FEMALE, OTHER"
        varchar(20) contact_number
        varchar(20) emergency_contact
        text symptoms
        enum current_priority "P1, P2, P3, P4"
        enum current_status "WAITING, TRIAGED, BED_ASSIGNED, IN_TREATMENT, DISCHARGED"
        varchar(36) assigned_bed_id FK
        timestamp registration_time
        timestamp created_at
        timestamp updated_at
    }

    TRIAGE_RECORDS {
        varchar(36) record_id PK
        varchar(36) patient_id FK
        int heart_rate
        int spo2
        int systolic_bp
        int diastolic_bp
        text symptoms_description
        int hr_points
        int spo2_points
        int bp_points
        int symptom_points
        int total_score
        enum calculated_priority "P1, P2, P3, P4"
        text clinical_explanation
        enum assessment_type "INITIAL_REGISTRATION, ROUTINE_RECHECK, DOCTOR_OVERRIDE, URGENCY_ESCALATION"
        varchar(36) assessed_by_staff_id FK
        timestamp assessment_time
    }

    ALERTS {
        varchar(36) alert_id PK
        varchar(36) patient_id FK
        enum alert_type "CRITICAL_PATIENT, PRIORITY_ESCALATED, NO_SUITABLE_BED, HOSPITAL_SURGE, CAPACITY_WARNING, NEW_REGISTRATION"
        enum severity "CRITICAL, HIGH, MEDIUM, INFO"
        varchar(150) title
        text message
        boolean is_resolved
        varchar(36) resolved_by_staff_id FK
        timestamp resolved_at
        timestamp created_at
    }

    AUDIT_LOG {
        varchar(36) log_id PK
        varchar(50) action_type
        varchar(50) entity_name
        varchar(50) entity_id
        varchar(36) staff_id FK
        varchar(100) staff_name
        varchar(30) staff_role
        text old_value
        text new_value
        text mandatory_reason
        varchar(45) ip_address
        timestamp created_at
    }

    MODE_LOG {
        varchar(36) mode_log_id PK
        enum previous_mode "NORMAL, BUSY, SURGE"
        enum new_mode "NORMAL, BUSY, SURGE"
        decimal occupancy_percentage
        int urgent_cases_last_hour
        varchar(255) trigger_reason
        varchar(36) triggered_by_staff_id FK
        timestamp created_at
    }
```

---

### 3. Relational Schema & Constraints Summary

| Table | Primary Key | Foreign Keys | Key Constraints & Validation |
| :--- | :--- | :--- | :--- |
| `staff` | `staff_id` | — | `UNIQUE(employee_code)`, `UNIQUE(email)`, `role IN ('RECEPTIONIST', 'NURSE', 'DOCTOR', 'ADMINISTRATOR')` |
| `wards` | `ward_id` | — | `UNIQUE(ward_code)`, `total_beds > 0`, `distance_from_er_meters >= 0` |
| `beds` | `bed_id` | `ward_id` $\to$ `wards.ward_id`, `current_patient_id` $\to$ `patients.patient_id` | `UNIQUE(bed_number)`, `status IN ('AVAILABLE', 'RESERVED', 'OCCUPIED', 'CLEANING')` |
| `patients` | `patient_id` | `assigned_bed_id` $\to$ `beds.bed_id` | `UNIQUE(patient_code)`, `age BETWEEN 0 AND 130`, `current_priority IN ('P1','P2','P3','P4')` |
| `triage_records` | `record_id` | `patient_id` $\to$ `patients.patient_id`, `assessed_by_staff_id` $\to$ `staff.staff_id` | `hr_points, spo2_points, bp_points, symptom_points BETWEEN 0 AND 3`, `total_score BETWEEN 0 AND 12` |
| `alerts` | `alert_id` | `patient_id` $\to$ `patients.patient_id`, `resolved_by_staff_id` $\to$ `staff.staff_id` | `severity IN ('CRITICAL', 'HIGH', 'MEDIUM', 'INFO')` |
| `audit_log` | `log_id` | `staff_id` $\to$ `staff.staff_id` | Immutable append-only log; captures doctor overrides and state changes with mandatory reason |
| `mode_log` | `mode_log_id` | `triggered_by_staff_id` $\to$ `staff.staff_id` | Captures occupancy %, urgent arrival count, and trigger reason |
