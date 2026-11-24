package com.example.solar_alarm.data.solarCalculator

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.data.tables.SolarTime
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Standard astronomical solar calculation engine implementing NOAA's solar position algorithms.
 * Reference: NOAA Earth System Research Laboratory (ESRL) Global Monitoring Division.
 * Calculates exact sunrise, sunset, solar noon, day length, and all twilight phases locally
 * without requiring any internet connection.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class NoaaSolarCalculator @Inject constructor() {

    companion object {
        const val ZENITH_OFFICIAL = 90.83333333333333  // 90 degrees 50' (atmospheric refraction + solar semi-diameter)
        const val ZENITH_CIVIL = 96.0                   // Sun 6 degrees below horizon
        const val ZENITH_NAUTICAL = 102.0               // Sun 12 degrees below horizon
        const val ZENITH_ASTRONOMICAL = 108.0           // Sun 18 degrees below horizon
    }

    /**
     * Calculates the complete [SolarTime] for a given location and date.
     */
    fun calculateSolarTime(
        locationId: Int,
        latitude: Double,
        longitude: Double,
        date: LocalDate
    ): SolarTime {
        val jd = getJulianDay(date)

        // 1. Solar noon and equation of time at noon
        val tNoon = getJulianCentury(jd + 0.5)
        val l0Noon = geomMeanLongSun(tNoon)
        val mNoon = geomMeanAnomalySun(tNoon)
        val eNoon = eccentricityEarthOrbit(tNoon)
        val obliqCorrNoon = obliquityCorrection(tNoon, meanObliquityOfEcliptic(tNoon))
        val eqTimeNoon = equationOfTime(l0Noon, mNoon, eNoon, obliqCorrNoon)
        val solarNoonMinutes = solarNoonUtc(longitude, eqTimeNoon)

        // 2. Refined Sunrise and Sunset (Official)
        val sunriseMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_OFFICIAL, isSunrise = true)
        val sunsetMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_OFFICIAL, isSunrise = false)

        // 3. Civil Twilight (-6 deg)
        val civilBeginMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_CIVIL, isSunrise = true)
        val civilEndMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_CIVIL, isSunrise = false)

        // 4. Nautical Twilight (-12 deg)
        val nauticalBeginMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_NAUTICAL, isSunrise = true)
        val nauticalEndMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_NAUTICAL, isSunrise = false)

        // 5. Astronomical Twilight (-18 deg)
        val astroBeginMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_ASTRONOMICAL, isSunrise = true)
        val astroEndMinutes = calculateEventMinutes(jd, latitude, longitude, solarNoonMinutes, ZENITH_ASTRONOMICAL, isSunrise = false)

        // 6. Day length in seconds
        val dayLengthSeconds = if (sunriseMinutes != null && sunsetMinutes != null) {
            max(0, ((sunsetMinutes - sunriseMinutes) * 60.0).roundToInt())
        } else {
            val decNoon = sunDeclination(obliqCorrNoon, sunApparentLong(tNoon, l0Noon + sunEqOfCenter(tNoon, mNoon)))
            if (latitude * decNoon > 0) 86400 else 0 // Midnight sun or polar night
        }

        return SolarTime(
            SolarDate = date,
            LocationId = locationId,
            DayLength = dayLengthSeconds,
            SunriseUtc = minutesToIsoUtcString(date, sunriseMinutes),
            SunsetUtc = minutesToIsoUtcString(date, sunsetMinutes),
            SolarNoonUtc = minutesToIsoUtcString(date, solarNoonMinutes),
            CivilTwilightBeginUtc = minutesToIsoUtcString(date, civilBeginMinutes),
            CivilTwilightEndUtc = minutesToIsoUtcString(date, civilEndMinutes),
            NauticalTwilightBeginUtc = minutesToIsoUtcString(date, nauticalBeginMinutes),
            NauticalTwilightEndUtc = minutesToIsoUtcString(date, nauticalEndMinutes),
            AstronomicalTwilightBeginUtc = minutesToIsoUtcString(date, astroBeginMinutes),
            AstronomicalTwilightEndUtc = minutesToIsoUtcString(date, astroEndMinutes)
        )
    }

    /**
     * Calculates the time in UTC minutes from midnight for a rise or set event with refinement.
     */
    private fun calculateEventMinutes(
        jd: Double,
        latitude: Double,
        longitude: Double,
        solarNoonMinutes: Double,
        zenith: Double,
        isSunrise: Boolean
    ): Double? {
        // Step 1: Approximate hour angle using noon values
        val tNoon = getJulianCentury(jd + 0.5)
        val l0Noon = geomMeanLongSun(tNoon)
        val mNoon = geomMeanAnomalySun(tNoon)
        val obliqCorrNoon = obliquityCorrection(tNoon, meanObliquityOfEcliptic(tNoon))
        val trueLongNoon = l0Noon + sunEqOfCenter(tNoon, mNoon)
        val appLongNoon = sunApparentLong(tNoon, trueLongNoon)
        val decNoon = sunDeclination(obliqCorrNoon, appLongNoon)

        val approxHourAngle = hourAngle(latitude, decNoon, zenith) ?: return null

        val approxEventMinutes = if (isSunrise) {
            solarNoonMinutes - 4.0 * approxHourAngle
        } else {
            solarNoonMinutes + 4.0 * approxHourAngle
        }

        // Step 2: Refine calculations using Julian Century at approx event time
        val tEvent = getJulianCentury(jd + (approxEventMinutes / 1440.0))
        val l0Event = geomMeanLongSun(tEvent)
        val mEvent = geomMeanAnomalySun(tEvent)
        val eEvent = eccentricityEarthOrbit(tEvent)
        val obliqCorrEvent = obliquityCorrection(tEvent, meanObliquityOfEcliptic(tEvent))
        val trueLongEvent = l0Event + sunEqOfCenter(tEvent, mEvent)
        val appLongEvent = sunApparentLong(tEvent, trueLongEvent)
        val decEvent = sunDeclination(obliqCorrEvent, appLongEvent)
        val eqTimeEvent = equationOfTime(l0Event, mEvent, eEvent, obliqCorrEvent)

        val refinedNoonMinutes = solarNoonUtc(longitude, eqTimeEvent)
        val refinedHourAngle = hourAngle(latitude, decEvent, zenith) ?: return null

        return if (isSunrise) {
            refinedNoonMinutes - 4.0 * refinedHourAngle
        } else {
            refinedNoonMinutes + 4.0 * refinedHourAngle
        }
    }

    // --- Astronomical Formula Implementations ---

    private fun getJulianDay(date: LocalDate): Double {
        var year = date.year
        var month = date.monthValue
        val day = date.dayOfMonth

        if (month <= 2) {
            year -= 1
            month += 12
        }

        val a = floor(year / 100.0)
        val b = 2.0 - a + floor(a / 4.0)

        return floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1)) + day + b - 1524.5
    }

    private fun getJulianCentury(jd: Double): Double = (jd - 2451545.0) / 36525.0

    private fun geomMeanLongSun(t: Double): Double {
        var l0 = 280.46646 + t * (36000.76983 + t * 0.0003032)
        l0 %= 360.0
        if (l0 < 0.0) l0 += 360.0
        return l0
    }

    private fun geomMeanAnomalySun(t: Double): Double {
        var m = 357.52911 + t * (35999.05029 - 0.0001537 * t)
        m %= 360.0
        if (m < 0.0) m += 360.0
        return m
    }

    private fun eccentricityEarthOrbit(t: Double): Double =
        0.016708634 - t * (0.000042037 + 0.0000001267 * t)

    private fun sunEqOfCenter(t: Double, m: Double): Double {
        val mRad = Math.toRadians(m)
        return sin(mRad) * (1.914602 - t * (0.004817 + 0.000014 * t)) +
                sin(2.0 * mRad) * (0.019993 - 0.000101 * t) +
                sin(3.0 * mRad) * 0.000289
    }

    private fun sunApparentLong(t: Double, trueLong: Double): Double {
        val omega = 125.04 - 1934.136 * t
        return trueLong - 0.00569 - 0.00478 * sin(Math.toRadians(omega))
    }

    private fun meanObliquityOfEcliptic(t: Double): Double {
        val seconds = 21.448 - t * (46.815 + t * (0.00059 - t * 0.001813))
        return 23.0 + (26.0 + (seconds / 60.0)) / 60.0
    }

    private fun obliquityCorrection(t: Double, meanObliq: Double): Double {
        val omega = 125.04 - 1934.136 * t
        return meanObliq + 0.00256 * cos(Math.toRadians(omega))
    }

    private fun sunDeclination(obliqCorr: Double, appLong: Double): Double {
        val sinDec = sin(Math.toRadians(obliqCorr)) * sin(Math.toRadians(appLong))
        return Math.toDegrees(asin(sinDec))
    }

    private fun equationOfTime(l0: Double, m: Double, e: Double, obliqCorr: Double): Double {
        val y = tan(Math.toRadians(obliqCorr) / 2.0).pow(2)
        val l0Rad = Math.toRadians(l0)
        val mRad = Math.toRadians(m)

        val eTime = y * sin(2.0 * l0Rad) -
                2.0 * e * sin(mRad) +
                4.0 * e * y * sin(mRad) * cos(2.0 * l0Rad) -
                0.5 * y * y * sin(4.0 * l0Rad) -
                1.25 * e * e * sin(2.0 * mRad)

        return 4.0 * Math.toDegrees(eTime)
    }

    private fun solarNoonUtc(longitude: Double, eTime: Double): Double =
        720.0 - (4.0 * longitude) - eTime

    private fun hourAngle(latitude: Double, declination: Double, zenith: Double): Double? {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val zenRad = Math.toRadians(zenith)

        val cosH = (cos(zenRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        if (cosH > 1.0 || cosH < -1.0) {
            return null // Sun does not reach the specified zenith (polar day or night)
        }
        return Math.toDegrees(acos(cosH))
    }

    private fun minutesToIsoUtcString(date: LocalDate, minutesFromMidnight: Double?): String? {
        if (minutesFromMidnight == null || minutesFromMidnight.isNaN()) return null
        val totalSeconds = (minutesFromMidnight * 60.0).roundToLong()
        val baseUtc = date.atStartOfDay(ZoneOffset.UTC)
        val zdt = baseUtc.plusSeconds(totalSeconds)
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
