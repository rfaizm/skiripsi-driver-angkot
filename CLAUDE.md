# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android driver app for an angkot (Bandung public minibus) ride-sharing thesis project. Single `app` module, Kotlin, XML layouts + ViewBinding, Jetpack Navigation. Package `com.example.driverangkot`. Code comments and UI strings are in Indonesian.

## Commands

Run from repo root (on Windows use `gradlew.bat` or `./gradlew` in Git Bash):

```
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # install on connected device/emulator
./gradlew testDebugUnitTest --tests "com.example.driverangkot.SomeTest"   # single JVM test class
./gradlew lint
```

Gradle 8.10 does not run on JDK 25 (Android Studio's bundled JBR); set `JAVA_HOME` to a JDK 17/21 (e.g. `~/.jdks/jbr-21.0.11`). There is no test suite.

## Configuration

- `MAPS_API_KEY` must be set in `local.properties`; injected into the manifest via the secrets-gradle-plugin.
- Backend base URL is a `buildConfigField` (`BASE_URL`) in `app/build.gradle.kts` — a Laravel API over plain HTTP (`usesCleartextTraffic="true"`).
- Pusher app key and cluster (`ap1`) are hardcoded in `service/PusherService.kt`.
- Dependency versions live in `gradle/libs.versions.toml`, though some deps are declared inline in `app/build.gradle.kts`.

## Architecture

Three layers per feature, wired manually (no Hilt/Koin):

- `data/api` — Retrofit `ApiService` + DTOs. `ApiConfig.getApiService(userPreference)` adds `Authorization: Bearer <token>` via an OkHttp interceptor whenever a token is stored, so endpoints take no token parameter.
- `data/repository` — concrete classes (`UserRepository`, `AngkotRepository`, `OrderRepository`, `LocationRepository`). They call `ApiService`/DataStore directly and throw on failure; business rules live here (login validation + driver-role check + session save, register multipart building and 422 parsing, order cancel error parsing).
- `presentation/<feature>` — Activities/Fragments + ViewModels exposing LiveData of `di/ResultState`. ViewModels wrap repository calls as `runCatching { repo.x() }.toResultState("fallback message")`. `SingleLiveEvent` for one-shot events.
- `di/Injection.kt` — builds repositories and holds the single `viewModelFactory` (`viewModelFactory { initializer { ... } }`). Screens use `by viewModels { Injection.viewModelFactory }`; services call `Injection.xxxRepository(context)`. Adding a ViewModel = one `initializer` line here.
- `domain/entity` — plain data classes (`User`, `OrderData`, `Passenger`, `Trayek`). Use `com.google.android.gms.maps.model.LatLng` for coordinates.

### Session & local state

`data/preference/UserPreference.kt` wraps a Preferences DataStore named `session` (login data, token, driverId, trayekId, online flag). Getters use `runBlocking` so they can be called synchronously. `OrderRepository` stores pending incoming orders as a JSON list under the `orders` key in the **same** DataStore; logout clears everything.

### Online flow & background services

Toggling online/offline in `HomeFragment` calls the API, saves the online flag, and starts/stops two foreground services (both `START_STICKY`):

- `service/PusherService` — subscribes to Pusher channel `driver.<driverId>`; on `App\Events\OrderCreated` saves the order via `OrderRepository` and posts a notification; on `App\Events\OrderCancelled` removes it. Stops itself if the driver is not online.
- `service/LocationUpdateService` — pushes GPS updates to `angkot/update-location` via `FusedLocationProviderClient` when moved ≥5 m (`Utils.calculateDistance`, meters). Its `REALTIME_TRACKING` logs (also in `HomeFragment`) are thesis latency measurements — keep them.

`MainActivity` redirects to `LoginActivity` if not logged in, and restarts/stops `LocationUpdateService` based on the stored online flag. The home screen observes the orders Flow from DataStore, so the service and UI communicate through DataStore rather than binding. Order status updates (`dijemput`, `selesai`) go through `ListPassengerActivity`; `selesai` also removes the order locally.

Main navigation: `MainActivity` bottom nav (`res/navigation/mobile_navigation.xml`) with Home, Income, Profile. Login, Register, ListPassenger, and TransferToBank are separate Activities with their own child fragments.
