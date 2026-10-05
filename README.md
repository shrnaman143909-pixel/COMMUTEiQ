# CommuteIQ – AI-Powered Personal Commute Intelligence Platform

> **SIH 2026 Student Innovation Project**  
> *Problem Statement:* Student Innovation – Creating intelligent devices to improve commutation sector  
> *Core Innovation:* **From Route Finding to Commute Intelligence**

---

## 1. Project Overview

**CommuteIQ** is not a generic Google Maps clone. While traditional mapping tools only locate static paths, CommuteIQ acts as an intelligent transit advisor. It learns each commuter's travel habits, crowd tolerance, walking boundaries, and budget constraints to synthesize a unique **Commute Fingerprint** and evaluate multi-modal transit options using a transparent **Dynamic Commute Score (0–100)**.

---

## 2. Key Features

- **Personalized Commute Fingerprint**:
  - Calibrates travel mode preferences, maximum walking distance (200m–2500m), daily budget (₹10–₹150), departure windows, crowd tolerance (Low / Moderate / High), and priority weighting sliders (Time, Cost, Foot fatigue).
- **Multi-Modal Route Intelligence**:
  - Seeded realistic transit network featuring Metro Rail, Smart AC EV Buses, Campus Shuttles, Shared EV Cabpools, and SmartCycle active transit.
  - Transparent 6-pillar score breakdown: Time efficiency (25%), Cost savings (20%), Crowd comfort (20%), Walking convenience (15%), Delay risk mitigation (10%), and Preference alignment (10%).
- **AI Recommendation Engine**:
  - Direct integration with **Gemini 3.5 Flash** for personalized reasoning, transit congestion warnings, and optimal departure windows.
  - Deterministic fallback scoring formulation ensuring 100% offline and low-latency availability.
- **3-Way Route Comparison**:
  - Side-by-side decision matrix comparing **Route A (Fastest)**, **Route B (Cheapest)**, and **Route C (Best Match for You)**.
- **Live Journey Mode with Visual Map Canvas**:
  - Interactive transit canvas rendering stations, multi-modal lines, transfer hubs, and live pulsing commuter GPS beacon.
  - Real-time signal telemetry status, progress bar, ETA countdown, next stop announcements, and step simulation controls.
- **Journey Feedback Loop**:
  - 1–5 star rating system assessing crowd levels, time accuracy, and ride comfort to continuously train and update the commuter's Commute Fingerprint.
- **Operator & Admin Dashboard**:
  - Commuter fleet analytics, user counts, route performance logs, delay alert toggling, new transit route creation, and an immutable RLS-enforced **Admin Audit Log**.

---

## 3. Tech Stack & Architecture

- **Client Runtime**: Android Jetpack Compose (Kotlin DSL), Material Design 3 (M3).
- **Build System**: Gradle 9 with Version Catalog (`gradle/libs.versions.toml`).
- **AI Engine**: Google Gemini API (`gemini-3.5-flash`) via Retrofit / OkHttp REST with structured JSON responses.
- **Backend & Database**: Supabase PostgreSQL 15+ with Row Level Security (RLS) & Supabase GoTrue Auth.
- **Visuals & Maps**: Custom Compose `Canvas` geometry rendering nodes, transit corridors, and pulse animations.

---

## 4. Environment Variables (`.env`)

Configure these variables in your `.env` file or via the **Secrets panel** in Google AI Studio:

```properties
# Google Gemini AI API Key (Required for AI Reasoning)
GEMINI_API_KEY=your-gemini-api-key

# Supabase Project URL (e.g., https://your-project-id.supabase.co)
SUPABASE_URL=https://your-project.supabase.co

# Supabase Public Safe Anon Key
SUPABASE_ANON_KEY=your-supabase-anon-key
```

*Note: Privileged `service_role` keys are strictly forbidden on frontend and mobile client devices.*

---

## 5. Supabase Setup & Database Schema

The database migration and Row Level Security policies are located in `/supabase/schema.sql`.

### Core Tables:
1. `profiles`: Commuter profiles linked to `auth.users(id)`.
2. `commute_preferences`: Dynamic commuter priorities, budget, and walking limit.
3. `transport_options`: Available transit modes and fare scales.
4. `routes`: Evaluated transit routes with commute scores, crowd levels, and delay risks.
5. `route_segments`: Multi-modal turn-by-turn legs (walking, boarding, transfers).
6. `journeys`: Commuter journey records with start/end timestamps.
7. `journey_feedback`: User ratings, crowd feedback, and punctuality observations.
8. `saved_routes`: Commuter bookmarks and favorites.
9. `commute_insights`: Telemetry analytics, average travel metrics, and CO₂ savings.
10. `admin_users`: Authorization table for transit administrators.
11. `admin_audit_logs`: Audit trail for route creation, updates, and deletions.

### Running Schema & RLS Tests:
1. Open your Supabase Dashboard -> **SQL Editor**.
2. Run `/supabase/schema.sql` to generate all tables, triggers, and RLS policies.
3. Run `/supabase/tests.sql` to verify RLS rule enforcement (non-admin restrictions, private profile isolation).

---

## 6. Demo Accounts & Credentials

For prototype and evaluation testing, pre-configured accounts are provided with 1-click access on the login screen:

| Role | Email | Password | Pre-configured Profile |
|------|-------|----------|------------------------|
| **Student Commuter** | `shrnaman143909@gmail.com` | `pass123` | Naman Sharma, Metro preferred, ₹45 budget, 800m walk limit |
| **Student Commuter B** | `ananya.student@sih.org` | `pass123` | Ananya Roy, EV Bus preferred, ₹30 budget, 1200m walk limit |
| **Transit Admin** | `admin@commuteiq.gov.in` | `pass123` | Transit Authority Operator, full route CRUD & audit privileges |

---

## 7. Local Development & Testing

### Compilation:
Run the compilation tool or Gradle directly:
```bash
gradle :app:assembleDebug
```

### Running Unit & Security Tests:
Run the comprehensive JVM test suite:
```bash
gradle :app:testDebugUnitTest
```
The test suite validates:
- Dynamic scoring algorithm across extreme priority weights (budget-first vs speed-first).
- Gemini API graceful fallback to deterministic evaluation.
- Supabase authentication workflow (commuter and admin sign in, sign out).
- Journey start, waypoint stepping, completion, and feedback logging.
- Admin action audit log generation.

---

## 8. Production Deployment Checklist

- [x] **Application ID**: Set to `com.aistudio.commuteiq.transit`.
- [x] **App Launcher Icon**: Custom adaptive icon configured (`ic_launcher_background.xml` & `ic_launcher_foreground.xml`).
- [x] **Secrets**: Injected through `BuildConfig` via `.env` (no hardcoded keys).
- [x] **Edge-to-Edge**: Handled via `enableEdgeToEdge()` and WindowInsets scaffolding.
- [x] **Responsive Width**: Centered with max-width bounding for tablets, foldables, and desktop layouts.
- [x] **Row Level Security**: 100% of database tables protected by RLS.
- [x] **Offline Resilience**: Fully deterministic fallback algorithm operational when API or internet is unavailable.

---

## 9. GitHub Export & CI/CD Workflow

### How to Push to GitHub from AI Studio:
1. Look at the top navigation bar or settings menu (three dots or project title menu in AI Studio).
2. Click **"Push to GitHub"** (or **"Export to GitHub"**).
3. Connect your GitHub account if prompted, and select or name your repository (e.g., `CommuteIQ`).
4. AI Studio will automatically push the entire repository, including Gradle configurations, adaptive icons, and source code.

### Repository Files Prepared for GitHub:
- **`gradlew` & `gradlew.bat`**: Full Gradle wrapper included so collaborators can build instantly using `./gradlew assembleDebug`.
- **`.github/workflows/android.yml`**: Pre-configured GitHub Actions CI workflow that tests and compiles debug APKs automatically on every push or pull request.
- **`.gitignore`**: Configured to protect secrets (`.env`, `debug.keystore`, `local.properties`) while retaining project source and schemas.

