/**
 * AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM
 * Frontend Application Controller (app.js)
 * CSA1104 - Object Oriented Analysis and Design
 */

document.addEventListener('DOMContentLoaded', async () => {
  // Initialize API connection
  await ApiClient.init();

  // Initialize UI components
  App.init();
});

const App = {
  currentSection: 'dashboard',
  forecastChart: null,
  triageChart: null,
  activeWardFilter: 'ALL',
  activeBedStatusFilter: 'ALL',

  async init() {
    this.setupEventListeners();
    this.checkAuth();
    await this.loadDashboardData();
    this.initTriageLivePreview();
    this.setupForecastSimulator();
  },

  // --------------------------------------------------------------------------
  // Authentication & Role Management
  // --------------------------------------------------------------------------
  checkAuth() {
    let user = ApiClient.currentUser;
    if (!user) {
      // Default to attending ER doctor for immediate evaluation
      user = {
        fullName: 'Dr. Marcus Vance, MD',
        role: 'DOCTOR',
        department: 'Emergency Medicine / Trauma',
        email: 'marcus.doctor@hospital.org',
        employeeCode: 'DOC-301',
        staffId: 'stf-003'
      };
      ApiClient.setSession('demo-token', user);
    }
    this.updateUserUI(user);
  },

  updateUserUI(user) {
    document.querySelectorAll('.current-user-name').forEach(el => el.textContent = user.fullName);
    document.querySelectorAll('.current-user-role').forEach(el => el.textContent = user.role);
    document.querySelectorAll('.current-user-avatar').forEach(el => el.textContent = user.fullName.charAt(0));

    // Role-specific badges and hints
    const rolePill = document.getElementById('userRoleBadge');
    if (rolePill) {
      rolePill.textContent = user.role;
      rolePill.className = `user-role-badge role-${user.role.toLowerCase()}`;
    }
  },

  async switchRole(roleKey) {
    const res = await ApiClient.login(roleKey, 'Password123!');
    this.updateUserUI(res);
    this.showToast(`Switched active session to: ${res.fullName} (${res.role})`, 'success');
    this.refreshCurrentView();
  },

  // --------------------------------------------------------------------------
  // Navigation
  // --------------------------------------------------------------------------
  setupEventListeners() {
    // Sidebar Nav items
    document.querySelectorAll('.nav-item[data-section]').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const sectionId = link.getAttribute('data-section');
        this.navigateTo(sectionId);
      });
    });

    // Mobile sidebar toggle
    const toggleBtn = document.getElementById('mobileMenuToggle');
    const sidebar = document.getElementById('appSidebar');
    if (toggleBtn && sidebar) {
      toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('open');
      });
    }

    // Role quick-switcher buttons
    document.querySelectorAll('.btn-role-switch').forEach(btn => {
      btn.addEventListener('click', () => {
        const role = btn.getAttribute('data-role');
        this.switchRole(role);
      });
    });

    // Modal close buttons
    document.querySelectorAll('.close-modal-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        this.closeAllModals();
      });
    });

    // Patient Registration form submit
    const regForm = document.getElementById('patientRegistrationForm');
    if (regForm) {
      regForm.addEventListener('submit', (e) => this.handlePatientRegistration(e));
    }

    // Doctor Override form submit
    const overrideForm = document.getElementById('doctorOverrideForm');
    if (overrideForm) {
      overrideForm.addEventListener('submit', (e) => this.handleDoctorOverrideSubmit(e));
    }

    // Recheck Vitals form submit
    const recheckForm = document.getElementById('recheckVitalsForm');
    if (recheckForm) {
      recheckForm.addEventListener('submit', (e) => this.handleRecheckVitalsSubmit(e));
    }

    // Patient Search in Records
    const patientSearch = document.getElementById('patientSearchInput');
    if (patientSearch) {
      patientSearch.addEventListener('input', (e) => this.filterPatientsTable(e.target.value));
    }

    // Audit logs filter
    const auditFilter = document.getElementById('auditActionFilter');
    if (auditFilter) {
      auditFilter.addEventListener('change', (e) => this.loadAuditLogs(e.target.value));
    }

    // Export Audit Logs to CSV
    const exportBtn = document.getElementById('exportAuditCsvBtn');
    if (exportBtn) {
      exportBtn.addEventListener('click', () => this.exportAuditLogsToCsv());
    }
  },

  navigateTo(sectionId) {
    if (sectionId === 'logout') {
      ApiClient.logout();
      this.showToast('You have been logged out.', 'info');
      setTimeout(() => this.switchRole('doctor'), 800);
      return;
    }

    this.currentSection = sectionId;

    // Update sidebar active state
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    const activeLink = document.querySelector(`.nav-item[data-section="${sectionId}"]`);
    if (activeLink) activeLink.classList.add('active');

    // Update visible content section
    document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
    const activeSec = document.getElementById(`section-${sectionId}`);
    if (activeSec) {
      activeSec.classList.add('active');
    }

    // Update breadcrumb
    const breadcrumb = document.getElementById('breadcrumbCurrent');
    if (breadcrumb && activeLink) {
      const text = activeLink.querySelector('.nav-label')?.textContent || sectionId;
      breadcrumb.textContent = text;
    }

    // Close mobile menu if open
    document.getElementById('appSidebar')?.classList.remove('open');

    // Trigger section-specific data loads
    this.refreshCurrentView();
  },

  refreshCurrentView() {
    switch (this.currentSection) {
      case 'dashboard':
        this.loadDashboardData();
        break;
      case 'patient-registration':
        this.resetRegistrationForm();
        break;
      case 'patient-records':
        this.loadPatientRecords();
        break;
      case 'emergency-triage':
        this.loadTriagePatientDropdown();
        break;
      case 'waiting-queue':
        this.loadWaitingQueue();
        break;
      case 'bed-management':
        this.loadBedBoard();
        break;
      case 'staff-management':
        this.loadStaffDirectory();
        break;
      case 'hospital-forecast':
        this.loadForecastView();
        break;
      case 'alerts':
        this.loadAlertsView();
        break;
      case 'doctor-override':
        this.loadDoctorOverrideView();
        break;
      case 'audit-logs':
        this.loadAuditLogs();
        break;
      case 'settings':
        this.loadSettingsView();
        break;
    }
  },

  // --------------------------------------------------------------------------
  // Module 2: Dashboard
  // --------------------------------------------------------------------------
  async loadDashboardData() {
    const stats = await ApiClient.getDashboardStats();
    if (!stats) return;

    // Update KPIs
    document.getElementById('kpiTotalPatients').textContent = stats.totalPatients || 0;
    document.getElementById('kpiCriticalPatients').textContent = stats.criticalPatientsCount || 0;
    document.getElementById('kpiAvailableBeds').textContent = stats.availableBeds || 0;
    document.getElementById('kpiOccupancyPct').textContent = `${stats.occupancyPercentage || 0}%`;

    // Waiting queue badge
    document.querySelectorAll('.waiting-badge-count').forEach(el => {
      el.textContent = stats.waitingQueueCount || 0;
    });

    // Active alerts badge
    const activeAlertsCount = stats.activeAlerts?.length || 0;
    document.querySelectorAll('.alerts-badge-count').forEach(el => {
      el.textContent = activeAlertsCount;
    });

    // Update Hospital Mode Pill
    this.updateHospitalModePill(stats.currentHospitalMode, stats.modeDirective);

    // Render Recent Cases Table
    this.renderRecentCasesTable(stats.recentEmergencyCases);

    // Render Live Alerts List
    this.renderDashboardAlerts(stats.activeAlerts);

    // Render Charts
    this.renderForecastChart(stats.forecast);
    this.renderTriageDonutChart(stats);
  },

  updateHospitalModePill(mode, directive) {
    const pill = document.getElementById('hospitalModePill');
    const text = document.getElementById('hospitalModeText');
    const directiveEl = document.getElementById('hospitalModeDirective');

    if (!pill || !text) return;

    pill.className = 'hospital-mode-pill';
    const modeKey = (mode || 'NORMAL').toUpperCase();

    if (modeKey === 'SURGE') {
      pill.classList.add('mode-surge');
      text.textContent = 'Surge Mode (Critical)';
    } else if (modeKey === 'BUSY') {
      pill.classList.add('mode-busy');
      text.textContent = 'Busy Mode (>= 80%)';
    } else {
      pill.classList.add('mode-normal');
      text.textContent = 'Normal Operations (< 80%)';
    }

    if (directiveEl && directive) {
      directiveEl.textContent = directive;
    }
  },

  renderRecentCasesTable(patients) {
    const tbody = document.getElementById('recentCasesTableBody');
    if (!tbody) return;

    if (!patients || patients.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="text-center" style="padding:24px; color:var(--text-muted);">No emergency patients registered yet.</td></tr>`;
      return;
    }

    tbody.innerHTML = patients.map(p => {
      const pClass = `badge-${p.currentPriority.toLowerCase()}`;
      return `
        <tr>
          <td><strong style="color:var(--primary);">${p.patientCode}</strong></td>
          <td>
            <strong>${p.fullName}</strong>
            <div style="font-size:0.75rem; color:var(--text-muted);">${p.age} yrs • ${p.gender}</div>
          </td>
          <td><span class="badge ${pClass}">${p.currentPriority}</span></td>
          <td style="max-width:220px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${p.symptoms}</td>
          <td>
            <button class="btn btn-sm btn-outline" onclick="App.openRecheckModal('${p.patientId}')">Re-check</button>
            <button class="btn btn-sm btn-primary" onclick="App.openBedAllocationModal('${p.patientId}')">Assign Bed</button>
          </td>
        </tr>
      `;
    }).join('');
  },

  renderDashboardAlerts(alerts) {
    const container = document.getElementById('dashboardAlertsList');
    if (!container) return;

    if (!alerts || alerts.length === 0) {
      container.innerHTML = `<div style="padding:16px; text-align:center; color:var(--text-muted); font-size:0.86rem;">No unresolved emergency alerts. All clear.</div>`;
      return;
    }

    container.innerHTML = alerts.slice(0, 4).map(a => {
      const sevClass = a.severity === 'CRITICAL' ? 'border-left: 4px solid var(--p1-critical);' : 'border-left: 4px solid var(--p2-high);';
      return `
        <div style="background:#ffffff; border:1px solid var(--border); border-radius:var(--radius-md); padding:12px 14px; margin-bottom:10px; ${sevClass}">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <strong style="font-size:0.86rem; color:var(--text-main);">${a.title}</strong>
            <button class="btn btn-sm btn-outline" onclick="App.resolveAlert('${a.alertId}')">Resolve</button>
          </div>
          <p style="font-size:0.78rem; color:var(--text-muted); margin-top:4px;">${a.message}</p>
        </div>
      `;
    }).join('');
  },

  // --------------------------------------------------------------------------
  // Charts (Chart.js)
  // --------------------------------------------------------------------------
  renderForecastChart(forecast) {
    const canvas = document.getElementById('forecastChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (this.forecastChart) {
      this.forecastChart.destroy();
    }

    const labels = forecast?.hourLabels || ['Now', '+1h', '+2h', '+3h', '+4h'];
    const pcts = forecast?.hourlyOccupancyPercentages || [84.0, 86.0, 89.0, 91.0, 92.0];

    this.forecastChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Projected Bed Occupancy (%)',
            data: pcts,
            borderColor: '#0284c7',
            backgroundColor: 'rgba(2, 132, 199, 0.12)',
            fill: true,
            tension: 0.35,
            pointBackgroundColor: '#0284c7',
            pointRadius: 5,
            pointHoverRadius: 7
          },
          {
            label: 'Busy Threshold (80%)',
            data: labels.map(() => 80),
            borderColor: '#f59e0b',
            borderDash: [5, 5],
            fill: false,
            pointRadius: 0
          },
          {
            label: 'Surge Threshold (95%)',
            data: labels.map(() => 95),
            borderColor: '#e11d48',
            borderDash: [5, 5],
            fill: false,
            pointRadius: 0
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top', labels: { boxWidth: 12, font: { size: 11 } } },
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.dataset.label}: ${ctx.raw}%`
            }
          }
        },
        scales: {
          y: {
            min: 50,
            max: 100,
            ticks: { callback: v => v + '%' }
          }
        }
      }
    });
  },

  renderTriageDonutChart(stats) {
    const canvas = document.getElementById('triageDonutChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (this.triageChart) {
      this.triageChart.destroy();
    }

    const p1 = stats.criticalPatientsCount || 2;
    const p2 = stats.highPriorityPatientsCount || 3;
    const p3 = stats.moderatePatientsCount || 2;
    const p4 = stats.lowPriorityPatientsCount || 1;

    this.triageChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['P1 Critical', 'P2 High', 'P3 Moderate', 'P4 Low'],
        datasets: [{
          data: [p1, p2, p3, p4],
          backgroundColor: ['#e11d48', '#ea580c', '#d97706', '#10b981'],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } }
        },
        cutout: '65%'
      }
    });
  },

  // --------------------------------------------------------------------------
  // Module 3 & 4: Patient Registration & Live Triage Preview
  // --------------------------------------------------------------------------
  initTriageLivePreview() {
    const inputs = ['regHeartRate', 'regSpo2', 'regSystolicBp', 'regDiastolicBp', 'regSymptoms'];
    inputs.forEach(id => {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener('input', () => this.updateLiveTriageCalculation());
      }
    });
    this.updateLiveTriageCalculation();
  },

  updateLiveTriageCalculation() {
    const hr = parseInt(document.getElementById('regHeartRate')?.value) || 80;
    const spo2 = parseInt(document.getElementById('regSpo2')?.value) || 98;
    const sys = parseInt(document.getElementById('regSystolicBp')?.value) || 120;
    const symptoms = document.getElementById('regSymptoms')?.value || '';

    // HR points (0-3)
    let hrPts = 0;
    if (hr < 40 || hr >= 140) hrPts = 3;
    else if (hr < 50 || hr >= 120) hrPts = 2;
    else if (hr < 60 || hr >= 101) hrPts = 1;

    // SpO2 points (0-3)
    let spo2Pts = 0;
    if (spo2 < 88) spo2Pts = 3;
    else if (spo2 <= 91) spo2Pts = 2;
    else if (spo2 <= 94) spo2Pts = 1;

    // BP points (0-3)
    let bpPts = 0;
    if (sys >= 180 || sys < 80) bpPts = 3;
    else if (sys >= 160 || sys < 90) bpPts = 2;
    else if (sys >= 140 || sys < 100) bpPts = 1;

    // Symptoms points (0-3)
    let symPts = 0;
    const s = symptoms.toLowerCase();
    if (s.includes('chest pain') || s.includes('stroke') || s.includes('arrest') || s.includes('unconscious') || s.includes('stemi')) {
      symPts = 3;
    } else if (s.includes('asthma') || s.includes('wheezing') || s.includes('severe') || s.includes('fracture') || s.includes('peritonitis')) {
      symPts = 2;
    } else if (s.includes('fever') || s.includes('dizziness') || s.includes('vomit') || s.includes('pain')) {
      symPts = 1;
    }

    const total = hrPts + spo2Pts + bpPts + symPts;
    let prio = 'P4';
    let prioLabel = 'Low Urgency';
    let prioColor = 'var(--p4-low)';
    let meterColor = '#10b981';

    if (total >= 8) {
      prio = 'P1';
      prioLabel = 'Critical (Immediate Care)';
      prioColor = 'var(--p1-critical)';
      meterColor = '#e11d48';
    } else if (total >= 5) {
      prio = 'P2';
      prioLabel = 'High Urgency (15 min)';
      prioColor = 'var(--p2-high)';
      meterColor = '#ea580c';
    } else if (total >= 3) {
      prio = 'P3';
      prioLabel = 'Moderate Urgency (60 min)';
      prioColor = 'var(--p3-moderate)';
      meterColor = '#d97706';
    }

    // Update Live UI
    const scoreNum = document.getElementById('previewScoreNum');
    const badge = document.getElementById('previewPriorityBadge');
    const fill = document.getElementById('previewScoreFill');

    if (scoreNum) scoreNum.textContent = total;
    if (badge) {
      badge.textContent = `${prio} - ${prioLabel}`;
      badge.className = `badge badge-${prio.toLowerCase()}`;
    }
    if (fill) {
      const pct = Math.min(100, Math.round((total / 12) * 100));
      fill.style.width = `${pct}%`;
      fill.style.backgroundColor = meterColor;
    }

    // Factor breakdown
    const elHr = document.getElementById('previewHrPts');
    const elSpo2 = document.getElementById('previewSpo2Pts');
    const elBp = document.getElementById('previewBpPts');
    const elSym = document.getElementById('previewSymPts');

    if (elHr) elHr.textContent = `${hrPts} pts`;
    if (elSpo2) elSpo2.textContent = `${spo2Pts} pts`;
    if (elBp) elBp.textContent = `${bpPts} pts`;
    if (elSym) elSym.textContent = `${symPts} pts`;
  },

  async handlePatientRegistration(e) {
    e.preventDefault();

    const data = {
      fullName: document.getElementById('regFullName').value.trim(),
      age: parseInt(document.getElementById('regAge').value),
      gender: document.getElementById('regGender').value,
      contactNumber: document.getElementById('regContact').value.trim(),
      emergencyContact: document.getElementById('regEmergencyContact').value.trim(),
      symptoms: document.getElementById('regSymptoms').value.trim(),
      heartRate: parseInt(document.getElementById('regHeartRate').value),
      spo2: parseInt(document.getElementById('regSpo2').value),
      systolicBp: parseInt(document.getElementById('regSystolicBp').value),
      diastolicBp: parseInt(document.getElementById('regDiastolicBp').value),
      staffId: ApiClient.currentUser?.staffId || 'stf-001'
    };

    if (!data.fullName || !data.contactNumber || !data.symptoms) {
      this.showToast('Please complete all required fields.', 'error');
      return;
    }

    const saved = await ApiClient.registerPatient(data);
    if (saved) {
      this.showToast(`Patient ${saved.fullName} registered successfully! Assigned Priority ${saved.currentPriority}.`, 'success');
      this.resetRegistrationForm();
      this.navigateTo('waiting-queue');
    }
  },

  resetRegistrationForm() {
    const form = document.getElementById('patientRegistrationForm');
    if (form) form.reset();
    this.updateLiveTriageCalculation();
  },

  // --------------------------------------------------------------------------
  // Module 5: Re-Check Waiting Patients (Escalation Detection)
  // --------------------------------------------------------------------------
  async openRecheckModal(patientId) {
    const patients = await ApiClient.getPatients();
    const p = patients.find(x => x.patientId === patientId);
    if (!p) return;

    document.getElementById('recheckPatientId').value = p.patientId;
    document.getElementById('recheckPatientName').textContent = `${p.fullName} (${p.patientCode})`;
    document.getElementById('recheckOldPriority').textContent = p.currentPriority;
    document.getElementById('recheckOldPriority').className = `badge badge-${p.currentPriority.toLowerCase()}`;

    // Fill current symptoms
    const symInput = document.getElementById('recheckSymptoms');
    if (symInput) symInput.value = p.symptoms;

    this.openModal('recheckModal');
  },

  async handleRecheckVitalsSubmit(e) {
    e.preventDefault();

    const patientId = document.getElementById('recheckPatientId').value;
    const data = {
      heartRate: parseInt(document.getElementById('recheckHeartRate').value),
      spo2: parseInt(document.getElementById('recheckSpo2').value),
      systolicBp: parseInt(document.getElementById('recheckSystolicBp').value),
      diastolicBp: parseInt(document.getElementById('recheckDiastolicBp').value),
      updatedSymptoms: document.getElementById('recheckSymptoms').value.trim(),
      staffId: ApiClient.currentUser?.staffId
    };

    const res = await ApiClient.recheckPatient(patientId, data);
    this.closeAllModals();

    if (res) {
      if (res.clinicalExplanation?.includes('ESCALATED') || res.clinicalExplanation?.includes('DETERIORATION')) {
        this.showToast(`CRITICAL ESCALATION: Patient priority increased to ${res.calculatedPriority}! System alert triggered.`, 'warning');
      } else {
        this.showToast(`Vitals re-check complete. Priority confirmed as ${res.calculatedPriority}.`, 'success');
      }
      this.refreshCurrentView();
    }
  },

  // --------------------------------------------------------------------------
  // Module 6: Bed Management & Multi-Criteria Recommendation
  // --------------------------------------------------------------------------
  async loadBedBoard() {
    const beds = await ApiClient.getBeds();
    const wards = await ApiClient.getWards();

    // Render Ward tabs
    this.renderWardTabs(wards);

    // Render Bed Cards
    this.renderBedCards(beds);
  },

  renderWardTabs(wards) {
    const container = document.getElementById('bedWardTabs');
    if (!container) return;

    container.innerHTML = `
      <button class="tab-btn ${this.activeWardFilter === 'ALL' ? 'active' : ''}" onclick="App.filterBedsByWard('ALL')">All Wards (${wards.length})</button>
      ${wards.map(w => `
        <button class="tab-btn ${this.activeWardFilter === w.wardId ? 'active' : ''}" onclick="App.filterBedsByWard('${w.wardId}')">${w.wardName}</button>
      `).join('')}
    `;
  },

  filterBedsByWard(wardId) {
    this.activeWardFilter = wardId;
    this.loadBedBoard();
  },

  filterBedsByStatus(status) {
    this.activeBedStatusFilter = status;
    this.loadBedBoard();
  },

  renderBedCards(beds) {
    const container = document.getElementById('bedCardsGrid');
    if (!container) return;

    let filtered = beds;
    if (this.activeWardFilter !== 'ALL') {
      filtered = filtered.filter(b => b.ward?.wardId === this.activeWardFilter);
    }
    if (this.activeBedStatusFilter !== 'ALL') {
      filtered = filtered.filter(b => b.status === this.activeBedStatusFilter);
    }

    if (filtered.length === 0) {
      container.innerHTML = `<div style="grid-column: 1/-1; padding:32px; text-align:center; color:var(--text-muted);">No beds match selected filters.</div>`;
      return;
    }

    container.innerHTML = filtered.map(b => {
      const statusBadge = `badge-${b.status.toLowerCase()}`;
      const patientLabel = b.currentPatientId ? `<div style="font-size:0.75rem; color:var(--primary); font-weight:600; margin-top:6px;">Patient: ${b.currentPatientId}</div>` : '';

      return `
        <div class="bed-card">
          <div class="bed-header">
            <span class="bed-code">${b.bedNumber}</span>
            <span class="badge ${statusBadge}">${b.status}</span>
          </div>
          <div style="font-size:0.78rem; color:var(--text-muted);">${b.ward?.wardName || 'Ward'}</div>
          ${patientLabel}
          <div class="bed-equipment-list">
            <span class="equip-pill ${b.hasVentilator ? 'active' : ''}">Ventilator</span>
            <span class="equip-pill ${b.hasCardiacMonitor ? 'active' : ''}">Monitor</span>
            <span class="equip-pill ${b.hasOxygenSupply ? 'active' : ''}">O2</span>
            ${b.hasDialysisPort ? `<span class="equip-pill active">Dialysis</span>` : ''}
          </div>
          <div style="display:flex; gap:6px; margin-top:12px;">
            ${b.status === 'AVAILABLE' ? `<button class="btn btn-sm btn-primary" style="flex:1;" onclick="App.openBedAllocationModal(null, '${b.bedId}')">Assign</button>` : ''}
            ${b.status === 'OCCUPIED' ? `<button class="btn btn-sm btn-outline" style="flex:1;" onclick="App.updateBedState('${b.bedId}', 'CLEANING')">Discharge</button>` : ''}
            ${b.status === 'CLEANING' ? `<button class="btn btn-sm btn-secondary" style="flex:1;" onclick="App.updateBedState('${b.bedId}', 'AVAILABLE')">Ready</button>` : ''}
          </div>
        </div>
      `;
    }).join('');
  },

  async updateBedState(bedId, newStatus) {
    await ApiClient.updateBedStatus(bedId, newStatus);
    this.showToast(`Bed status updated to ${newStatus} via State Pattern.`, 'info');
    this.loadBedBoard();
  },

  // Multi-Criteria Bed Allocation Modal
  async openBedAllocationModal(patientId, preselectedBedId) {
    const queue = await ApiClient.getWaitingQueue();
    const select = document.getElementById('assignModalPatientSelect');
    if (!select) return;

    select.innerHTML = queue.map(p => `
      <option value="${p.patientId}" ${p.patientId === patientId ? 'selected' : ''}>
        ${p.fullName} (${p.patientCode}) - ${p.currentPriority}
      </option>
    `).join('');

    const targetPatientId = patientId || select.value;
    if (targetPatientId) {
      await this.runAiBedRecommendation(targetPatientId, preselectedBedId);
    }

    select.onchange = (e) => this.runAiBedRecommendation(e.target.value);
    this.openModal('bedAssignModal');
  },

  async runAiBedRecommendation(patientId, preselectedBedId) {
    const container = document.getElementById('aiRecommendationResults');
    if (!container) return;

    container.innerHTML = `<div style="padding:16px; text-align:center; color:var(--text-muted);">Running Strategy ranking algorithm (40% priority + 30% equipment + 20% ward + 10% distance)...</div>`;

    const recommendations = await ApiClient.recommendBeds(patientId);

    if (!recommendations || recommendations.length === 0) {
      container.innerHTML = `<div class="alert alert-warning" style="padding:12px; background:#fff1f2; border:1px solid #fecdd3; border-radius:var(--radius-md); color:#be123c;">
        <strong>NO SUITABLE BED AVAILABLE:</strong> All matching beds are currently occupied. Hospital surge alert triggered.
      </div>`;
      return;
    }

    container.innerHTML = recommendations.slice(0, 5).map((rec, index) => {
      const isTop = index === 0;
      return `
        <div style="background:${isTop ? '#f0fdf4' : '#ffffff'}; border:1px solid ${isTop ? '#86efac' : 'var(--border)'}; border-radius:var(--radius-md); padding:12px 14px; margin-bottom:8px; display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="display:flex; align-items:center; gap:8px;">
              <strong style="font-size:0.92rem; color:var(--text-main);">${rec.bed.bedNumber}</strong>
              <span class="badge" style="background:#0284c7; color:white;">Fit: ${rec.totalFitScore}%</span>
              ${isTop ? `<span class="badge" style="background:#16a34a; color:white;">Top Recommendation</span>` : ''}
            </div>
            <div style="font-size:0.76rem; color:var(--text-muted); margin-top:2px;">
              Priority fit: ${rec.priorityFitScore}% • Equip: ${rec.equipmentScore}% • Ward: ${rec.wardTypeScore}% • Distance: ${rec.distanceScore}%
            </div>
          </div>
          <button class="btn btn-sm btn-primary" onclick="App.confirmBedAssignment('${rec.bed.bedId}', '${patientId}')">Assign</button>
        </div>
      `;
    }).join('');
  },

  async confirmBedAssignment(bedId, patientId) {
    await ApiClient.assignBed(bedId, patientId, 'Allocated via OOAD Strategy recommendation');
    this.closeAllModals();
    this.showToast('Bed allocated and marked OCCUPIED via State Pattern!', 'success');
    this.refreshCurrentView();
  },

  // --------------------------------------------------------------------------
  // Module 7 & 8: Hospital Mode & 4-Hour Forecast Simulator
  // --------------------------------------------------------------------------
  setupForecastSimulator() {
    const admSlider = document.getElementById('forecastAdmissionsInput');
    const disSlider = document.getElementById('forecastDischargesInput');

    if (admSlider && disSlider) {
      const update = () => {
        const admissions = parseInt(admSlider.value);
        const discharges = parseInt(disSlider.value);
        document.getElementById('forecastAdmissionsVal').textContent = admissions;
        document.getElementById('forecastDischargesVal').textContent = discharges;
        this.runForecastSimulation(admissions, discharges);
      };
      admSlider.addEventListener('input', update);
      disSlider.addEventListener('input', update);
    }
  },

  async loadForecastView() {
    this.runForecastSimulation(null, null);
  },

  async runForecastSimulation(admissions, discharges) {
    const forecast = await ApiClient.getForecast(admissions, discharges);
    if (!forecast) return;

    document.getElementById('forecastCurrentOccupied').textContent = forecast.currentOccupiedBeds;
    document.getElementById('forecastTotalCapacity').textContent = forecast.totalCapacity;
    document.getElementById('forecastProjectedOccupied').textContent = forecast.projectedOccupiedBeds;
    document.getElementById('forecastProjectedPct').textContent = `${forecast.projectedOccupancyPercentage}%`;

    const warningBox = document.getElementById('forecastWarningBox');
    if (warningBox) {
      warningBox.textContent = forecast.warningMessage;
      warningBox.className = forecast.projectedOccupancyPercentage >= 95
        ? 'alert alert-danger'
        : forecast.projectedOccupancyPercentage >= 80 ? 'alert alert-warning' : 'alert alert-info';
    }

    this.renderForecastChart(forecast);
  },

  // --------------------------------------------------------------------------
  // Module 9: Doctor Override
  // --------------------------------------------------------------------------
  async loadDoctorOverrideView() {
    const patients = await ApiClient.getPatients();
    const select = document.getElementById('overridePatientSelect');
    if (!select) return;

    select.innerHTML = `<option value="">-- Select Patient to Override --</option>` +
      patients.map(p => `
        <option value="${p.patientId}">
          ${p.fullName} (${p.patientCode}) - Current: ${p.currentPriority}
        </option>
      `).join('');

    select.onchange = (e) => {
      const chosen = patients.find(x => x.patientId === e.target.value);
      if (chosen) {
        document.getElementById('overrideCurrentPrio').textContent = chosen.currentPriority;
        document.getElementById('overrideCurrentPrio').className = `badge badge-${chosen.currentPriority.toLowerCase()}`;
      }
    };
  },

  async handleDoctorOverrideSubmit(e) {
    e.preventDefault();

    if (ApiClient.currentUser?.role !== 'DOCTOR' && ApiClient.currentUser?.role !== 'ADMINISTRATOR') {
      this.showToast('Access restricted: Only DOCTOR or ADMINISTRATOR can execute priority overrides.', 'error');
      return;
    }

    const patientId = document.getElementById('overridePatientSelect').value;
    const newPriority = document.getElementById('overrideNewPriority').value;
    const reason = document.getElementById('overrideMandatoryReason').value.trim();

    if (!patientId || !newPriority || reason.length < 10) {
      this.showToast('A mandatory clinical rationale (at least 10 characters) is required.', 'error');
      return;
    }

    const updated = await ApiClient.applyDoctorOverride(patientId, newPriority, reason);
    if (updated) {
      this.showToast(`Doctor Override successful: Priority changed to ${newPriority}. Saved in immutable audit log.`, 'success');
      document.getElementById('doctorOverrideForm').reset();
      this.navigateTo('waiting-queue');
    }
  },

  // --------------------------------------------------------------------------
  // Module 10: Alerts & Notifications
  // --------------------------------------------------------------------------
  async loadAlertsView() {
    const alerts = await ApiClient.getAlerts();
    const container = document.getElementById('alertsListContainer');
    if (!container) return;

    if (!alerts || alerts.length === 0) {
      container.innerHTML = `<div style="padding:32px; text-align:center; color:var(--text-muted);">No active emergency alerts. All wards running smoothly.</div>`;
      return;
    }

    container.innerHTML = alerts.map(a => {
      const badgeClass = a.severity === 'CRITICAL' ? 'badge-p1' : (a.severity === 'HIGH' ? 'badge-p2' : 'badge-p3');
      return `
        <div class="card" style="margin-bottom:14px; border-left:4px solid ${a.severity === 'CRITICAL' ? 'var(--p1-critical)' : 'var(--p2-high)'};">
          <div class="card-body" style="display:flex; justify-content:space-between; align-items:flex-start;">
            <div>
              <div style="display:flex; align-items:center; gap:8px;">
                <span class="badge ${badgeClass}">${a.severity}</span>
                <strong style="font-size:1.02rem;">${a.title}</strong>
              </div>
              <p style="margin:8px 0; color:var(--text-muted); font-size:0.88rem;">${a.message}</p>
              <div style="font-size:0.75rem; color:var(--text-light);">${new Date(a.createdAt).toLocaleString()}</div>
            </div>
            <button class="btn btn-outline" onclick="App.resolveAlert('${a.alertId}')">Resolve Alert</button>
          </div>
        </div>
      `;
    }).join('');
  },

  async resolveAlert(alertId) {
    await ApiClient.resolveAlert(alertId);
    this.showToast('Alert resolved and marked resolved in database.', 'info');
    this.refreshCurrentView();
  },

  // --------------------------------------------------------------------------
  // Waiting Queue View
  // --------------------------------------------------------------------------
  async loadWaitingQueue() {
    const queue = await ApiClient.getWaitingQueue();
    const tbody = document.getElementById('waitingQueueTableBody');
    if (!tbody) return;

    if (queue.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center" style="padding:28px; color:var(--text-muted);">No patients currently waiting in queue.</td></tr>`;
      return;
    }

    tbody.innerHTML = queue.map((p, index) => {
      const prioClass = `badge-${p.currentPriority.toLowerCase()}`;
      const elapsedMins = Math.max(1, Math.round((Date.now() - new Date(p.registrationTime)) / 60000));

      return `
        <tr>
          <td><span style="font-weight:800; color:var(--text-muted);">#${index + 1}</span></td>
          <td><strong style="color:var(--primary);">${p.patientCode}</strong></td>
          <td>
            <strong>${p.fullName}</strong>
            <div style="font-size:0.75rem; color:var(--text-muted);">${p.age} yrs • ${p.gender}</div>
          </td>
          <td><span class="badge ${prioClass}">${p.currentPriority}</span></td>
          <td>${elapsedMins} mins</td>
          <td style="max-width:260px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${p.symptoms}</td>
          <td>
            <button class="btn btn-sm btn-outline" onclick="App.openRecheckModal('${p.patientId}')">Re-check Vitals</button>
            <button class="btn btn-sm btn-primary" onclick="App.openBedAllocationModal('${p.patientId}')">Assign Bed</button>
          </td>
        </tr>
      `;
    }).join('');
  },

  // --------------------------------------------------------------------------
  // Patient Records View
  // --------------------------------------------------------------------------
  async loadPatientRecords() {
    const patients = await ApiClient.getPatients();
    this.renderPatientRecordsTable(patients);
  },

  renderPatientRecordsTable(patients) {
    const tbody = document.getElementById('patientRecordsTableBody');
    if (!tbody) return;

    tbody.innerHTML = patients.map(p => {
      const prioClass = `badge-${p.currentPriority.toLowerCase()}`;
      return `
        <tr>
          <td><strong style="color:var(--primary);">${p.patientCode}</strong></td>
          <td><strong>${p.fullName}</strong></td>
          <td>${p.age}</td>
          <td>${p.gender}</td>
          <td>${p.contactNumber}</td>
          <td><span class="badge ${prioClass}">${p.currentPriority}</span></td>
          <td><span class="badge badge-outline">${p.currentStatus}</span></td>
          <td>${new Date(p.registrationTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</td>
        </tr>
      `;
    }).join('');
  },

  async filterPatientsTable(query) {
    const patients = await ApiClient.getPatients();
    if (!query) {
      this.renderPatientRecordsTable(patients);
      return;
    }
    const q = query.toLowerCase();
    const filtered = patients.filter(p =>
      p.fullName.toLowerCase().includes(q) ||
      p.patientCode.toLowerCase().includes(q) ||
      p.symptoms.toLowerCase().includes(q)
    );
    this.renderPatientRecordsTable(filtered);
  },

  // --------------------------------------------------------------------------
  // Audit Logs View
  // --------------------------------------------------------------------------
  async loadAuditLogs(actionFilter) {
    const logs = await ApiClient.getAuditLogs(actionFilter);
    const tbody = document.getElementById('auditLogsTableBody');
    if (!tbody) return;

    if (!logs || logs.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center" style="padding:28px; color:var(--text-muted);">No audit log entries found.</td></tr>`;
      return;
    }

    tbody.innerHTML = logs.map(l => {
      return `
        <tr>
          <td style="white-space:nowrap; font-size:0.78rem; color:var(--text-muted);">${new Date(l.createdAt).toLocaleString()}</td>
          <td><strong style="font-size:0.8rem; color:var(--primary);">${l.actionType}</strong></td>
          <td>${l.entityName} (${l.entityId})</td>
          <td>
            <strong>${l.staffName}</strong>
            <div style="font-size:0.72rem; color:var(--text-muted);">${l.staffRole}</div>
          </td>
          <td>
            <div style="font-size:0.78rem; color:var(--text-muted);">Old: ${l.oldValue || '—'}</div>
            <div style="font-size:0.78rem; font-weight:600; color:var(--text-main);">New: ${l.newValue || '—'}</div>
          </td>
          <td style="max-width:240px; font-size:0.82rem;">${l.mandatoryReason || '—'}</td>
        </tr>
      `;
    }).join('');
  },

  exportAuditLogsToCsv() {
    ApiClient.getAuditLogs().then(logs => {
      let csv = 'Timestamp,Action,Entity,Staff,Role,Old Value,New Value,Reason\n';
      logs.forEach(l => {
        csv += `"${l.createdAt}","${l.actionType}","${l.entityName}","${l.staffName}","${l.staffRole}","${l.oldValue || ''}","${l.newValue || ''}","${(l.mandatoryReason || '').replace(/"/g, '""')}"\n`;
      });
      const blob = new Blob([csv], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `hospital_audit_log_${Date.now()}.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  },

  // --------------------------------------------------------------------------
  // Staff Directory View
  // --------------------------------------------------------------------------
  async loadStaffDirectory() {
    const staffList = await ApiClient.getStaff();
    const grid = document.getElementById('staffGridContainer');
    if (!grid) return;

    grid.innerHTML = staffList.map(s => {
      return `
        <div class="card" style="padding:18px;">
          <div style="display:flex; align-items:center; gap:12px; margin-bottom:12px;">
            <div class="user-avatar" style="width:44px; height:44px; font-size:1.1rem;">${s.fullName.charAt(0)}</div>
            <div>
              <strong style="font-size:0.95rem;">${s.fullName}</strong>
              <div style="font-size:0.76rem; color:var(--text-muted);">${s.employeeCode} • ${s.department}</div>
            </div>
          </div>
          <span class="user-role-badge role-${s.role.toLowerCase()}">${s.role}</span>
          <div style="margin-top:12px; font-size:0.8rem; color:var(--text-muted);">
            <div>Email: ${s.email}</div>
            <div>Status: <span style="color:#10b981; font-weight:600;">Active On-Duty</span></div>
          </div>
        </div>
      `;
    }).join('');
  },

  // --------------------------------------------------------------------------
  // Settings View
  // --------------------------------------------------------------------------
  loadSettingsView() {
    const apiIndicator = document.getElementById('apiStatusIndicator');
    if (apiIndicator) {
      apiIndicator.textContent = ApiClient.isLiveApiAvailable ? 'Online (Spring Boot + MySQL/H2)' : 'Offline / Standalone Preview Mode';
      apiIndicator.style.color = ApiClient.isLiveApiAvailable ? '#10b981' : '#f59e0b';
    }
  },

  // --------------------------------------------------------------------------
  // Modal & Toast Helpers
  // --------------------------------------------------------------------------
  openModal(modalId) {
    this.closeAllModals();
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('show');
  },

  closeAllModals() {
    document.querySelectorAll('.modal-backdrop').forEach(m => m.classList.remove('show'));
  },

  showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <div style="flex:1; font-size:0.86rem; font-weight:500;">${message}</div>
      <button style="background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:1rem;" onclick="this.parentElement.remove()">&times;</button>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.remove();
    }, 4500);
  }
};
