# AI-Enabled Hospital Resource and Emergency Triage System
### CSA1104 – Object Oriented Analysis and Design (OOAD) Project

![Java](https://img.shields.io/badge/Java-17%20LTS-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20JS%20%7C%20Chart.js-E34F26)
![OOAD Patterns](https://img.shields.io/badge/Design%20Patterns-Strategy%20%7C%20State%20%7C%20Observer-blueviolet)

---

## 1. Project Overview

The **AI-Enabled Hospital Resource and Emergency Triage System** is an enterprise-grade hospital emergency management platform designed for the academic course **CSA1104 – Object Oriented Analysis and Design**.

It assists emergency department doctors, triage nurses, and hospital administrators in:
- Rapid emergency patient registration with automated Patient ID generation (`PAT-YYYYMMDD-XXXX`).
- Multi-factor physiological triage priority calculation (0 to 12 points across Heart Rate, SpO2, Blood Pressure, and Symptoms).
- Dynamic priority classification: **P1 (Critical: 8+)**, **P2 (High: 5–7)**, **P3 (Moderate: 3–4)**, **P4 (Low: 0–2)**.
- Continuous vital signs re-checking with **automatic clinical deterioration and urgency escalation detection**.
- Specialized bed recommendation using a weighted multi-criteria strategy (40% Priority + 30% Equipment + 20% Ward + 10% Distance).
- Hospital capacity mode management (**Normal < 80%**, **Busy &ge; 80%**, **Surge &ge; 95% or 5 urgent cases/hr**).
- Four-hour bed occupancy mathematical forecasting with interactive capacity warnings.
- Doctor Priority Overrides with mandatory clinical justification and immutable audit trail logging.

> **Clinical Decision Support Notice:** The system supports clinical staff in decision-making and does **not** replace professional medical judgement. Scoring rules are configurable for academic demonstration.

---

## 2. Technology Stack

### Frontend:
- **HTML5 & CSS3:** Custom modern healthcare design system with deep navy, teal, and slate palette, rounded-2xl cards, and responsive desktop/tablet/mobile layouts.
- **JavaScript (ES6+):** Pure reactive asynchronous DOM controller, real-time formula calculations, and toast notification engine.
- **Chart.js:** 4-hour bed forecast projection line chart and triage priority distribution donut chart.

### Backend:
- **Java 17 (LTS):** Clean layered architecture with clean entity-service-controller separation.
- **Spring Boot 3.2.5:** High-performance RESTful API microservice.
- **Spring Data JPA & Hibernate:** ORM mapping, parameterized queries preventing SQL injection, transaction management.
- **Bean Validation (Jakarta Validation):** Robust validation rules for vital ranges, phone numbers, and mandatory reasons.

### Database:
- **MySQL 8.0:** Primary production database with 8 normalized tables, foreign keys, constraints, and indexes.
- **H2 In-Memory Database:** Built-in MySQL-compatible runtime fallback for instant zero-configuration demonstration.

---

## 3. Object-Oriented Design (OOAD) Patterns

| Pattern | Interface & Classes | Architectural Role |
| :--- | :--- | :--- |
| **Strategy Pattern** | `TriageScoringStrategy`<br>&bull; `StandardAITriageStrategy`<br>&bull; `PediatricTriageStrategy` | Encapsulates pluggable triage scoring algorithms. Normalizes pediatric heart rate baselines for young patients. |
| **Strategy Pattern** | `BedRankingStrategy`<br>&bull; `WeightedMultiCriteriaBedStrategy` | Implements multi-criteria weighted bed recommendation: `0.40 * Priority + 0.30 * Equipment + 0.20 * Ward + 0.10 * Distance`. |
| **State Pattern** | `BedState`<br>&bull; `AvailableBedState`<br>&bull; `ReservedBedState`<br>&bull; `OccupiedBedState`<br>&bull; `CleaningBedState` | Enforces medical sanitization and bed occupancy lifecycle transitions. Releasing an occupied bed forces it into `CLEANING` state. |
| **State Pattern** | `HospitalModeState`<br>&bull; `NormalModeState`<br>&bull; `BusyModeState`<br>&bull; `SurgeModeState` | Dynamically evaluates hospital capacity state based on real-time occupancy percentages and hourly surge arrivals. |
| **Observer Pattern** | `HospitalEventObserver`<br>&bull; `EmergencyAlertObserver`<br>&bull; `WaitingQueueObserver`<br>&bull; `DashboardUpdateObserver` | Decoupled event publishing when patients deteriorate, P1 cases arrive, or hospital surge thresholds are breached. |

---

## 4. Project Directory Structure

```
ooad prj/
├── pom.xml                                   # Maven project descriptor (Spring Boot 3.2.5)
├── mvnw.cmd                                  # Windows Maven wrapper launcher
├── schema.sql                                # MySQL 8.0 DDL Schema (8 tables)
├── data.sql                                  # Demonstration seed dataset
├── README.md                                 # Complete documentation & run instructions
├── docs/
│   ├── UML_Class_Diagram.md                  # Comprehensive UML Class diagram & Mermaid
│   ├── UML_UseCase_Diagram.md                # UML Use Case diagram & actor specifications
│   ├── Database_ER_Diagram.md                # Database ER diagram & table dictionary
│   └── OOAD_Architecture_Report.md           # Formal OOAD design patterns & principles report
├── frontend/                                 # Standalone static distribution
│   ├── index.html                            # Modern Healthcare SaaS Web Application
│   ├── css/
│   │   └── style.css                         # Healthcare Design System styling
│   └── js/
│       ├── api.js                            # Dual-mode API client (REST + Mock fallback)
│       └── app.js                            # Interactive UI application controller
└── src/
    ├── main/
    │   ├── java/com/hospital/triage/
    │   │   ├── HospitalTriageApplication.java# Spring Boot Application entrypoint
    │   │   ├── config/WebConfig.java         # CORS & Static resource mappings
    │   │   ├── controller/                   # REST API Controllers (10 controllers)
    │   │   ├── dto/                          # Request / Response Data Transfer Objects
    │   │   ├── model/                        # JPA Entities (8 entities) & Enums
    │   │   ├── patterns/
    │   │   │   ├── observer/                 # Subject & Observers
    │   │   │   ├── state/                    # BedState & HospitalModeState
    │   │   │   └── strategy/                 # Triage & Bed Ranking Strategies
    │   │   ├── repository/                   # Spring Data JPA Repositories
    │   │   └── service/                      # Business Logic Services
    │   └── resources/
    │       ├── application.properties        # Default configuration (H2 zero-setup mode)
    │       ├── application-mysql.properties  # MySQL 8.0 profile configuration
    │       ├── schema.sql                    # Classpath initialization schema
    │       ├── data.sql                      # Classpath demonstration dataset
    │       └── static/                       # Spring Boot static web assets
    └── test/
        └── java/com/hospital/triage/         # JUnit 5 & Spring Boot test suite
            ├── TriageStrategyTest.java       # Strategy pattern test cases
            ├── BedRankingStrategyTest.java   # Multi-criteria bed ranking test cases
            ├── BedStatePatternTest.java      # Bed lifecycle state transition tests
            └── HospitalModeStateTest.java    # Hospital mode state transition tests
```

---

## 5. Step-by-Step Setup & How to Run

### Method 1: Instant Standalone Browser Preview (Zero Setup)
No build tools or database server required to inspect the UI!
1. Open the project folder in File Explorer:
   `c:\Users\jaish\Downloads\ooad prj\frontend`
2. Double-click **`index.html`** to open in Google Chrome, Microsoft Edge, or Firefox.
3. The built-in client data engine (`js/api.js`) seamlessly powers all 13 modules, live triage calculations, 4-hour forecasts, bed allocation, and doctor overrides out of the box!

---

### Method 2: Run with Spring Boot (Default Embedded H2 Database)
This mode runs the complete Spring Boot backend with preloaded sample data in zero-setup mode:
```powershell
cd "c:\Users\jaish\Downloads\ooad prj"
mvn spring-boot:run
```
Once started:
- **Hospital Web Dashboard:** `http://localhost:8080/`
- **REST API Base:** `http://localhost:8080/api/dashboard/stats`
- **H2 Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:hospital_triage_db`, User: `sa`, Password: empty)

---

### Method 3: Run with Local MySQL 8.0 Database Server
1. Start your local MySQL service (e.g. via MySQL Workbench or Windows Services).
2. Execute `schema.sql` and `data.sql` to initialize `hospital_triage_db`:
   ```powershell
   mysql -u root -p < schema.sql
   mysql -u root -p < data.sql
   ```
3. Update database credentials in `src/main/resources/application-mysql.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hospital_triage_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```
4. Run Spring Boot with the `mysql` profile:
   ```powershell
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 6. Running Test Cases

Run the automated JUnit 5 test suite verifying all OOAD patterns and scoring algorithms:
```powershell
mvn test
```
The test suite validates:
- `TriageStrategyTest`: Verifies P1, P2, P3, P4 physiological threshold cutoffs and pediatric adjustments.
- `BedRankingStrategyTest`: Verifies the 40-30-20-10 multi-criteria bed ranking algorithm.
- `BedStatePatternTest`: Verifies `AVAILABLE -> RESERVED -> OCCUPIED -> CLEANING -> AVAILABLE` state machine transitions and invalid transition rejections.
- `HospitalModeStateTest`: Verifies `Normal -> Busy -> Surge` transitions based on occupancy and urgent arrivals.

---

## 7. Demonstration Accounts

Use the Quick Role Switcher on the sidebar or test the credentials below:

| Role | Name | Department | Email / Username | Password |
| :--- | :--- | :--- | :--- | :--- |
| **Doctor** | Dr. Marcus Vance, MD | Emergency Medicine / Trauma | `doctor` / `marcus.doctor@hospital.org` | `Password123!` |
| **Doctor** | Dr. Priya Sharma, MD | Critical Care & Cardiology | `priya.doctor@hospital.org` | `Password123!` |
| **Nurse** | Elena Rostova, RN | Emergency Department | `nurse` / `elena.nurse@hospital.org` | `Password123!` |
| **Receptionist** | Sarah Jenkins | Patient Admission | `reception` / `sarah.reception@hospital.org` | `Password123!` |
| **Administrator**| Director Arthur Pendelton | Hospital Operations | `admin` / `admin@hospital.org` | `Password123!` |

---

## 8. REST API Reference

| HTTP Method | API Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticates staff credentials and returns session token |
| `GET` | `/api/dashboard/stats` | Returns KPIs, occupancy %, mode, alerts, and recent cases |
| `GET` | `/api/dashboard/forecast` | Returns 4-hour bed occupancy forecast with admissions & discharges |
| `GET` | `/api/patients` | Retrieves all registered emergency patients |
| `GET` | `/api/patients/queue` | Retrieves waiting patients sorted strictly by urgency (`P1 > P2 > P3 > P4`) |
| `POST` | `/api/patients/register` | Registers new patient, runs initial triage, persists to MySQL |
| `POST` | `/api/triage/assess` | Calculates triage score (0–12) using the Strategy Pattern |
| `POST` | `/api/triage/recheck/{patientId}` | Updates vitals, detects deterioration, triggers escalation alert |
| `GET` | `/api/beds` | Retrieves hospital beds (supports `?status=AVAILABLE`) |
| `GET` | `/api/beds/recommend/{patientId}` | Ranks candidate beds via 4-factor Multi-Criteria Strategy |
| `POST` | `/api/beds/{bedId}/assign` | Allocates and occupies bed using State Pattern |
| `PATCH` | `/api/beds/{bedId}/status` | Updates bed state (`AVAILABLE`, `RESERVED`, `OCCUPIED`, `CLEANING`) |
| `POST` | `/api/triage/override` | Applies Doctor Priority Override with mandatory reason & audit log |
| `GET` | `/api/alerts` | Retrieves active unresolved emergency alerts |
| `PATCH` | `/api/alerts/{id}/resolve`| Resolves active alert |
| `GET` | `/api/audit-logs` | Retrieves immutable audit log entries |
| `GET` | `/api/staff` | Retrieves staff directory |
| `GET / PUT` | `/api/settings` | Gets or updates system operational thresholds |
