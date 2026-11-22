# Video Demo Script: Intelligent IT Service Management (I-ITSM) System

**Course:** Object-Oriented Programming (CS F213 / OOP)  
**Institution:** BITS Pilani  
**Repository:** [https://github.com/annibold/OOPS-Project](https://github.com/annibold/OOPS-Project)  
**Total Video Duration:** 15–20 Minutes  

---

## Project Team & Presenters

| S. No. | Presenter Name | BITS ID Number | Video Presentation Role |
|:---:|:---|:---:|:---|
| 1 | **Aniruddh Pandey** | `2027A7PS0065H` | Part 1: Architecture & System Overview, Part 6: Wrap-up |
| 2 | **Kushagr Kakkar** | `2025A7PS0071H` | Part 2: **Workflow 1** (Incident Reporting, Dynamic Triage & Assignment) |
| 3 | **Ishaan Singhal** | `2025A7PS0171H` | Part 3: **Workflow 2** (Investigation, SLA Tracking, Verification & Closure) |
| 4 | **Mitansh Gupta** | `2025A7PS0159H` | Part 4: **Workflow 3** (Intelligent Duplicates & AI Suggestions) & Part 5: Tests |

---

## Video Breakdown & Timestamps

| Segment | Timestamp | Topic / Workflow | Assigned Speaker |
|---|---|---|---|
| **Part 1** | 00:00 – 02:30 | Project Introduction, Architecture & Design Patterns | **Aniruddh Pandey** |
| **Part 2** | 02:30 – 07:30 | **Workflow 1:** Incident Submission, Auto-Triage & Assignment | **Kushagr Kakkar** |
| **Part 3** | 07:30 – 12:30 | **Workflow 2:** Investigation, SLA Monitor, Verification & Closure | **Ishaan Singhal** |
| **Part 4** | 12:30 – 16:30 | **Workflow 3:** Duplicate Detection & AI Resolution Recommendations | **Mitansh Gupta** |
| **Part 5** | 16:30 – 19:00 | Unit Test Suite (19 Tests) & Code Architecture Walkthrough | **Mitansh & Kushagr** |
| **Part 6** | 19:00 – 20:00 | Conclusion & Q&A Summary | **All Team Members** |

---

## Detailed Script & Live Demonstration Actions

### Part 1: Introduction & Architecture (00:00 – 02:30)
**Speaker: Aniruddh Pandey (`2027A7PS0065H`)**

- **Screen Setup:** Display Slide 1 / GitHub Repository (`https://github.com/annibold/OOPS-Project`).
- **Speaking Script:**
  > *"Hello everyone! We are group members Aniruddh Pandey, Kushagr Kakkar, Ishaan Singhal, and Mitansh Gupta from BITS Pilani. Today we are excited to present our Object-Oriented Programming Capstone Project: the **Intelligent IT Service Management System (I-ITSM)**.*
  >
  > *Built using modern Java 21, Spring Boot 4.1.2, Spring Security, Spring Data JPA, and Thymeleaf, our platform addresses critical enterprise IT bottlenecks: ticket dispatch delays, unmonitored SLA breaches, and redundant troubleshooting.*
  >
  > *Our architecture strictly follows the MVC pattern and embodies foundational OOP design patterns:
  > 1. The **Strategy Pattern** for hot-swappable technician assignment and priority scoring.
  > 2. The **Observer Pattern** for decoupled audit trails and multi-channel notifications.
  > 3. The **State Pattern** for strict, bug-proof lifecycle state transitions.*
  >
  > *Now, Kushagr will demonstrate our first major workflow: incident reporting, automated triage, and intelligent assignment."*

---

### Part 2: Workflow 1 — Report, Auto-Triage & Assignment (02:30 – 07:30)
**Speaker: Kushagr Kakkar (`2025A7PS0071H`)**

- **Screen Setup:** Open web browser at `http://localhost:8080/login`.
- **Action 1:** Log in with employee credentials:
  - Username: `employee@itsm.com`
  - Password: `emp123`
- **Speaking Script:**
  > *"Thank you, Aniruddh. I am Kushagr Kakkar. Here we log into the Employee Portal, characterized by its professional blue theme. Let's imagine an employee experiences a high-priority production database outage."*
- **Action 2:** Click **+ Report Issue** in the navbar. Enter:
  - **Title:** `Production Payment Gateway 500 Server Error`
  - **Category:** `DATABASE`
  - **Impact:** `High (3)`
  - **Urgency:** `Critical (3)`
  - **Business Criticality:** `3`
  - **Description:** `All customer payment checkout transactions are failing with HTTP 500 errors due to database connection pool exhaustion.`
- **Action 3:** Click **Submit Incident Report**.
- **Speaking Script:**
  > *"The moment I submit, two critical backend patterns execute instantaneously:
  > First, our `ScoreBasedPriorityStrategy` evaluates the impact, urgency, and criticality, automatically classifying this incident as **P1 Critical**.
  > Second, our `AssignmentService` evaluates candidate technicians using the `AssignmentStrategy`. It matches the ticket to Ravi Sharma (`ravi@itsm.com`), who has the lowest active workload and database specialization.*
  >
  > *Let's check the Notifications bell at the top: an immediate alert has been triggered via our Observer pattern. Now Ishaan will take over to walk through the technician investigation, SLA tracking, and resolution lifecycle."*

---

### Part 3: Workflow 2 — Investigation, SLA Tracking, Verification & Closure (07:30 – 12:30)
**Speaker: Ishaan Singhal (`2025A7PS0171H`)**

- **Screen Setup:** Log out and log in as technician `ravi@itsm.com` (password `tech123`).
- **Speaking Script:**
  > *"Hi everyone, I'm Ishaan Singhal. We are now in the Technician Engineering Console, styled with a distinct dark slate and cyan theme. In Ravi's queue, the newly assigned P1 incident appears immediately."*
- **Action 1:** Click on Incident #4.
- **Action 2:** Click **Acknowledge Incident** &rarr; status updates from `ASSIGNED` to `ACKNOWLEDGED`.
- **Action 3:** Click **Start Investigation** &rarr; status updates to `IN_PROGRESS`.
- **Action 4:** Add a diagnostic note:
  - Note: *"Inspected HikariCP pool metrics. Active connections reached ceiling limit of 10. Increasing pool size to 50."*
  - Click **Post Investigation Note**.
- **Action 5:** Provide formal resolution:
  - Textarea: *"Restarted connection pool service and reconfigured maximum active connections to 50. All end-to-end checkout calls succeeding."*
  - Click **Mark as Resolved &#10004;**. Status updates to `RESOLVED`.
- **Action 6:** Log out and log in as administrator: `admin@itsm.com` (`admin123`) &rarr; navigate to **SLA Monitor**.
- **Speaking Script:**
  > *"As administrator, we can view our automated background SLA monitor. Because the technician responded within 5 minutes and resolved the ticket within 30 minutes, our real-time engine flags the health status as **ON_TRACK**.*
  >
  > *Now, let's log back in as the reporting employee (`employee@itsm.com`) to demonstrate our new verification and rating feature."*
- **Action 7:** Open Incident #4 as employee.
- **Speaking Script:**
  > *"The employee sees the resolution details and our new Employee Verification Panel. We test our checkout, confirm it works, select a 5-Star rating (⭐ 5 - Excellent), leave the comment: 'Checkout works instantly now, great response time!', and click 'Yes — Mark as Verified'."*
- **Action 8:** Return to Administrator Portal (`admin@itsm.com`) &rarr; open Incident #4 &rarr; show the employee's 5-star rating, review comments, and complete audit trail &rarr; click **Confirm & Formally Close Incident**. Status becomes `CLOSED`.
- **Speaking Script:**
  > *"Now Mitansh will demonstrate our heuristic duplicate detection and automated solution recommendation."*

---

### Part 4: Workflow 3 — Duplicate Detection & Heuristic Suggestions (12:30 – 16:30)
**Speaker: Mitansh Gupta (`2025A7PS0159H`)**

- **Screen Setup:** Log in as `employee@itsm.com` (`emp123`).
- **Action 1:** Click **+ Report Issue** to simulate a second employee reporting a correlated problem:
  - **Title:** `Database Connection Timeout on Checkout`
  - **Category:** `DATABASE`
  - **Impact:** `High (3)` | **Urgency:** `Critical (3)` | **Business Criticality:** `3`
  - **Description:** `Users report that checkout transactions timeout when trying to query the central database.`
  - Click **Submit Incident Report**.
- **Action 2:** Log in as technician `ravi@itsm.com` or `karan@itsm.com` and open this new incident.
- **Speaking Script:**
  > *"Hello, I am Mitansh Gupta. Here is where the heuristic intelligence of our system shines. Without relying on external, slow third-party AI APIs, our custom in-memory `DuplicateDetectionService` tokenizes the title and description, filters stop words, and calculates Jaccard similarity.*
  >
  > *Notice the yellow banner at the top: **'Intelligent Alert: Potential Correlated / Duplicate Incidents'** correctly linking this ticket to Incident #4!*
  >
  > *Even better, observe the emerald green panel right below: **'AI / Heuristic Suggestion: Past Proven Solutions'**. The system surfaced Ravi's verified fix from Incident #4 with an 85%+ confidence match. The engineer can directly review the proven solution and resolve the incident in minutes."*

---

### Part 5: Test Suite Verification & Code Structure (16:30 – 19:00)
**Speakers: Mitansh Gupta (`2025A7PS0159H`) & Kushagr Kakkar (`2025A7PS0071H`)**

- **Screen Setup:** Open PowerShell terminal in `C:\Users\ANIRUDDH PANDEY\Downloads\demo\demo`.
- **Action:** Run the full test suite:
  ```powershell
  .\mvnw.cmd test
  ```
- **Speaking Script (Mitansh Gupta):**
  > *"A robust architecture must be backed by comprehensive testing. Our test suite contains 19 automated unit tests written in JUnit 5 with Mockito, validating normal flows, edge cases, SLA boundaries, and heuristic similarity matching."*
- **Screen:** Show the terminal finishing with:
  ```text
  [INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
  [INFO] BUILD SUCCESS
  ```
- **Speaking Script (Kushagr Kakkar):**
  > *"Notice that all 19 tests pass cleanly. Our codebase is organized into clean packages: `model` for entities and the state transition machine, `strategy` for assignment and priority algorithms, `observer` for event broadcasting, `service` for business logic, and `controller` for role-secured endpoints."*

---

### Part 6: Conclusion & Wrap-Up (19:00 – 20:00)
**Speakers: Aniruddh Pandey (`2027A7PS0065H`) & All Team Members**

- **Speaking Script (Aniruddh Pandey):**
  > *"To conclude, our I-ITSM platform demonstrates how foundational Object-Oriented principles — encapsulation, inheritance, polymorphism, and abstraction — combined with classic design patterns and heuristic algorithms, can deliver a production-ready, resilient IT service management solution."*
- **Closing Remarks:**
  - **Kushagr Kakkar (`2025A7PS0071H`):** *"Thank you for your time and guidance."*
  - **Ishaan Singhal (`2025A7PS0171H`):** *"All code, test suites, and documentation are available on our GitHub repository."*
  - **Mitansh Gupta (`2025A7PS0159H`):** *"We look forward to your feedback and questions!"*
