<p align="center">
  <img src="app/src/main/res/drawable/campusliftlogo.png" alt="CampusLift Logo" width="150"/>
</p>

<h1 align="center">CampusLift</h1>

<p align="center"><em>Safe. Affordable. Student Rides.</em></p>

---

A ride-sharing Android application built exclusively for university students. CampusLift connects verified students who need rides with fellow students who are already driving the same route — making transport cheaper, safer, and more sustainable for campus communities.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Screenshots](#screenshots)
- [Testing](#testing)
- [Version Control & CI/CD](#version-control--cicd)
- [Team & Contributions](#team--contributions)
- [Acknowledgements](#acknowledgements)
- [AI Usage Declaration](#ai-usage-declaration)
- [References](#references)
- [Demonstration Video](#demonstration-video)
- [License](#license)

---

## Overview

CampusLift is a native Android application that enables students to share rides within their campus community. The app requires university email verification to ensure safety and trust, offers a fixed cost-sharing model (no surge pricing), and provides real-time communication between drivers and passengers.

**Target Users:** University students who commute to campus — both those seeking rides and those willing to offer them.

---

## Problem Statement

Access to safe and reliable transportation is a challenge faced by many university students. Current options — expensive taxi services, inefficient public transport, and informal WhatsApp groups — are ineffective and sometimes unsafe.

CampusLift addresses this by providing an authenticated platform where verified university students can provide and request rides within their campus community, making travel cheaper, easier, and safer while also reducing car usage.

---

## Features

### Core Features (POE Part 1 & 2)

- **University Email Verification** — Only verified students can offer or book rides
- **Single Sign-On (SSO)** — Sign in with Google or Microsoft account via Firebase Authentication
- **Ride Matching** — Search for rides by pickup, destination, date, and time
- **Ride Creation** — Drivers can post rides with seat availability
- **Seat Booking** — Passengers can request seats with driver approval
- **Driver Ratings** — Post-trip rating system to build community trust
- **Trip Chat** — Private per-trip messaging between driver and passengers
- **Real-time Notifications** — Firebase Cloud Messaging for booking updates
- **Settings & Preferences** — Per-user settings with persistent storage
- **Offline Mode** — Local storage with automatic sync when reconnected

### Advanced Features (Final POE)

- **Biometric Authentication** — Fingerprint / face unlock
- **Multi-Language Support** — English and isiZulu
- **Live Location Sharing** — Share trip details with emergency contacts

---

## Technology Stack

### Android Application
| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Architecture | MVVM |
| Navigation | Navigation Compose |
| Networking | Retrofit + OkHttp |
| Local Storage | DataStore Preferences |
| Authentication | Firebase Auth + Credential Manager |
| Push Notifications | Firebase Cloud Messaging |
| Image Loading | Coil |
| Maps | Google Maps SDK |

### Backend
| Component | Technology |
|-----------|-----------|
| API Framework | ASP.NET Core Web API |
| Database | Supabase (PostgreSQL) |
| Hosting | Cloud-hosted |
| Authentication | Firebase + custom `X-Firebase-Uid` header |

---

## Architecture

CampusLift follows a client-server architecture with clear separation of concerns:

```
Android Application (Kotlin + Jetpack Compose)
        |
        | HTTPS / JSON
        | X-Firebase-Uid header
        v
CampusLift REST API (ASP.NET Core)
        |
        v
Supabase Database (PostgreSQL)

External Services:
- Firebase Auth (SSO)
- Firebase Cloud Messaging (Push)
- Google Maps SDK (Location)
```

### Architecture Diagram

![Architecture Diagram](docs/architecture-diagram.png)

*The UML system architecture diagram from the Part 1 design document illustrates how the mobile application communicates with the custom REST API, database, and external SDKs.*

---

## Project Structure

```
app/src/main/java/com/example/campuslift/
├── Auth/                      # Firebase authentication logic
│   ├── AuthRepository.kt
│   ├── AuthState.kt
│   └── GoogleSignInHelper.kt
├── Components/                # Reusable UI components
│   ├── ActiveTripBanner.kt
│   ├── BookingCard.kt
│   ├── CampusLiftButton.kt
│   ├── CampusLiftRideCard.kt
│   ├── CampusLiftTextField.kt
│   ├── CampusLiftTopBar.kt
│   ├── EmptyState.kt
│   ├── ErrorMessage.kt
│   ├── LanguageDropdown.kt
│   ├── LoadingIndicator.kt
│   ├── PassiveBanner.kt
│   ├── SettingsClickRow.kt
│   ├── SettingsToggleRow.kt
│   └── ValidationUtils.kt
├── Data/                      # Data layer
│   ├── dto/                   # Data Transfer Objects
│   ├── remote/                # Network layer (Retrofit)
│   ├── repository/            # Repositories
│   └── SettingsRepository.kt  # DataStore-based settings storage
├── Navigation/                # Navigation graph
│   ├── AppNav.kt
│   ├── BottomNavBar.kt
│   └── BottomNavItem.kt
├── Screens/                   # UI Screens
│   ├── AddVehicleScreen.kt
│   ├── AlertsScreen.kt
│   ├── BookingDetailsScreen.kt
│   ├── CreateRideScreen.kt
│   ├── HomeScreen.kt
│   ├── LiftDetailsScreen.kt
│   ├── LiftsScreen.kt
│   ├── LoginScreen.kt
│   ├── MyBookingsScreen.kt
│   ├── RegisterScreen.kt
│   ├── RideDetailsScreen.kt
│   └── SettingsScreen.kt
├── ViewModels/                # MVVM ViewModels
│   ├── AuthViewModel.kt
│   ├── BookingViewModel.kt
│   ├── NotificationViewModel.kt
│   ├── SettingsViewModel.kt
│   ├── TripViewModel.kt
│   ├── UserViewModel.kt
│   └── VehicleViewModel.kt
├── ui/theme/                  # Theme & styling
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── MainActivity.kt            # App entry point
```

---

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Android SDK API 25+
- JDK 11+
- Firebase project with Authentication enabled
- Access to the CampusLift backend API

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ST10441359/CampusLiftApp.git
   cd CampusLiftApp
   ```

2. **Open in Android Studio:**
   File → Open → select the `CampusLiftApp` folder

3. **Configure Firebase:**
   Place `google-services.json` in the `app/` folder and ensure your Firebase project has Google Sign-In enabled.

4. **Configure API URL:**
   Open `Data/remote/ApiConfig.kt` and set `BASE_URL` to your local or hosted API address.

5. **Build and run:**
   ```bash
   ./gradlew assembleDebug
   ```
   Or press Run in Android Studio.

---

## API Endpoints

### Users
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/users/sync` | Sync Firebase user with backend |
| GET | `/api/users/me` | Get current user's profile |

### Vehicles
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/vehicles` | List user's vehicles |
| POST | `/api/vehicles` | Register a new vehicle |
| PATCH | `/api/vehicles/{id}` | Update a vehicle |
| DELETE | `/api/vehicles/{id}` | Remove a vehicle |

### Trips
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/trips` | Search for rides |
| GET | `/api/trips/mine` | Get rides created by current user |
| POST | `/api/trips` | Create a ride |
| PATCH | `/api/trips/{id}` | Update a ride |
| POST | `/api/trips/{id}/cancel` | Cancel a ride |
| POST | `/api/trips/{id}/complete` | Complete a ride |

### Bookings
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/bookings` | Book a seat |
| GET | `/api/bookings/mine` | List user's bookings |
| POST | `/api/bookings/{id}/approve` | Approve a booking |
| POST | `/api/bookings/{id}/cancel` | Cancel a booking |
| POST | `/api/bookings/{id}/pickup-confirm` | Confirm pickup |

### Notifications
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/notifications` | List notifications |
| GET | `/api/notifications/unread-count` | Get unread count |
| PATCH | `/api/notifications/{id}` | Mark as read |
| POST | `/api/notifications/mark-all-read` | Mark all read |

---

## Data Models

### UserDto
```kotlin
data class UserDto(
    val id: String,
    val firebaseUid: String?,
    val email: String?,
    val name: String?,
    val surname: String?,
    val studentNumber: String?,
    val university: String?,
    val isVerified: Boolean?,
    val profilePicture: String?,
    val emergencyContact: String?,
    val language: String?,
    val darkMode: Boolean?,
    val biometricEnabled: Boolean?,
    val notificationEnabled: Boolean?
)
```

### TripWithAvailabilityDto
```kotlin
data class TripWithAvailabilityDto(
    val id: String,
    val driverId: String,
    val vehicleId: String?,
    val fromLocation: String,
    val toLocation: String,
    val eventTime: String?,
    val pricePerSeat: Double,
    val totalSeats: Int,
    val seatsTaken: Int,
    val seatsRemaining: Int,
    val description: String?,
    val isActive: Boolean,
    val isComplete: Boolean,
    val driver: PublicUserSummaryDto?,
    val vehicle: PublicVehicleSummaryDto?
)
```

Full models are located in `Data/dto/`.

---

## Screenshots

*Screenshots will be added once the UI is finalised. Screens to capture:*

- Login / SSO screen
- Home / Search screen
- Ride Details screen
- Bookings screen
- Trip Chat screen
- Profile / Settings screen (light mode)
- Profile / Settings screen (dark mode)
- Alerts / Notifications screen

*Screenshots are saved to `docs/screenshots/` and embedded using:*
```markdown
![Settings Light](docs/screenshots/settings-light.png)
![Settings Dark](docs/screenshots/settings-dark.png)
```

---

## Testing

The project includes unit tests for reusable logic components, executed automatically via GitHub Actions on every push.

### Unit Tests

**Validation logic** (`ValidationUtilsTest.kt`):
- Phone number validation (valid and invalid formats)
- Email validation (valid and invalid formats)
- Required field validation
- Max length validation

*Additional tests to be added for other components as development progresses.*

### Running Tests Locally

```bash
./gradlew test
```

### Automated Testing

Tests run automatically on every push via GitHub Actions. See the [Actions tab](https://github.com/ST10441359/CampusLiftApp/actions) for live results.

---

## Version Control & CI/CD

### Version Control

This project uses Git with a feature-branch workflow:
- `main` — stable, deployable branch
- Feature branches merged via pull requests
- Regular commits with descriptive messages

### Commit Convention

```
feat: add new feature
fix: resolve bug
docs: update documentation
test: add tests
ci: update CI configuration
```

### GitHub Actions

*Workflow configuration and CI/CD setup will be finalised by the team. This section will document:*
- Workflow file name and location
- Trigger conditions (push to main, pull requests)
- Jobs performed (build, test, lint)
- Link to live workflow results

---

## Team & Contributions

**Group: Commit Push & Pray**

| Member | Student Number | Primary Responsibility |
|--------|---------------|----------------------|
| Suvan Samlall | ST10441359 | Group Leader, SSO, API Integration, GitHub Actions |
| Joshua Gerald Chetty | ST10296234 | REST API, Database, Backend Business Logic |
| Ziyaad Simjee | ST10406906 | Core Ride Features & Feature UI |
| Keshvir Parthab | ST10451537 | Settings, Reusable Components, Validation, Tests |

### Keshvir Parthab — Settings, Reusable Components, Validation

**Settings Screen (`SettingsScreen.kt`)**
- Account overview section displaying user name and email
- Language dropdown (English / isiZulu)
- Dark Mode toggle wired to the app's theme
- Biometric Login toggle
- Notifications toggle
- Editable default pickup, emergency contact and vehicle fields
- Save button with Toast confirmation
- Sign Out integration

**Reusable UI Components (14 total, in `Components/`)**
- `CampusLiftButton` — primary orange action button
- `CampusLiftTextField` — input with inline error support
- `CampusLiftRideCard` — ride listing card
- `CampusLiftTopBar` — consistent header with back arrow
- `LoadingIndicator` — centered spinner
- `ErrorMessage` — error box
- `EmptyState` — empty list placeholder
- `SettingsClickRow` — tappable settings row
- `SettingsToggleRow` — toggle switch row
- `LanguageDropdown` — dropdown for language selection
- `BookingCard` — booking listing card
- `ActiveTripBanner` — banner shown during active trips
- `PassiveBanner` — auto-dismissing notification banner
- `ValidationUtils` — reusable validation helpers

**Data Persistence (`SettingsRepository.kt`, `SettingsViewModel.kt`)**
- Per-user settings storage using Android DataStore
- All preferences keyed by Firebase UID
- Automatic sync when user changes accounts
- Persists across app restarts

**Theme Switching (`MainActivity.kt`)**
- Dynamic dark/light theme driven by user preference
- Combined with Suvan's `SyncObserver` for post-login sync

**Validation (`ValidationUtils.kt`)**
- Phone number validation
- Email validation
- Required field validation
- Max length validation

**Logging**
- Android Log statements throughout the Settings flow
- Enables debugging of user actions in Logcat

---

## Acknowledgements

CampusLift was built using the following open-source libraries and public documentation:

- **Jetpack Compose** — Android UI toolkit (Apache 2.0)
- **Retrofit** — Networking library by Square (Apache 2.0)
- **OkHttp** — HTTP client by Square (Apache 2.0)
- **Firebase** — Authentication and Cloud Messaging by Google
- **DataStore** — Preferences storage by Android
- **Material Design 3** — Design system by Google
- **ASP.NET Core** — Backend framework by Microsoft
- **Supabase** — PostgreSQL database hosting
- **Gson** — JSON serialization by Google

Full references listed in the References section.

---

## AI Usage Declaration

Generative AI tools (ChatGPT, Google Gemini) were used during development of CampusLift for:

- Debugging assistance and error resolution
- Code structure suggestions and boilerplate generation
- Documentation drafting and refinement

All AI-generated content was reviewed, adapted, and integrated by team members. Any code snippets or patterns adapted from external sources are referenced in the References section.

---

## References

The following resources were consulted and used during the development of CampusLift. All sources are referenced using the IEEE style.

### Android & Jetpack Compose

[1] Android Developers, "Jetpack Compose," [Online]. Available: https://developer.android.com/jetpack/compose. [Accessed: Sep. 2026].

[2] Android Developers, "Guide to app architecture - MVVM," [Online]. Available: https://developer.android.com/topic/architecture. [Accessed: Sep. 2026].

[3] Android Developers, "Save data in a local database using Room," [Online]. Available: https://developer.android.com/training/data-storage/room. [Accessed: Sep. 2026].

[4] Android Developers, "DataStore," [Online]. Available: https://developer.android.com/topic/libraries/architecture/datastore. [Accessed: Sep. 2026].

[5] Android Developers, "Biometric authentication," [Online]. Available: https://developer.android.com/training/sign-in/biometric-auth. [Accessed: Sep. 2026].

[6] Android Developers, "Navigation Compose," [Online]. Available: https://developer.android.com/develop/ui/compose/navigation. [Accessed: Sep. 2026].

[7] Android Developers, "Material 3 in Compose," [Online]. Available: https://developer.android.com/develop/ui/compose/designsystems/material3. [Accessed: Sep. 2026].

[8] Android Developers, "Navigation Bar in Compose," [Online]. Available: https://developer.android.com/develop/ui/compose/components/navigation-bar. [Accessed: Sep. 2026].

### Firebase

[9] Firebase, "Firebase Authentication for Android," [Online]. Available: https://firebase.google.com/docs/auth/android/start. [Accessed: Sep. 2026].

[10] Firebase, "Firebase Cloud Messaging," [Online]. Available: https://firebase.google.com/docs/cloud-messaging. [Accessed: Sep. 2026].

[11] Google, "Sign in with Google for Android using Credential Manager," [Online]. Available: https://developer.android.com/identity/sign-in/credential-manager-siwg. [Accessed: Sep. 2026].

### Networking & Data

[12] Square, "Retrofit - A type-safe HTTP client for Android," [Online]. Available: https://square.github.io/retrofit/. [Accessed: Sep. 2026].

[13] Square, "OkHttp," [Online]. Available: https://square.github.io/okhttp/. [Accessed: Sep. 2026].

[14] Square, "OkHttp Interceptors," [Online]. Available: https://square.github.io/okhttp/features/interceptors/. [Accessed: Sep. 2026].

[15] Google, "Gson," [Online]. Available: https://github.com/google/gson. [Accessed: Sep. 2026].

### Backend

[16] Microsoft, "ASP.NET Core Web API Documentation," [Online]. Available: https://learn.microsoft.com/en-us/aspnet/core/web-api/. [Accessed: Sep. 2026].

[17] Supabase, "Supabase Documentation," [Online]. Available: https://supabase.com/docs. [Accessed: Sep. 2026].

### Maps & Location

[18] Google, "Maps SDK for Android," [Online]. Available: https://developers.google.com/maps/documentation/android-sdk. [Accessed: Sep. 2026].

### Design

[19] Material Design 3, "Material Design Guidelines," [Online]. Available: https://m3.material.io/. [Accessed: Sep. 2026].

### Tools & DevOps

[20] GitHub, "GitHub Actions Documentation," [Online]. Available: https://docs.github.com/en/actions. [Accessed: Sep. 2026].

[21] JetBrains, "Kotlin Documentation," [Online]. Available: https://kotlinlang.org/docs/. [Accessed: Sep. 2026].

---

## Demonstration Video

Video link:

The demonstration video will cover:
- SSO registration and login
- Settings menu with working toggles
- Offline mode with sync
- Real-time notifications
- Core user-defined features
- Live demonstration on a physical Android device

---

## License

This project is developed as part of the IIE Bachelor of Computer and Information Sciences module PROG7314 - Programming 3D. It is for academic purposes only.

---
