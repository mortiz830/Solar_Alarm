package com.example.solar_alarm.createAlarm

import com.example.solar_alarm.BuildConfig

/**
 * Debug configuration settings for developer testing.
 * All properties are strictly gated by BuildConfig.DEBUG so they are disabled and unreachable in release builds.
 */
object DebugSettings {
    const val EXTRA_IS_DEBUG_ONE_MINUTE = "EXTRA_IS_DEBUG_ONE_MINUTE"

    /**
     * When enabled in debug builds, any alarm created will be scheduled to fire
     * exactly 1 minute (60 seconds) after creation.
     * Always returns false and rejects assignments in release builds.
     */
    var ringOneMinuteAfterCreation: Boolean = false
        get() = BuildConfig.DEBUG && field
        set(value) {
            field = BuildConfig.DEBUG && value
        }

    /**
     * Indicates whether the app is currently running as a debug build.
     */
    val isDebug: Boolean
        get() = BuildConfig.DEBUG
}
