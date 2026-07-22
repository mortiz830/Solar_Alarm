# Implementation Plan - Project Re-evaluation and Modernization

This plan aims to address technical debt, anti-patterns, and deviations from modern Android best practices identified in the Solar Alarm project.

## User Review Required

> [!IMPORTANT]
> **Breaking Change:** I will be refactoring the `SolarAlarm` data model to remove database access from its getters. UI components and adapters will now need to fetch related data (Location and SolarTime) explicitly or via joined Room queries.

> [!WARNING]
> **Fragment Lifecycle:** I will remove parameters from Fragment constructors to ensure they can be properly recreated by the system. I will implement shared ViewModels using `activityViewModels()`.

## Proposed Changes

### 1. Data Layer & Threading

#### [MODIFY] [SolarAlarmDao.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Data/Daos/SolarAlarmDao.kt)
- Add a new query method `GetAllWithDetails()` that returns `Flow<List<SolarAlarmWithDetails>>` to fetch joined data.
- Mark all non-Flow query methods as `suspend`.

#### [MODIFY] [LocationDao.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Data/Daos/LocationDao.kt)
- Mark all query methods as `suspend`.

#### [MODIFY] [SolarTimeDao.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Data/Daos/SolarTimeDao.kt)
- Mark all non-Flow query methods as `suspend`.

#### [MODIFY] [SolarAlarm.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Data/Tables/SolarAlarm.kt)
- Remove `runBlocking` from properties and methods.
- Remove dependency on `SolarAlarmApp`.

#### [NEW] [SolarAlarmWithDetails.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Data/Tables/SolarAlarmWithDetails.kt)
- Create a Room relation class to hold `SolarAlarm`, `Location`, and `SolarTime`.

---

### 2. UI Layer & Architecture

#### [MODIFY] [NavActivity.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Activities/NavActivity.kt)
- Update fragment replacement logic to use parameterless constructors.

#### [MODIFY] [CreateAlarmFragment.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/CreateAlarm/CreateAlarmFragment.kt)
- Remove constructor parameters.
- Use `activityViewModels()` for data sharing.
- Replace `runBlocking` with `lifecycleScope.launch`.
- Remove all `AsyncTask` inner classes and dead code.

#### [MODIFY] [SolarAlarmListFragment.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/AlarmList/SolarAlarmListFragment.kt)
- Remove constructor parameters.
- Use `activityViewModels()`.
- Update adapter to use `SolarAlarmWithDetails`.

#### [MODIFY] [LocationCreateFragment.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/Location/LocationCreateFragment.kt)
- Remove constructor parameters.
- Use `activityViewModels()`.
- Remove `AsyncTask` inner classes.

---

### 3. Cleanup & Standardization

#### [MODIFY] [build.gradle](file:///C:/GitHub/Solar_Alarm/app/build.gradle)
- Remove `ButterKnife` dependencies.
- Remove `org.apache.http.legacy`.
- Update Retrofit and Gson versions to current stable versions.

#### [MODIFY] [AndroidManifest.xml](file:///C:/GitHub/Solar_Alarm/app/src/main/AndroidManifest.xml)
- Remove `org.apache.http.legacy` usage.

#### [REFACTOR] Packages and Methods
- Rename PascalCase packages to lowercase.
- Rename PascalCase methods to camelCase across all Repositories and DAOs.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify compilation.
- Run `androidTest` to ensure core functionality (like HTTP requests if applicable) still passes.

### Manual Verification
- Deploy to emulator and verify:
    - Alarm list loads correctly with location names.
    - Creating a location works without UI freeze.
    - Creating an alarm works without UI freeze.
    - App survives screen rotation on all screens.
