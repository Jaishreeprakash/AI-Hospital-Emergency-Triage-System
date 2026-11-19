-- ============================================================================
-- AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
-- Initial Demonstration Dataset
-- ============================================================================

-- Staff Members
INSERT INTO staff (staff_id, employee_code, full_name, role, department, email, password_hash, contact_phone, is_active) VALUES
('stf-001', 'REC-101', 'Sarah Jenkins', 'RECEPTIONIST', 'Patient Admission', 'sarah.reception@hospital.org', '$2a$10$7Q1kY0jKx9b5yN5rB4P9Te9j6iC5mK.w1c8L4F6fQv8y6.9O3tTte', '+1-555-0101', TRUE),
('stf-002', 'NUR-201', 'Elena Rostova', 'NURSE', 'Emergency Department', 'elena.nurse@hospital.org', '$2a$10$7Q1kY0jKx9b5yN5rB4P9Te9j6iC5mK.w1c8L4F6fQv8y6.9O3tTte', '+1-555-0102', TRUE),
('stf-003', 'DOC-301', 'Dr. Marcus Vance, MD', 'DOCTOR', 'Emergency Medicine / Trauma', 'marcus.doctor@hospital.org', '$2a$10$7Q1kY0jKx9b5yN5rB4P9Te9j6iC5mK.w1c8L4F6fQv8y6.9O3tTte', '+1-555-0103', TRUE),
('stf-004', 'DOC-302', 'Dr. Priya Sharma, MD', 'DOCTOR', 'Critical Care & Cardiology', 'priya.doctor@hospital.org', '$2a$10$7Q1kY0jKx9b5yN5rB4P9Te9j6iC5mK.w1c8L4F6fQv8y6.9O3tTte', '+1-555-0104', TRUE),
('stf-005', 'ADM-401', 'Director Arthur Pendelton', 'ADMINISTRATOR', 'Hospital Operations', 'admin@hospital.org', '$2a$10$7Q1kY0jKx9b5yN5rB4P9Te9j6iC5mK.w1c8L4F6fQv8y6.9O3tTte', '+1-555-0105', TRUE);

-- Wards
INSERT INTO wards (ward_id, ward_code, ward_name, ward_type, total_beds, floor_number, wing, distance_from_er_meters) VALUES
('wrd-001', 'ER-BAY', 'Emergency Resuscitation Unit', 'EMERGENCY', 12, 1, 'East Wing Ground', 15),
('wrd-002', 'ICU-CRIT', 'Intensive Care Unit (Critical)', 'ICU', 10, 2, 'North Wing Tower', 45),
('wrd-003', 'GEN-MALE', 'General Medicine - Male Ward', 'GENERAL_MALE', 14, 3, 'South Wing Block A', 85),
('wrd-004', 'GEN-FEM', 'General Medicine - Female Ward', 'GENERAL_FEMALE', 14, 3, 'South Wing Block B', 90),
('wrd-005', 'PED-CARE', 'Pediatric Emergency & Inpatient', 'PEDIATRIC', 10, 2, 'West Wing Children', 60);

-- Beds (60 total beds)
-- Emergency Ward Beds
INSERT INTO beds (bed_id, bed_number, ward_id, status, has_ventilator, has_cardiac_monitor, has_oxygen_supply, has_dialysis_port) VALUES
('bed-er-01', 'ER-101', 'wrd-001', 'AVAILABLE', TRUE, TRUE, TRUE, FALSE),
('bed-er-02', 'ER-102', 'wrd-001', 'OCCUPIED',  TRUE, TRUE, TRUE, FALSE),
('bed-er-03', 'ER-103', 'wrd-001', 'OCCUPIED',  FALSE, TRUE, TRUE, FALSE),
('bed-er-04', 'ER-104', 'wrd-001', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-er-05', 'ER-105', 'wrd-001', 'CLEANING',  TRUE, TRUE, TRUE, FALSE),
('bed-er-06', 'ER-106', 'wrd-001', 'OCCUPIED',  FALSE, TRUE, TRUE, FALSE),
('bed-er-07', 'ER-107', 'wrd-001', 'RESERVED',  TRUE, TRUE, TRUE, FALSE),
('bed-er-08', 'ER-108', 'wrd-001', 'OCCUPIED',  FALSE, FALSE, TRUE, FALSE),
('bed-er-09', 'ER-109', 'wrd-001', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-er-10', 'ER-110', 'wrd-001', 'OCCUPIED',  TRUE, TRUE, TRUE, FALSE),
('bed-er-11', 'ER-111', 'wrd-001', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-er-12', 'ER-112', 'wrd-001', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE);

-- ICU Beds
INSERT INTO beds (bed_id, bed_number, ward_id, status, has_ventilator, has_cardiac_monitor, has_oxygen_supply, has_dialysis_port) VALUES
('bed-icu-01', 'ICU-201', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, TRUE),
('bed-icu-02', 'ICU-202', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, TRUE),
('bed-icu-03', 'ICU-203', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, FALSE),
('bed-icu-04', 'ICU-204', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, FALSE),
('bed-icu-05', 'ICU-205', 'wrd-002', 'AVAILABLE', TRUE, TRUE, TRUE, TRUE),
('bed-icu-06', 'ICU-206', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, FALSE),
('bed-icu-07', 'ICU-207', 'wrd-002', 'CLEANING', TRUE, TRUE, TRUE, FALSE),
('bed-icu-08', 'ICU-208', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, TRUE),
('bed-icu-09', 'ICU-209', 'wrd-002', 'AVAILABLE', TRUE, TRUE, TRUE, TRUE),
('bed-icu-10', 'ICU-210', 'wrd-002', 'OCCUPIED', TRUE, TRUE, TRUE, FALSE);

-- General Male Beds
INSERT INTO beds (bed_id, bed_number, ward_id, status, has_ventilator, has_cardiac_monitor, has_oxygen_supply, has_dialysis_port) VALUES
('bed-gm-01', 'GM-301', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-02', 'GM-302', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-03', 'GM-303', 'wrd-003', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-gm-04', 'GM-304', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-05', 'GM-305', 'wrd-003', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-gm-06', 'GM-306', 'wrd-003', 'OCCUPIED', FALSE, FALSE, FALSE, FALSE),
('bed-gm-07', 'GM-307', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-08', 'GM-308', 'wrd-003', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-gm-09', 'GM-309', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-10', 'GM-310', 'wrd-003', 'CLEANING', FALSE, FALSE, FALSE, FALSE),
('bed-gm-11', 'GM-311', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gm-12', 'GM-312', 'wrd-003', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-gm-13', 'GM-313', 'wrd-003', 'AVAILABLE', FALSE, FALSE, FALSE, FALSE),
('bed-gm-14', 'GM-314', 'wrd-003', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE);

-- General Female Beds
INSERT INTO beds (bed_id, bed_number, ward_id, status, has_ventilator, has_cardiac_monitor, has_oxygen_supply, has_dialysis_port) VALUES
('bed-gf-01', 'GF-401', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gf-02', 'GF-402', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gf-03', 'GF-403', 'wrd-004', 'OCCUPIED', FALSE, TRUE, TRUE, FALSE),
('bed-gf-04', 'GF-404', 'wrd-004', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-gf-05', 'GF-405', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gf-06', 'GF-406', 'wrd-004', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-gf-07', 'GF-407', 'wrd-004', 'OCCUPIED', FALSE, FALSE, FALSE, FALSE),
('bed-gf-08', 'GF-408', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gf-09', 'GF-409', 'wrd-004', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-gf-10', 'GF-410', 'wrd-004', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-gf-11', 'GF-411', 'wrd-004', 'OCCUPIED', FALSE, FALSE, FALSE, FALSE),
('bed-gf-12', 'GF-412', 'wrd-004', 'CLEANING', FALSE, FALSE, TRUE, FALSE),
('bed-gf-13', 'GF-413', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-gf-14', 'GF-414', 'wrd-004', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE);

-- Pediatric Beds
INSERT INTO beds (bed_id, bed_number, ward_id, status, has_ventilator, has_cardiac_monitor, has_oxygen_supply, has_dialysis_port) VALUES
('bed-ped-01', 'PED-501', 'wrd-005', 'OCCUPIED', TRUE, TRUE, TRUE, FALSE),
('bed-ped-02', 'PED-502', 'wrd-005', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-ped-03', 'PED-503', 'wrd-005', 'OCCUPIED', FALSE, TRUE, TRUE, FALSE),
('bed-ped-04', 'PED-504', 'wrd-005', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-ped-05', 'PED-505', 'wrd-005', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-ped-06', 'PED-506', 'wrd-005', 'AVAILABLE', FALSE, TRUE, TRUE, FALSE),
('bed-ped-07', 'PED-507', 'wrd-005', 'OCCUPIED', FALSE, FALSE, TRUE, FALSE),
('bed-ped-08', 'PED-508', 'wrd-005', 'AVAILABLE', FALSE, FALSE, TRUE, FALSE),
('bed-ped-09', 'PED-509', 'wrd-005', 'CLEANING', FALSE, TRUE, TRUE, FALSE),
('bed-ped-10', 'PED-510', 'wrd-005', 'AVAILABLE', TRUE, TRUE, TRUE, FALSE);

-- Patients
INSERT INTO patients (patient_id, patient_code, full_name, age, gender, contact_number, emergency_contact, symptoms, current_priority, current_status, assigned_bed_id) VALUES
('pat-001', 'PAT-2026-0001', 'Robert Montgomery', 62, 'MALE', '+1-555-7811', '+1-555-9011 (Wife)', 'Severe crushing chest pain radiating to left arm, cold sweats, dyspnea', 'P1', 'IN_TREATMENT', 'bed-icu-01'),
('pat-002', 'PAT-2026-0002', 'Chloe Bennett', 28, 'FEMALE', '+1-555-4422', '+1-555-8833 (Mother)', 'Acute asthma exacerbation, severe wheezing, retractions, SpO2 dropping', 'P1', 'IN_TREATMENT', 'bed-er-02'),
('pat-003', 'PAT-2026-0003', 'David K. Miller', 45, 'MALE', '+1-555-3388', '+1-555-7766 (Brother)', 'High fever (103.5 F), intense abdominal rebound tenderness, suspected appendicitis', 'P2', 'WAITING', NULL),
('pat-004', 'PAT-2026-0004', 'Aaliyah Khan', 34, 'FEMALE', '+1-555-9922', '+1-555-1100 (Husband)', 'Suspected deep vein thrombosis in right calf, moderate localized swelling and pain', 'P2', 'WAITING', NULL),
('pat-005', 'PAT-2026-0005', 'Thomas Wright', 71, 'MALE', '+1-555-2155', '+1-555-3211 (Son)', 'Dehydration, moderate dizziness, persistent vomiting for 24 hours', 'P3', 'WAITING', NULL),
('pat-006', 'PAT-2026-0006', 'Sophia Martinez', 19, 'FEMALE', '+1-555-6677', '+1-555-8899 (Father)', 'Sprained ankle with mild edema following soccer match, weight-bearing difficult', 'P4', 'WAITING', NULL),
('pat-007', 'PAT-2026-0007', 'Liam O\'Connor', 8, 'MALE', '+1-555-5533', '+1-555-7744 (Mother)', 'Persistent high fever, mild respiratory distress, barking cough (croup)', 'P2', 'WAITING', NULL),
('pat-008', 'PAT-2026-0008', 'Hannah Green', 52, 'FEMALE', '+1-555-1244', '+1-555-4321 (Daughter)', 'Sudden unilateral facial drooping, mild slurred speech, urgent stroke evaluation', 'P1', 'WAITING', NULL);

UPDATE beds SET current_patient_id = 'pat-001' WHERE bed_id = 'bed-icu-01';
UPDATE beds SET current_patient_id = 'pat-002' WHERE bed_id = 'bed-er-02';

-- Triage Records
INSERT INTO triage_records (record_id, patient_id, heart_rate, spo2, systolic_bp, diastolic_bp, symptoms_description, hr_points, spo2_points, bp_points, symptom_points, total_score, calculated_priority, clinical_explanation, assessment_type, assessed_by_staff_id) VALUES
('trg-001', 'pat-001', 135, 87, 185, 110, 'Crushing substernal chest pain, diaphoresis, acute STEMI suspected', 2, 3, 3, 3, 11, 'P1', 'Extreme cardiovascular instability with severe hypoxia and hypertensive emergency. Immediate resuscitation required.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-002', 'pat-002', 142, 85, 148, 92, 'Severe status asthmaticus, poor air entry', 3, 3, 1, 3, 10, 'P1', 'Critical respiratory failure and tachycardia. Immediate oxygen therapy and bronchodilators indicated.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-003', 'pat-003', 112, 96, 138, 86, 'Acute lower right quadrant pain, guarding and fever', 1, 0, 1, 2, 4, 'P3', 'Initial presentation with stable vitals but moderate acute abdominal symptoms.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-004', 'pat-004', 98, 97, 142, 90, 'Unilateral leg pain, calf tenderness, erythema', 0, 0, 1, 2, 3, 'P3', 'Sub-acute presentation, stable vitals, moderate vascular discomfort.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-005', 'pat-005', 86, 98, 118, 76, 'Moderate dehydration, nausea, dry mucous membranes', 0, 0, 0, 1, 1, 'P4', 'Low urgency presentation; normal vitals with mild dehydration symptoms.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-006', 'pat-006', 74, 99, 120, 80, 'Minor musculoskeletal soft tissue injury, ankle strain', 0, 0, 0, 0, 0, 'P4', 'Non-urgent ambulatory presentation; standard outpatient examination.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-007', 'pat-007', 128, 93, 105, 68, 'Stridor at rest, intercostal retraction, fever', 2, 1, 0, 2, 5, 'P2', 'Pediatric respiratory distress; urgent nebulization and physician evaluation.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-008', 'pat-008', 115, 94, 188, 105, 'Acute onset facial droop, dysarthria within 40 minutes', 1, 1, 3, 3, 8, 'P1', 'Code Stroke candidate. High blood pressure with neurological deficit. Priority 1 resuscitation protocol initiated.', 'INITIAL_REGISTRATION', 'stf-002'),
('trg-009', 'pat-003', 132, 94, 155, 96, 'Re-check: Abdominal pain worsened acutely, peritonitis signs, fever 104F, tachycardia', 2, 1, 2, 2, 7, 'P2', 'Urgency escalated on routine re-check. Priority raised from P3 to P2 due to clinical deterioration.', 'URGENCY_ESCALATION', 'stf-002');

-- Alerts
INSERT INTO alerts (alert_id, patient_id, alert_type, severity, title, message, is_resolved) VALUES
('alt-001', 'pat-008', 'CRITICAL_PATIENT', 'CRITICAL', 'Urgent Code Stroke (P1): Hannah Green', 'Patient registered with acute stroke symptoms, BP 188/105, Priority Score 8. Immediate CT bay & bed needed.', FALSE),
('alt-002', 'pat-003', 'PRIORITY_ESCALATED', 'HIGH', 'Clinical Deterioration: David K. Miller', 'Routine vitals re-check detected score increase from 4 (P3) to 7 (P2). Queue rank escalated.', FALSE),
('alt-003', NULL, 'CAPACITY_WARNING', 'HIGH', 'ICU Bed Availability Low (2 Available)', 'ICU Ward occupancy has reached 80.0%. Only 2 critical care beds remain unassigned.', FALSE),
('alt-004', NULL, 'HOSPITAL_SURGE', 'CRITICAL', 'Hospital Surge Threshold Approaching', 'Overall hospital occupancy at 76.7% with 3 active Priority 1 arrivals in the past 90 minutes.', FALSE);

-- Audit Log
INSERT INTO audit_log (log_id, action_type, entity_name, entity_id, staff_id, staff_name, staff_role, old_value, new_value, mandatory_reason, ip_address) VALUES
('aud-001', 'PATIENT_REGISTRATION', 'PATIENT', 'pat-001', 'stf-001', 'Sarah Jenkins', 'RECEPTIONIST', NULL, 'PAT-2026-0001 (Robert Montgomery)', 'Initial emergency registration', '192.168.1.101'),
('aud-002', 'BED_ALLOCATION', 'BED', 'bed-icu-01', 'stf-003', 'Dr. Marcus Vance, MD', 'DOCTOR', 'AVAILABLE', 'OCCUPIED (pat-001)', 'Immediate allocation for acute myocardial infarction', '192.168.1.103'),
('aud-003', 'DOCTOR_OVERRIDE', 'PATIENT', 'pat-004', 'stf-004', 'Dr. Priya Sharma, MD', 'DOCTOR', 'P3 (Calculated Score: 3)', 'P2 (Physician Clinical Judgement)', 'High D-Dimer suspicion and severe unilateral pain indicates impending pulmonary embolism risk.', '192.168.1.104'),
('aud-004', 'PRIORITY_RECHECK_ESCALATION', 'PATIENT', 'pat-003', 'stf-002', 'Elena Rostova', 'NURSE', 'P3 (Score: 4)', 'P2 (Score: 7)', 'Vital signs deteriorated during 30-min waiting room triage check.', '192.168.1.102');

-- Hospital Mode Log
INSERT INTO mode_log (mode_log_id, previous_mode, new_mode, occupancy_percentage, urgent_cases_last_hour, trigger_reason, triggered_by_staff_id) VALUES
('mod-001', 'NORMAL', 'BUSY', 80.0, 2, 'Total occupied beds reached 80% threshold across Emergency and ICU sectors.', 'stf-005'),
('mod-002', 'BUSY', 'NORMAL', 76.67, 1, 'Two patients safely discharged and transferred, dropping overall occupancy below 80%.', 'stf-005');
