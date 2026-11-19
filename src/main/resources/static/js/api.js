/**
 * AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
 * API Client Layer with Dual-Mode Support:
 * - Live REST Mode (http://localhost:8080/api)
 * - Standalone Mock Fallback (for immediate file:// browser testing)
 */

const API_BASE = window.location.origin.startsWith('http') ? '/api' : 'http://localhost:8080/api';

const ApiClient = {
  authToken: localStorage.getItem('hospital_auth_token') || null,
  currentUser: JSON.parse(localStorage.getItem('hospital_current_user') || 'null'),
  isLiveApiAvailable: false,

  async init() {
    try {
      const res = await fetch(`${API_BASE}/dashboard/stats`, { method: 'GET' });
      if (res.ok) {
        this.isLiveApiAvailable = true;
        console.log('[ApiClient] Connected to Spring Boot Backend REST APIs at ' + API_BASE);
      } else {
        this.isLiveApiAvailable = false;
      }
    } catch (e) {
      this.isLiveApiAvailable = false;
      console.warn('[ApiClient] Backend server not reachable at ' + API_BASE + '. Running in Offline/Client Mode.');
    }
  },

  getHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    if (this.authToken) {
      headers['Authorization'] = `Bearer ${this.authToken}`;
    }
    return headers;
  },

  // 1. Authentication
  async login(username, password) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/auth/login`, {
          method: 'POST',
          headers: this.getHeaders(),
          body: JSON.stringify({ username, password })
        });
        const data = await res.json();
        if (data.success) {
          this.setSession(data.sessionToken, data);
        }
        return data;
      } catch (e) {
        console.error(e);
      }
    }
    // Client-side fallback
    const roleMap = {
      'doctor': { fullName: 'Dr. Marcus Vance, MD', role: 'DOCTOR', department: 'Emergency Medicine / Trauma', code: 'DOC-301', id: 'stf-003' },
      'nurse': { fullName: 'Elena Rostova, RN', role: 'NURSE', department: 'Emergency Department', code: 'NUR-201', id: 'stf-002' },
      'reception': { fullName: 'Sarah Jenkins', role: 'RECEPTIONIST', department: 'Patient Admission', code: 'REC-101', id: 'stf-001' },
      'admin': { fullName: 'Director Arthur Pendelton', role: 'ADMINISTRATOR', department: 'Hospital Operations', code: 'ADM-401', id: 'stf-005' }
    };

    let userKey = Object.keys(roleMap).find(k => username.toLowerCase().includes(k)) || 'doctor';
    const profile = roleMap[userKey];
    const session = {
      success: true,
      message: 'Client-mode authentication successful',
      staffId: profile.id,
      employeeCode: profile.code,
      fullName: profile.fullName,
      role: profile.role,
      department: profile.department,
      email: `${userKey}@hospital.org`,
      sessionToken: 'mock-token-' + Date.now()
    };
    this.setSession(session.sessionToken, session);
    return session;
  },

  setSession(token, user) {
    this.authToken = token;
    this.currentUser = user;
    localStorage.setItem('hospital_auth_token', token);
    localStorage.setItem('hospital_current_user', JSON.stringify(user));
  },

  logout() {
    this.authToken = null;
    this.currentUser = null;
    localStorage.removeItem('hospital_auth_token');
    localStorage.removeItem('hospital_current_user');
  },

  // 2. Dashboard
  async getDashboardStats() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/dashboard/stats`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.getDashboardStats();
  },

  // 3. Forecast
  async getForecast(expectedAdmissions, expectedDischarges) {
    if (this.isLiveApiAvailable) {
      try {
        const query = new URLSearchParams();
        if (expectedAdmissions != null) query.append('expectedAdmissions', expectedAdmissions);
        if (expectedDischarges != null) query.append('expectedDischarges', expectedDischarges);
        const res = await fetch(`${API_BASE}/dashboard/forecast?${query}`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.calculateForecast(expectedAdmissions, expectedDischarges);
  },

  // 4. Patients
  async getPatients() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/patients`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.patients;
  },

  async getWaitingQueue() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/patients/queue`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.getWaitingQueue();
  },

  async registerPatient(data) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/patients/register`, {
          method: 'POST',
          headers: this.getHeaders(),
          body: JSON.stringify(data)
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.registerPatient(data);
  },

  // 5. Triage Assessment & Recheck
  async recheckPatient(patientId, data) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/triage/recheck/${patientId}`, {
          method: 'POST',
          headers: this.getHeaders(),
          body: JSON.stringify(data)
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.recheckPatient(patientId, data);
  },

  async getTriageRecords() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/triage/records`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.triageRecords;
  },

  // 6. Beds
  async getBeds() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/beds`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.beds;
  },

  async getWards() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/beds/wards`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.wards;
  },

  async recommendBeds(patientId) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/beds/recommend/${patientId}`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.recommendBeds(patientId);
  },

  async assignBed(bedId, patientId, clinicalNotes) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/beds/${bedId}/assign`, {
          method: 'POST',
          headers: this.getHeaders(),
          body: JSON.stringify({ patientId, clinicalNotes, staffId: this.currentUser?.staffId })
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.assignBed(bedId, patientId, clinicalNotes);
  },

  async updateBedStatus(bedId, newStatus) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/beds/${bedId}/status`, {
          method: 'PATCH',
          headers: this.getHeaders(),
          body: JSON.stringify({ newStatus, staffId: this.currentUser?.staffId })
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.updateBedStatus(bedId, newStatus);
  },

  // 7. Doctor Override
  async applyDoctorOverride(patientId, newPriority, mandatoryReason) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/triage/override`, {
          method: 'POST',
          headers: this.getHeaders(),
          body: JSON.stringify({
            patientId,
            newPriority,
            mandatoryReason,
            doctorStaffId: this.currentUser?.staffId
          })
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.doctorOverride(patientId, newPriority, mandatoryReason, this.currentUser);
  },

  // 8. Alerts
  async getAlerts() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/alerts`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.alerts.filter(a => !a.isResolved);
  },

  async resolveAlert(alertId) {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/alerts/${alertId}/resolve`, {
          method: 'PATCH',
          headers: this.getHeaders(),
          body: JSON.stringify({ staffId: this.currentUser?.staffId })
        });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.resolveAlert(alertId);
  },

  // 9. Audit Logs
  async getAuditLogs(action) {
    if (this.isLiveApiAvailable) {
      try {
        const query = action ? `?action=${action}` : '';
        const res = await fetch(`${API_BASE}/audit-logs${query}`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.getAuditLogs(action);
  },

  // 10. Staff
  async getStaff() {
    if (this.isLiveApiAvailable) {
      try {
        const res = await fetch(`${API_BASE}/staff`, { headers: this.getHeaders() });
        if (res.ok) return await res.json();
      } catch (e) { console.error(e); }
    }
    return MockDataStore.staff;
  }
};

/**
 * In-memory Mock Data Store for zero-setup offline preview.
 */
const MockDataStore = {
  staff: [
    { staffId: 'stf-001', employeeCode: 'REC-101', fullName: 'Sarah Jenkins', role: 'RECEPTIONIST', department: 'Patient Admission', email: 'sarah.reception@hospital.org' },
    { staffId: 'stf-002', employeeCode: 'NUR-201', fullName: 'Elena Rostova, RN', role: 'NURSE', department: 'Emergency Department', email: 'elena.nurse@hospital.org' },
    { staffId: 'stf-003', employeeCode: 'DOC-301', fullName: 'Dr. Marcus Vance, MD', role: 'DOCTOR', department: 'Emergency Medicine / Trauma', email: 'marcus.doctor@hospital.org' },
    { staffId: 'stf-004', employeeCode: 'DOC-302', fullName: 'Dr. Priya Sharma, MD', role: 'DOCTOR', department: 'Critical Care & Cardiology', email: 'priya.doctor@hospital.org' },
    { staffId: 'stf-005', employeeCode: 'ADM-401', fullName: 'Director Arthur Pendelton', role: 'ADMINISTRATOR', department: 'Hospital Operations', email: 'admin@hospital.org' }
  ],

  wards: [
    { wardId: 'wrd-001', wardCode: 'ER-BAY', wardName: 'Emergency Resuscitation Unit', wardType: 'EMERGENCY', totalBeds: 12, distanceFromErMeters: 15 },
    { wardId: 'wrd-002', wardCode: 'ICU-CRIT', wardName: 'Intensive Care Unit (Critical)', wardType: 'ICU', totalBeds: 10, distanceFromErMeters: 45 },
    { wardId: 'wrd-003', wardCode: 'GEN-MALE', wardName: 'General Medicine (Male)', wardType: 'GENERAL_MALE', totalBeds: 14, distanceFromErMeters: 85 },
    { wardId: 'wrd-004', wardCode: 'GEN-FEM', wardName: 'General Medicine (Female)', wardType: 'GENERAL_FEMALE', totalBeds: 14, distanceFromErMeters: 90 },
    { wardId: 'wrd-005', wardCode: 'PED-CARE', wardName: 'Pediatric Inpatient & Emergency', wardType: 'PEDIATRIC', totalBeds: 10, distanceFromErMeters: 60 }
  ],

  beds: [
    // Emergency
    { bedId: 'bed-er-01', bedNumber: 'ER-101', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'AVAILABLE', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-er-02', bedNumber: 'ER-102', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'OCCUPIED', currentPatientId: 'pat-002', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-er-03', bedNumber: 'ER-103', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-er-04', bedNumber: 'ER-104', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'CLEANING', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-er-05', bedNumber: 'ER-105', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'RESERVED', currentPatientId: null, hasVentilator: false, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-er-06', bedNumber: 'ER-106', ward: { wardId: 'wrd-001', wardName: 'Emergency Unit', wardType: 'EMERGENCY', distanceFromErMeters: 15 }, status: 'OCCUPIED', currentPatientId: null, hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },

    // ICU
    { bedId: 'bed-icu-01', bedNumber: 'ICU-201', ward: { wardId: 'wrd-002', wardName: 'Intensive Care Unit', wardType: 'ICU', distanceFromErMeters: 45 }, status: 'OCCUPIED', currentPatientId: 'pat-001', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true, hasDialysisPort: true },
    { bedId: 'bed-icu-02', bedNumber: 'ICU-202', ward: { wardId: 'wrd-002', wardName: 'Intensive Care Unit', wardType: 'ICU', distanceFromErMeters: 45 }, status: 'AVAILABLE', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true, hasDialysisPort: true },
    { bedId: 'bed-icu-03', bedNumber: 'ICU-203', ward: { wardId: 'wrd-002', wardName: 'Intensive Care Unit', wardType: 'ICU', distanceFromErMeters: 45 }, status: 'OCCUPIED', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-icu-04', bedNumber: 'ICU-204', ward: { wardId: 'wrd-002', wardName: 'Intensive Care Unit', wardType: 'ICU', distanceFromErMeters: 45 }, status: 'CLEANING', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },

    // General Male
    { bedId: 'bed-gm-01', bedNumber: 'GM-301', ward: { wardId: 'wrd-003', wardName: 'General Male', wardType: 'GENERAL_MALE', distanceFromErMeters: 85 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-gm-02', bedNumber: 'GM-302', ward: { wardId: 'wrd-003', wardName: 'General Male', wardType: 'GENERAL_MALE', distanceFromErMeters: 85 }, status: 'OCCUPIED', hasVentilator: false, hasCardiacMonitor: false, hasOxygenSupply: true },
    { bedId: 'bed-gm-03', bedNumber: 'GM-303', ward: { wardId: 'wrd-003', wardName: 'General Male', wardType: 'GENERAL_MALE', distanceFromErMeters: 85 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: false, hasOxygenSupply: true },

    // General Female
    { bedId: 'bed-gf-01', bedNumber: 'GF-401', ward: { wardId: 'wrd-004', wardName: 'General Female', wardType: 'GENERAL_FEMALE', distanceFromErMeters: 90 }, status: 'OCCUPIED', hasVentilator: false, hasCardiacMonitor: false, hasOxygenSupply: true },
    { bedId: 'bed-gf-02', bedNumber: 'GF-402', ward: { wardId: 'wrd-004', wardName: 'General Female', wardType: 'GENERAL_FEMALE', distanceFromErMeters: 90 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-gf-03', bedNumber: 'GF-403', ward: { wardId: 'wrd-004', wardName: 'General Female', wardType: 'GENERAL_FEMALE', distanceFromErMeters: 90 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: false, hasOxygenSupply: true },

    // Pediatric
    { bedId: 'bed-ped-01', bedNumber: 'PED-501', ward: { wardId: 'wrd-005', wardName: 'Pediatric Care', wardType: 'PEDIATRIC', distanceFromErMeters: 60 }, status: 'AVAILABLE', hasVentilator: true, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-ped-02', bedNumber: 'PED-502', ward: { wardId: 'wrd-005', wardName: 'Pediatric Care', wardType: 'PEDIATRIC', distanceFromErMeters: 60 }, status: 'OCCUPIED', hasVentilator: false, hasCardiacMonitor: true, hasOxygenSupply: true },
    { bedId: 'bed-ped-03', bedNumber: 'PED-503', ward: { wardId: 'wrd-005', wardName: 'Pediatric Care', wardType: 'PEDIATRIC', distanceFromErMeters: 60 }, status: 'AVAILABLE', hasVentilator: false, hasCardiacMonitor: false, hasOxygenSupply: true }
  ],

  patients: [
    { patientId: 'pat-001', patientCode: 'PAT-2026-0001', fullName: 'Robert Montgomery', age: 62, gender: 'MALE', contactNumber: '+1-555-7811', emergencyContact: '+1-555-9011 (Wife)', symptoms: 'Crushing chest pain, cold sweats, dyspnea, suspected acute STEMI', currentPriority: 'P1', currentStatus: 'IN_TREATMENT', assignedBedId: 'bed-icu-01', registrationTime: new Date(Date.now() - 120 * 60000).toISOString() },
    { patientId: 'pat-002', patientCode: 'PAT-2026-0002', fullName: 'Chloe Bennett', age: 28, gender: 'FEMALE', contactNumber: '+1-555-4422', emergencyContact: '+1-555-8833 (Mother)', symptoms: 'Acute asthma exacerbation, severe wheezing, retractions', currentPriority: 'P1', currentStatus: 'IN_TREATMENT', assignedBedId: 'bed-er-02', registrationTime: new Date(Date.now() - 95 * 60000).toISOString() },
    { patientId: 'pat-003', patientCode: 'PAT-2026-0003', fullName: 'David K. Miller', age: 45, gender: 'MALE', contactNumber: '+1-555-3388', emergencyContact: '+1-555-7766 (Brother)', symptoms: 'High fever (103.5 F), intense abdominal rebound tenderness, suspected appendicitis', currentPriority: 'P2', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 45 * 60000).toISOString() },
    { patientId: 'pat-004', patientCode: 'PAT-2026-0004', fullName: 'Aaliyah Khan', age: 34, gender: 'FEMALE', contactNumber: '+1-555-9922', emergencyContact: '+1-555-1100 (Husband)', symptoms: 'Suspected deep vein thrombosis in right calf, moderate swelling', currentPriority: 'P2', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 35 * 60000).toISOString() },
    { patientId: 'pat-005', patientCode: 'PAT-2026-0005', fullName: 'Thomas Wright', age: 71, gender: 'MALE', contactNumber: '+1-555-2155', emergencyContact: '+1-555-3211 (Son)', symptoms: 'Dehydration, moderate dizziness, persistent vomiting for 24 hours', currentPriority: 'P3', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 50 * 60000).toISOString() },
    { patientId: 'pat-006', patientCode: 'PAT-2026-0006', fullName: 'Sophia Martinez', age: 19, gender: 'FEMALE', contactNumber: '+1-555-6677', emergencyContact: '+1-555-8899 (Father)', symptoms: 'Sprained ankle with mild edema following soccer match', currentPriority: 'P4', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 60 * 60000).toISOString() },
    { patientId: 'pat-007', patientCode: 'PAT-2026-0007', fullName: 'Liam O\'Connor', age: 8, gender: 'MALE', contactNumber: '+1-555-5533', emergencyContact: '+1-555-7744 (Mother)', symptoms: 'Persistent fever, mild respiratory distress, barking cough', currentPriority: 'P2', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 20 * 60000).toISOString() },
    { patientId: 'pat-008', patientCode: 'PAT-2026-0008', fullName: 'Hannah Green', age: 52, gender: 'FEMALE', contactNumber: '+1-555-1244', emergencyContact: '+1-555-4321 (Daughter)', symptoms: 'Sudden unilateral facial drooping, mild slurred speech, urgent stroke evaluation', currentPriority: 'P1', currentStatus: 'WAITING', assignedBedId: null, registrationTime: new Date(Date.now() - 10 * 60000).toISOString() }
  ],

  triageRecords: [
    { recordId: 'trg-001', patient: { patientId: 'pat-008', fullName: 'Hannah Green' }, heartRate: 115, spo2: 94, systolicBp: 188, diastolicBp: 105, totalScore: 8, calculatedPriority: 'P1', clinicalExplanation: 'Code Stroke candidate. Hypertensive crisis with acute neurological deficit. Immediate CT and trauma bed required.', assessmentType: 'INITIAL_REGISTRATION', assessmentTime: new Date(Date.now() - 10 * 60000).toISOString() },
    { recordId: 'trg-002', patient: { patientId: 'pat-003', fullName: 'David K. Miller' }, heartRate: 132, spo2: 94, systolicBp: 155, diastolicBp: 96, totalScore: 7, calculatedPriority: 'P2', clinicalExplanation: 'CLINICAL DETERIORATION DETECTED: Priority escalated from P3 to P2 due to acute worsening of peritonitis and tachycardia.', assessmentType: 'URGENCY_ESCALATION', assessmentTime: new Date(Date.now() - 15 * 60000).toISOString() }
  ],

  alerts: [
    { alertId: 'alt-001', alertType: 'CRITICAL_PATIENT', severity: 'CRITICAL', title: 'Urgent Code Stroke (P1): Hannah Green', message: 'Patient registered with acute stroke symptoms, BP 188/105, Priority Score 8. Immediate resuscitation bay & bed needed.', isResolved: false, createdAt: new Date(Date.now() - 10 * 60000).toISOString() },
    { alertId: 'alt-002', alertType: 'PRIORITY_ESCALATED', severity: 'HIGH', title: 'Clinical Deterioration: David K. Miller', message: 'Routine vitals re-check detected score increase from 4 (P3) to 7 (P2). Queue rank escalated.', isResolved: false, createdAt: new Date(Date.now() - 15 * 60000).toISOString() },
    { alertId: 'alt-003', alertType: 'CAPACITY_WARNING', severity: 'HIGH', title: 'ICU Bed Availability Low (1 Available)', message: 'ICU Ward occupancy has reached 80.0%. Only 1 critical care bed remains unassigned.', isResolved: false, createdAt: new Date(Date.now() - 30 * 60000).toISOString() }
  ],

  auditLogs: [
    { logId: 'aud-001', actionType: 'PATIENT_REGISTRATION', entityName: 'PATIENT', entityId: 'pat-008', staffName: 'Sarah Jenkins', staffRole: 'RECEPTIONIST', oldValue: null, newValue: 'PAT-2026-0008 (Hannah Green)', mandatoryReason: 'New emergency intake', createdAt: new Date(Date.now() - 10 * 60000).toISOString() },
    { logId: 'aud-002', actionType: 'DOCTOR_OVERRIDE', entityName: 'PATIENT', entityId: 'pat-004', staffName: 'Dr. Priya Sharma, MD', staffRole: 'DOCTOR', oldValue: 'P3 (Score: 3)', newValue: 'P2 (Clinical Judgement)', mandatoryReason: 'High D-Dimer suspicion and sudden unilateral calf swelling indicates pulmonary embolism risk.', createdAt: new Date(Date.now() - 25 * 60000).toISOString() },
    { logId: 'aud-003', actionType: 'PRIORITY_RECHECK_ESCALATION', entityName: 'PATIENT', entityId: 'pat-003', staffName: 'Elena Rostova, RN', staffRole: 'NURSE', oldValue: 'P3 (Score: 4)', newValue: 'P2 (Score: 7)', mandatoryReason: 'Vital signs deteriorated during 30-min waiting room triage check.', createdAt: new Date(Date.now() - 15 * 60000).toISOString() }
  ],

  getWaitingQueue() {
    const priorityOrder = { 'P1': 1, 'P2': 2, 'P3': 3, 'P4': 4 };
    return this.patients
      .filter(p => p.currentStatus === 'WAITING')
      .sort((a, b) => {
        const diff = (priorityOrder[a.currentPriority] || 5) - (priorityOrder[b.currentPriority] || 5);
        if (diff !== 0) return diff;
        return new Date(a.registrationTime) - new Date(b.registrationTime);
      });
  },

  getDashboardStats() {
    const totalPatients = this.patients.length;
    const p1 = this.patients.filter(p => p.currentPriority === 'P1').length;
    const p2 = this.patients.filter(p => p.currentPriority === 'P2').length;
    const p3 = this.patients.filter(p => p.currentPriority === 'P3').length;
    const p4 = this.patients.filter(p => p.currentPriority === 'P4').length;
    const waiting = this.patients.filter(p => p.currentStatus === 'WAITING').length;

    const totalBeds = 50; // Reference hospital capacity
    const occupied = this.beds.filter(b => b.status === 'OCCUPIED').length + 35; // Simulated baseline total
    const available = totalBeds - occupied;
    const occPct = Math.round((occupied / totalBeds) * 1000) / 10;

    let mode = 'NORMAL';
    let directive = 'Standard clinical operating procedures. Regular shift staffing and elective admissions permitted.';
    if (occPct >= 95 || p1 >= 5) {
      mode = 'SURGE';
      directive = 'CRITICAL SURGE PROTOCOL: Emergency divert active for non-trauma arrivals. Convert step-down beds.';
    } else if (occPct >= 80) {
      mode = 'BUSY';
      directive = 'Hospital occupancy >= 80%. Prioritize rapid discharge evaluations, expedite bed cleaning.';
    }

    return {
      totalPatients,
      criticalPatientsCount: p1,
      highPriorityPatientsCount: p2,
      moderatePatientsCount: p3,
      lowPriorityPatientsCount: p4,
      waitingQueueCount: waiting,
      totalBeds,
      availableBeds: available,
      occupiedBeds: occupied,
      occupancyPercentage: occPct,
      currentHospitalMode: mode,
      modeDirective: directive,
      recentEmergencyCases: this.patients.slice(-5).reverse(),
      activeAlerts: this.alerts.filter(a => !a.isResolved),
      forecast: this.calculateForecast(null, null)
    };
  },

  calculateForecast(admissionsParam, dischargesParam) {
    const totalCapacity = 50;
    const currentOccupied = 42; // Academic example from prompt
    const admissions = admissionsParam != null ? admissionsParam : 8;
    const discharges = dischargesParam != null ? dischargesParam : 4;

    const projectedOccupied = currentOccupied + admissions - discharges; // 46
    const projectedOccupancyPct = Math.round((projectedOccupied / totalCapacity) * 1000) / 10; // 92.0%

    const hourLabels = ['Now', '+1h', '+2h', '+3h', '+4h'];
    const hourlyProjectedBeds = [42, 43, 45, 45, projectedOccupied];
    const hourlyOccupancyPercentages = hourlyProjectedBeds.map(b => Math.round((b / totalCapacity) * 1000) / 10);

    return {
      totalCapacity,
      currentOccupiedBeds: currentOccupied,
      currentOccupancyPercentage: Math.round((currentOccupied / totalCapacity) * 1000) / 10,
      expectedAdmissions: admissions,
      expectedDischarges: discharges,
      projectedOccupiedBeds: projectedOccupied,
      projectedOccupancyPercentage: projectedOccupancyPct,
      isCapacityWarning: projectedOccupancyPct >= 80,
      warningMessage: projectedOccupancyPct >= 95
        ? `CRITICAL SURGE WARNING: Projected occupancy reaches ${projectedOccupancyPct}%. Code Red Surge protocol required.`
        : projectedOccupancyPct >= 80
        ? `CAPACITY WARNING: Projected occupancy reaches ${projectedOccupancyPct}%. Expedited discharges recommended.`
        : 'Hospital capacity is within normal operational range (<80%).',
      hourLabels,
      hourlyProjectedBeds,
      hourlyOccupancyPercentages
    };
  },

  registerPatient(data) {
    const id = 'pat-' + Date.now().toString().slice(-4);
    const code = 'PAT-2026-' + String(this.patients.length + 1).padStart(4, '0');
    
    // Triage scoring
    const hrPts = data.heartRate < 40 || data.heartRate >= 140 ? 3 : (data.heartRate < 50 || data.heartRate >= 120 ? 2 : (data.heartRate < 60 || data.heartRate >= 101 ? 1 : 0));
    const spo2Pts = data.spo2 < 88 ? 3 : (data.spo2 <= 91 ? 2 : (data.spo2 <= 94 ? 1 : 0));
    const bpPts = data.systolicBp >= 180 || data.systolicBp < 80 ? 3 : (data.systolicBp >= 160 || data.systolicBp < 90 ? 2 : (data.systolicBp >= 140 || data.systolicBp < 100 ? 1 : 0));
    let symPts = 0;
    const s = (data.symptoms || '').toLowerCase();
    if (s.includes('chest pain') || s.includes('stroke') || s.includes('arrest') || s.includes('unconscious')) symPts = 3;
    else if (s.includes('asthma') || s.includes('fracture') || s.includes('severe') || s.includes('fever')) symPts = 2;
    else if (s.includes('dizziness') || s.includes('vomit') || s.includes('pain')) symPts = 1;

    const total = hrPts + spo2Pts + bpPts + symPts;
    const prio = total >= 8 ? 'P1' : (total >= 5 ? 'P2' : (total >= 3 ? 'P3' : 'P4'));

    const newPatient = {
      patientId: id,
      patientCode: code,
      fullName: data.fullName,
      age: data.age,
      gender: data.gender,
      contactNumber: data.contactNumber,
      emergencyContact: data.emergencyContact || 'None',
      symptoms: data.symptoms,
      currentPriority: prio,
      currentStatus: 'WAITING',
      assignedBedId: null,
      registrationTime: new Date().toISOString()
    };

    this.patients.push(newPatient);

    this.auditLogs.unshift({
      logId: 'aud-' + Date.now().toString().slice(-4),
      actionType: 'PATIENT_REGISTRATION',
      entityName: 'PATIENT',
      entityId: id,
      staffName: 'Elena Rostova, RN',
      staffRole: 'NURSE',
      oldValue: null,
      newValue: `${code} (${data.fullName}) - Priority ${prio}`,
      mandatoryReason: 'Emergency Registration',
      createdAt: new Date().toISOString()
    });

    if (prio === 'P1') {
      this.alerts.unshift({
        alertId: 'alt-' + Date.now().toString().slice(-4),
        alertType: 'CRITICAL_PATIENT',
        severity: 'CRITICAL',
        title: `CRITICAL P1 PATIENT: ${data.fullName}`,
        message: `Registered with score ${total}/12. Immediate trauma/ICU bed allocation required.`,
        isResolved: false,
        createdAt: new Date().toISOString()
      });
    }

    return newPatient;
  },

  recheckPatient(patientId, data) {
    const p = this.patients.find(x => x.patientId === patientId);
    if (!p) return null;

    const oldPrio = p.currentPriority;
    const hrPts = data.heartRate < 40 || data.heartRate >= 140 ? 3 : (data.heartRate < 50 || data.heartRate >= 120 ? 2 : (data.heartRate < 60 || data.heartRate >= 101 ? 1 : 0));
    const spo2Pts = data.spo2 < 88 ? 3 : (data.spo2 <= 91 ? 2 : (data.spo2 <= 94 ? 1 : 0));
    const bpPts = data.systolicBp >= 180 || data.systolicBp < 80 ? 3 : (data.systolicBp >= 160 || data.systolicBp < 90 ? 2 : (data.systolicBp >= 140 || data.systolicBp < 100 ? 1 : 0));
    const symPts = data.updatedSymptoms ? 2 : 1;
    const total = hrPts + spo2Pts + bpPts + symPts;
    const newPrio = total >= 8 ? 'P1' : (total >= 5 ? 'P2' : (total >= 3 ? 'P3' : 'P4'));

    p.currentPriority = newPrio;
    if (data.updatedSymptoms) p.symptoms = data.updatedSymptoms;

    const rank = { 'P1': 1, 'P2': 2, 'P3': 3, 'P4': 4 };
    const escalated = rank[newPrio] < rank[oldPrio];

    if (escalated) {
      this.alerts.unshift({
        alertId: 'alt-' + Date.now().toString().slice(-4),
        alertType: 'PRIORITY_ESCALATED',
        severity: 'HIGH',
        title: `CLINICAL DETERIORATION: ${p.fullName}`,
        message: `Vital signs recheck increased urgency from ${oldPrio} to ${newPrio} (New Score: ${total}/12).`,
        isResolved: false,
        createdAt: new Date().toISOString()
      });
    }

    this.auditLogs.unshift({
      logId: 'aud-' + Date.now().toString().slice(-4),
      actionType: escalated ? 'PRIORITY_RECHECK_ESCALATION' : 'ROUTINE_RECHECK',
      entityName: 'PATIENT',
      entityId: p.patientId,
      staffName: 'Elena Rostova, RN',
      staffRole: 'NURSE',
      oldValue: oldPrio,
      newValue: newPrio,
      mandatoryReason: `Vitals recheck: HR ${data.heartRate}, SpO2 ${data.spo2}%, BP ${data.systolicBp}/${data.diastolicBp}`,
      createdAt: new Date().toISOString()
    });

    return { totalScore: total, calculatedPriority: newPrio, clinicalExplanation: `Score ${total}/12. ${escalated ? 'URGENCY ESCALATED' : 'Stable'}` };
  },

  recommendBeds(patientId) {
    const p = this.patients.find(x => x.patientId === patientId);
    const availableBeds = this.beds.filter(b => b.status === 'AVAILABLE');

    return availableBeds.map(bed => {
      let prioFit = 60;
      if (p.currentPriority === 'P1') {
        prioFit = bed.ward.wardType === 'ICU' ? 100 : (bed.ward.wardType === 'EMERGENCY' ? 95 : 40);
      } else if (p.currentPriority === 'P2') {
        prioFit = bed.ward.wardType === 'EMERGENCY' ? 100 : (bed.ward.wardType === 'ICU' ? 80 : 75);
      } else {
        prioFit = bed.ward.wardType.startsWith('GENERAL') ? 100 : 50;
      }

      let equip = 40;
      if (bed.hasVentilator) equip += 30;
      if (bed.hasCardiacMonitor) equip += 20;
      if (bed.hasOxygenSupply) equip += 10;
      equip = Math.min(100, equip);

      let wardFit = 90;
      if (p.age < 12) {
        wardFit = bed.ward.wardType === 'PEDIATRIC' ? 100 : 30;
      } else if (bed.ward.wardType === 'GENERAL_MALE' && p.gender !== 'MALE') {
        wardFit = 0;
      } else if (bed.ward.wardType === 'GENERAL_FEMALE' && p.gender !== 'FEMALE') {
        wardFit = 0;
      }

      const dist = Math.max(20, 100 - (bed.ward.distanceFromErMeters * 0.5));

      const total = (prioFit * 0.40) + (equip * 0.30) + (wardFit * 0.20) + (dist * 0.10);
      const roundedTotal = Math.round(total * 10) / 10;

      return {
        bed,
        totalFitScore: roundedTotal,
        priorityFitScore: prioFit,
        equipmentScore: equip,
        wardTypeScore: wardFit,
        distanceScore: dist,
        matchReason: `Fit Score: ${roundedTotal}% | ${bed.bedNumber} in ${bed.ward.wardName}`
      };
    }).sort((a, b) => b.totalFitScore - a.totalFitScore);
  },

  assignBed(bedId, patientId, notes) {
    const bed = this.beds.find(b => b.bedId === bedId);
    const p = this.patients.find(x => x.patientId === patientId);
    if (bed && p) {
      bed.status = 'OCCUPIED';
      bed.currentPatientId = p.patientId;
      p.assignedBedId = bed.bedId;
      p.currentStatus = 'BED_ASSIGNED';

      this.auditLogs.unshift({
        logId: 'aud-' + Date.now().toString().slice(-4),
        actionType: 'BED_ASSIGNMENT',
        entityName: 'BED',
        entityId: bed.bedId,
        staffName: 'Dr. Marcus Vance, MD',
        staffRole: 'DOCTOR',
        oldValue: 'AVAILABLE',
        newValue: `OCCUPIED (${p.fullName})`,
        mandatoryReason: notes || 'Bed allocated based on Strategy recommendation',
        createdAt: new Date().toISOString()
      });
    }
    return bed;
  },

  updateBedStatus(bedId, newStatus) {
    const bed = this.beds.find(b => b.bedId === bedId);
    if (bed) {
      const old = bed.status;
      bed.status = newStatus;
      if (newStatus === 'AVAILABLE' || newStatus === 'CLEANING') {
        bed.currentPatientId = null;
      }
      this.auditLogs.unshift({
        logId: 'aud-' + Date.now().toString().slice(-4),
        actionType: 'BED_STATUS_UPDATE',
        entityName: 'BED',
        entityId: bed.bedId,
        staffName: 'Director Arthur Pendelton',
        staffRole: 'ADMINISTRATOR',
        oldValue: old,
        newValue: newStatus,
        mandatoryReason: 'State pattern transition applied',
        createdAt: new Date().toISOString()
      });
    }
    return bed;
  },

  doctorOverride(patientId, newPriority, reason, doctor) {
    const p = this.patients.find(x => x.patientId === patientId);
    if (!p) return null;
    const old = p.currentPriority;
    p.currentPriority = newPriority;

    this.auditLogs.unshift({
      logId: 'aud-' + Date.now().toString().slice(-4),
      actionType: 'DOCTOR_OVERRIDE',
      entityName: 'PATIENT',
      entityId: p.patientId,
      staffName: doctor?.fullName || 'Dr. Marcus Vance, MD',
      staffRole: 'DOCTOR',
      oldValue: `${old} (System Score)`,
      newValue: `${newPriority} (Clinical Judgement)`,
      mandatoryReason: reason,
      createdAt: new Date().toISOString()
    });

    return p;
  },

  resolveAlert(alertId) {
    const alert = this.alerts.find(a => a.alertId === alertId);
    if (alert) alert.isResolved = true;
    return alert;
  },

  getAuditLogs(action) {
    if (!action) return this.auditLogs;
    return this.auditLogs.filter(l => l.actionType === action);
  }
};
