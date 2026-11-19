# UML Use Case Diagram
## AI-Enabled Hospital Resource and Emergency Triage System
**Course:** CSA1104 – Object Oriented Analysis and Design (OOAD)

---

### 1. Actor Identification
The system defines four human role-based actors and one automated system actor:

1. **Receptionist:** Handles preliminary patient intake, personal demographics, emergency contact logging, and primary ID generation.
2. **Nurse (Triage Staff):** Conducts physiological vitals examination (HR, SpO2, BP), monitors waiting room patients, executes periodic vitals re-checks, and identifies acute deteriorations.
3. **Doctor (Attending Physician):** Reviews priority queues, assigns clinical beds, exercises medical judgement to perform Doctor Overrides with mandatory clinical justification.
4. **Administrator (Hospital Director):** Monitors hospital operational modes (Normal, Busy, Surge), oversees staff accounts, inspects immutable audit logs, and tunes system strategy parameters.
5. **AegisHealth System Engine (Automated Actor):** Continuously monitors capacity thresholds, triggers Surge mode, calculates 4-hour forecasts, and dispatches alerts via the Observer Pattern.

---

### 2. Mermaid UML Use Case Diagram

```mermaid
flowchart LR
    %% Actors
    subgraph Actors ["Hospital Actors"]
        Receptionist(["📋 Receptionist"])
        Nurse(["👩‍⚕️ Nurse"])
        Doctor(["👨‍⚕️ Doctor"])
        Admin(["⚙️ Administrator"])
        SystemEngine(["🤖 System Monitor"])
    end

    %% Use Cases
    subgraph Boundary ["AI-Enabled Hospital Resource & Emergency Triage System"]
        UC1(["UC-01: Register Emergency Patient"])
        UC2(["UC-02: Compute Emergency Triage Score"])
        UC3(["UC-03: Re-Check Waiting Patient Vitals"])
        UC4(["UC-04: Monitor Prioritized Waiting Queue"])
        UC5(["UC-05: AI Bed Recommendation (Multi-Criteria)"])
        UC6(["UC-06: Manage Bed Lifecycle (State Pattern)"])
        UC7(["UC-07: Perform Doctor Priority Override"])
        UC8(["UC-08: Manage Hospital Operational Mode"])
        UC9(["UC-09: Simulate 4-Hour Bed Forecast"])
        UC10(["UC-10: Inspect Immutable Audit Logs"])
        UC11(["UC-11: Resolve Emergency & Urgency Alerts"])
        UC12(["UC-12: Configure System Thresholds & Weights"])
    end

    %% Connections
    Receptionist --> UC1
    UC1 ..> UC2 : <<include>>

    Nurse --> UC2
    Nurse --> UC3
    Nurse --> UC4
    Nurse --> UC11

    UC3 ..> UC4 : <<include>> Updates Order
    UC3 -.-> UC11 : <<extend>> If Priority Escalates

    Doctor --> UC4
    Doctor --> UC5
    Doctor --> UC6
    Doctor --> UC7
    Doctor --> UC11

    UC5 ..> UC6 : <<include>> Occupies Bed
    UC7 ..> UC10 : <<include>> Logs Mandatory Rationale

    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC12

    SystemEngine --> UC8
    SystemEngine --> UC9
    SystemEngine -.-> UC11 : <<trigger>> On Threshold Breach
```

---

### 3. Use Case Specifications

#### UC-01: Register Emergency Patient
- **Primary Actor:** Receptionist / Nurse
- **Preconditions:** Staff is authenticated into AegisHealth system.
- **Main Flow:**
  1. Staff inputs patient full name, age, gender, contact, and symptoms.
  2. Staff inputs baseline vital signs (Heart rate, SpO2, Blood Pressure).
  3. System automatically generates formatted patient code (`PAT-YYYYMMDD-XXXX`).
  4. System invokes `StandardAITriageStrategy` to compute initial 4-factor score (0–12).
  5. System assigns priority badge (P1–P4) and inserts record into MySQL `patients` table.
  6. Audit log entry is generated.
- **Postconditions:** Patient is queued in Waiting Room with clinical urgency score.

#### UC-03: Re-Check Waiting Patient Vitals
- **Primary Actor:** Nurse
- **Preconditions:** Patient is present in Waiting Queue.
- **Main Flow:**
  1. Nurse selects waiting patient and enters updated vital signs and symptoms.
  2. System recalculates triage score and compares with previous priority rank.
  3. If priority increases (e.g., P3 $\to$ P2 or P2 $\to$ P1), system identifies clinical deterioration.
  4. Urgency escalation alert is broadcast to all active clinical dashboards via the Observer Pattern.
  5. Patient's queue rank is instantly escalated to the front of lower priority patients.
- **Postconditions:** New triage record saved; patient rank updated; escalation alert active.

#### UC-05: Recommend Suitable Beds (Multi-Criteria Strategy)
- **Primary Actor:** Doctor / Nurse
- **Preconditions:** Patient requires inpatient or emergency bed allocation.
- **Main Flow:**
  1. Staff selects patient from Waiting Queue.
  2. System queries candidate available beds across hospital wards.
  3. System executes `WeightedMultiCriteriaBedStrategy`:
     $$\text{Total Fit} = 0.40 \times \text{Priority} + 0.30 \times \text{Equipment} + 0.20 \times \text{Ward} + 0.10 \times \text{Distance}$$
  4. System displays ranked bed list with fit percentages and equipment matches.
  5. Staff clicks "Assign Bed".
  6. Bed transitions from `AVAILABLE` to `OCCUPIED` via the State Pattern.
- **Postconditions:** Bed occupied, patient marked `BED_ASSIGNED`, audit log recorded.

#### UC-07: Execute Doctor Priority Override
- **Primary Actor:** Doctor
- **Preconditions:** Authenticated user holds `DOCTOR` or `ADMINISTRATOR` credentials.
- **Main Flow:**
  1. Doctor selects patient whose algorithmic triage score does not reflect clinical diagnosis.
  2. Doctor selects new medical priority (P1, P2, P3, or P4).
  3. System enforces mandatory clinical reasoning (minimum 10 characters).
  4. System updates patient urgency and appends an immutable entry to `audit_log` with doctor's name, old priority, new priority, and rationale.
  5. Observer Pattern updates waiting queue ranking.
- **Postconditions:** Clinical override permanently audited; patient priority adjusted.
