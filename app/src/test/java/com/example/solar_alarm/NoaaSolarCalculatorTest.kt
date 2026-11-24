package com.example.solar_alarm

import android.os.Build
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import com.example.solar_alarm.data.solarCalculator.NoaaSolarCalculator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.ZoneId

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class NoaaSolarCalculatorTest {

    private lateinit var calculator: NoaaSolarCalculator

    @Before
    fun setUp() {
        calculator = NoaaSolarCalculator()
    }

    @Test
    fun calculateSolarTime_forNewYork_producesAccurateSolarTimes() {
        // New York City coordinates: 40.7128 N, 74.0060 W
        val date = LocalDate.of(2026, 10, 26)
        val locationId = 1
        val latitude = 40.7128
        val longitude = -74.0060

        val solarTime = calculator.calculateSolarTime(locationId, latitude, longitude, date)

        assertNotNull(solarTime)
        assertEquals(date, solarTime.SolarDate)
        assertEquals(locationId, solarTime.LocationId)

        assertNotNull(solarTime.SunriseUtc)
        assertNotNull(solarTime.SunsetUtc)
        assertNotNull(solarTime.SolarNoonUtc)
        assertNotNull(solarTime.CivilTwilightBeginUtc)
        assertNotNull(solarTime.CivilTwilightEndUtc)

        // New York on Oct 26: Sunrise is ~11:20 UTC (07:20 EDT), Sunset is ~22:00 UTC (18:00 EDT)
        val sunriseLocal = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).withZoneSameInstant(ZoneId.of("America/New_York"))
        val sunsetLocal = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.Sunset).withZoneSameInstant(ZoneId.of("America/New_York"))
        val solarNoonLocal = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).withZoneSameInstant(ZoneId.of("America/New_York"))

        // Sunrise should be at hour 7
        assertEquals(7, sunriseLocal.hour)
        assertTrue("Sunrise minute ${sunriseLocal.minute} should be between 15 and 30", sunriseLocal.minute in 15..30)

        // Sunset should be at hour 17 or 18
        assertTrue("Sunset hour should be 17 or 18", sunsetLocal.hour in 17..18)

        // Solar noon should be around 12:40 PM EDT
        assertEquals(12, solarNoonLocal.hour)
        assertTrue("Solar noon minute ${solarNoonLocal.minute} should be between 35 and 45", solarNoonLocal.minute in 35..45)

        // Day length should be approx 10.5 hours (37000 to 39000 seconds)
        assertTrue("Day length should be around 10.5 hours", solarTime.DayLength in 37000..39500)

        // Twilight progression: Astro < Nautical < Civil < Sunrise
        val astroBegin = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightBegin)
        val nauticalBegin = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightBegin)
        val civilBegin = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightBegin)
        val sunrise = solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise)

        assertTrue(astroBegin.isBefore(nauticalBegin))
        assertTrue(nauticalBegin.isBefore(civilBegin))
        assertTrue(civilBegin.isBefore(sunrise))
    }

    @Test
    fun calculateSolarTime_forLondonEquinox_producesApproximately12HoursDay() {
        // London on spring equinox: ~12 hours day length
        val date = LocalDate.of(2026, 3, 20)
        val solarTime = calculator.calculateSolarTime(1, 51.5074, -0.1278, date)

        assertNotNull(solarTime.SunriseUtc)
        assertNotNull(solarTime.SunsetUtc)

        // 12 hours = 43200 seconds; should be within 15 minutes of 12 hours (42300 - 44100)
        assertTrue("Day length at equinox should be ~12 hours, was ${solarTime.DayLength}", solarTime.DayLength in 42300..44100)
    }

    @Test
    fun calculateSolarTime_forSouthernHemisphere_calculatesCorrectly() {
        // Sydney, Australia: 33.8688 S, 151.2093 E
        // In June (winter in southern hemisphere), day length is short (~10 hours)
        val date = LocalDate.of(2026, 6, 21)
        val solarTime = calculator.calculateSolarTime(2, -33.8688, 151.2093, date)

        assertNotNull(solarTime.SunriseUtc)
        assertNotNull(solarTime.SunsetUtc)
        assertTrue("Sydney winter day length should be between 34000 and 37000 seconds", solarTime.DayLength in 34000..37000)
    }
}
