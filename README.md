# Bus Driver Android App — Offline-first Trip Tracking

An Android app for bus drivers to record trips offline with continuous background location tracking and automatic sync when online. Built with Kotlin, MVVM, Room, WorkManager, and Hilt. Designed for easy demo: simple login, route selection, trip capture via a foreground service, and reliable upload with retry.

## Overview

- Offline-first: all data (driver, routes, trips, locations, session) is persisted locally so the app works without connectivity.
- Background tracking: foreground service captures location points while a trip is active.
- Robust sync: WorkManager uploads completed trips when the device is connected; failed uploads are retried with backoff.
- Minimal UI for assignment review: login, route list, recent trips, trip details.

## Tech Stack

- Language: Kotlin
- Architecture: MVVM + Repository (clean-lite), Hilt for DI
- Persistence: Room (Driver, Route, Trip, TripLocation, Session)
- Background: Foreground Service (location), WorkManager (sync)
- Location: FusedLocationProviderClient
- Coroutines + Flow

## Project Structure

- data/
    - local/ (Room entities, DAOs, AppDatabase)
    - remote/ (DTOs, mappers, remote data sources)
    - repository/ (interfaces + Room-backed impls)
    - session/ (SessionStore)
    - mappers/ (entity-domain conversions)
- domain/
    - models/ (Driver, Route, Trip, TripLocation, TripStatus)
    - session/ (SessionManager)
    - usecases/ (Login/Logout, Start/End Trip, Refresh Catalog, Enqueue/Cancel Sync, etc.)
- service/
    - LocationTrackingService (foreground)
- sync/
    - TripSyncWorker, CatalogSyncWorker
- ui/
    - MainActivity (routes, recent trips, login/logout gating)
    - TripDetailsActivity
    - ViewModel (MainViewModel)

## Data Model

- Driver: id, name, lastSyncedAt
- Route: id, name, startPoint, endPoint, lastUpdatedAt
- Trip:
    - id, driverId, routeId
    - startTime, endTime
    - status: ACTIVE | COMPLETED | SYNCED
    - createdAt, updatedAt
    - Persisted stats: locationCount, firstPointAt, lastPointAt
- TripLocation: id, tripId, timestamp, lat, lng, accuracy, speed, bearing
- Session: driverId (single-row table)

Note: Trip stats are stored on the Trip row at completion time so that raw TripLocation rows can be safely deleted after successful sync.

## Offline-first Strategy

- Room is the source of truth. UI observes Room tables with Flow.
- Session is restored from Room (SessionStore) at launch; no network required.
- Routes can be refreshed when online; otherwise the last known catalog is used.
- Trips and location points are captured entirely offline and synced later.

## Permissions

The app requires these permissions before starting tracking:
- Android 13+ (API 33+): POST_NOTIFICATIONS
- Foreground location: ACCESS_FINE_LOCATION (and COARSE)
- Background location: ACCESS_BACKGROUND_LOCATION

The app enforces “all mandatory or nothing” for tracking:
- It requests in order: Notifications (33+), Foreground, then Background.
- The service does not start until all are granted.

Make sure the manifest includes:
- ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
- ACCESS_BACKGROUND_LOCATION
- FOREGROUND_SERVICE and FOREGROUND_SERVICE_LOCATION
- POST_NOTIFICATIONS (tools:targetApi="33")

## Location Tracking

- Foreground service shows a persistent notification “Trip in progress”.
- LocationRequest: balanced power accuracy, ~1s interval (configurable).
- Batches are persisted as TripLocation rows in the background scope.
- If permissions are missing at runtime, the service stops safely.

## Sync Strategy

- TripSyncWorker:
    - Finds COMPLETED trips.
    - Builds upload payloads (TripUploadDto with TripPointDto list).
    - On success: marks SYNCED, then clears TripLocation rows for that trip.
    - Retries on failure; one-time and periodic jobs are supported.
- CatalogSyncWorker:
    - Refreshes driver display name and route catalog on connectivity.

Constraints: NetworkType.CONNECTED. Periodic sync (15 minutes) can be ensured.

## UI Flow

- Login (simple username; seeded default driver exists).
- When logged in:
    - Routes list (tap a route to start a trip; permissions are enforced).
    - Active trip banner + End Trip button.
    - Recent Trips list (auto-updates via Flow and reorders by updatedAt).
    - Trip Details screen: shows route, status, start/end, points count, first/last timestamps.
- When logged out:
    - All lists/status/buttons are hidden; only login controls are shown.

## Fresh Schema

For a clean start (no migrations):
- AppDatabase version = 1, exportSchema = false.
- TripEntity includes persisted stats:
    - locationCount: Int = 0
    - firstPointAt: Long? = null
    - lastPointAt: Long? = null
- Ensure DAOs include:
    - TripDao.markCompletedWithStats (or equivalent) to set status=endTime=stats
    - TripDao.updateStatusOnly for non-completion transitions
    - TripLocationDao.getCount / getFirstTimestamp / getLastTimestamp
    - TripLocationDao.deleteByTripId

Uninstall the app or clear data when changing schema.

## Setup and Run

Requirements:
- Android Studio Giraffe+ (Arctic Fox or newer recommended)
- JDK 17
- minSdk 29 (Android 10), targetSdk 34/35

Steps:
- Clone repository.
- Open in Android Studio and let Gradle sync.
- Run on a device/emulator with Google Play Services.
- On first launch, seed runs automatically:
    - Driver: driver-001 (Alex Driver)
    - Routes: route-101/102/103

Permissions:
- On first trip start, the app will request:
    - Notifications (API 33+)
    - Foreground location
    - Background location
- All are mandatory before tracking begins.

Login:
- Enter any username (the app associates with the seeded driver for demo).
- After login, routes and recent trips are shown.

Start a trip:
- Tap a route, grant permissions if prompted.
- A foreground notification appears.

End a trip:
- Tap “End Trip”.
- Trip moves to COMPLETED; TripSyncWorker uploads when online; status becomes SYNCED.
- Recent Trips scrolls to top on completion and auto-updates on SYNC via Flow.

## Testing Scenarios

- Offline capture:
    - Disable network.
    - Start trip, move emulator/device to generate locations.
    - End trip.
    - Re-enable network; Wait for sync; status changes to SYNCED; points are cleared.
- App relaunch with active trip:
    - Start a trip, put app in background, relaunch—active trip persists; tracking continues as service remains.
- Permissions:
    - Deny any permission: the app blocks tracking until all mandatory permissions are granted.
- Recent list auto-update:
    - Observe recent trips reordering (ORDER BY updatedAt DESC) when status updates to SYNCED.

## Developer Notes

- DI modules:
    - AppModule, DatabaseModule/RepositoryModule, CatalogModule, NetworkModule.
    - Fake remote data sources are used for demo; swap with real ones for production.
- SeedManager seeds driver and routes once at first run.
- Session:
    - SessionManager keeps in-memory session (StateFlow).
    - SessionStore persists driverId in Room (reactive Flow for UI gating).
- Mappers:
    - Keep domain and data layers decoupled.
- UI Adapters:
    - Route and Trip list items use CardView with minor elevation and ripple.

## Assumptions and Limitations

- Single driver session; no multi-account switching UI.
- A simple “fake” backend interface; real networking not included.
- No map visualization (kept textual for assignment scope).
- OEM background policies may throttle services; interval/power settings can be tuned.
- Data is reset on schema changes (no migrations) in this demo setup.

## Future Improvements

- Real backend integration (Retrofit, auth tokens).
- Streaming/chunked uploads for large trips; compression (e.g., gzip).
- Encrypted Room database (SQLCipher/EncryptedRoom).
- Map preview and route matching.
- Fine-grained location policy (adaptive sampling, battery saver).
- Detailed error surfaces for sync and permissions.