# Implementation Plan - Project Stabilization and Best Practices

This plan focuses on fixing memory leaks, resolving test compilation errors, and aligning the project with modern Android architectural standards.

## User Review Required

> [!IMPORTANT]
> **Memory Safety:** I will be refactoring all Fragments to use a nullable `_binding` variable and a non-null `binding` getter, ensuring `_binding` is set to `null` in `onDestroyView`. This is critical for preventing memory leaks.

> [!TIP]
> **Dependency Injection:** To reduce boilerplate, I strongly recommend implementing **Hilt** in a future step. For now, I will clean up the manual factory usage to be more consistent.

## Proposed Changes

### 1. Fix Memory Leaks (ViewBinding)

#### [MODIFY] All Fragments
- Implement the standard pattern:
  ```kotlin
  private var _binding: FragmentBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(...) {
      _binding = FragmentBinding.inflate(inflater, container, false)
      return binding.root
  }

  override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
  }
  ```
- Files: `CreateAlarmFragment.kt`, `SolarAlarmListFragment.kt`, `LocationCreateFragment.kt`, `LocationListFragment.kt`, `SolarTimeFragment.kt`, `UpdateAlarmFragment.kt`.

### 2. Fix Test Compilation

#### [MODIFY] [HttpTestCase.kt](file:///C:/GitHub/Solar_Alarm/app/src/test/java/com/example/solar_alarm/HttpTestCase.kt)
- Update method call `GetSolarData` to `getSolarData` to match the refactored `HttpRequests` class.

### 3. Architectural Cleanup

#### [MODIFY] [CreateAlarmFragment.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/createAlarm/CreateAlarmFragment.kt)
- Remove `solarAlarmRepository` property.
- Move the `insert` logic into `SolarAlarmViewModel`.
- Fragment should only interact with `solarAlarmViewModel`.

#### [MODIFY] [SolarAlarmViewHolder.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/alarmList/SolarAlarmViewHolder.kt)
- Use ViewBinding inside the ViewHolder instead of `findViewById`.
- Remove redundant/incorrect `locationName` field.

### 4. Code Quality & Standards

#### [REFACTOR] Star Imports
- Replace `import com.example.solar_alarm.data.viewmodels.*` and other star imports with explicit imports in all modified files.

#### [MODIFY] [AlarmService.kt](file:///C:/GitHub/Solar_Alarm/app/src/main/java/com/example/solar_alarm/service/AlarmService.kt)
- Move hardcoded notification text to `strings.xml`.

## Verification Plan

### Automated Tests
- Run `./gradlew test` to ensure `HttpTestCase` passes.
- Run `./gradlew assembleDebug` to verify compilation.

### Manual Verification
- Deploy to emulator.
- Use the app to create an alarm and navigate between screens.
- Use Android Studio's Memory Profiler to verify that Fragments are properly garbage collected after being removed from the backstack.
