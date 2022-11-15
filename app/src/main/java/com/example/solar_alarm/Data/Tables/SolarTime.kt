package com.example.solar_alarm.Data.Tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(tableName = "SolarTime", indices = [Index(value = ["SolarDate", "LocationId"], unique = true)], foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["Id"],
            childColumns = ["LocationId"],
            onDelete = ForeignKey.CASCADE)]
)
class SolarTime : TableBase {
    @JvmField
    var SolarDate: LocalDate? = null

    @JvmField
    var LocationId = 0
    @JvmField
    var DayLength = 0

    // UTC Time Stings in ISO_OFFSET_DATE_TIME format
    @JvmField
    var SunriseUtc: String? = null
    @JvmField
    var SunsetUtc: String? = null
    @JvmField
    var SolarNoonUtc: String? = null
    @JvmField
    var CivilTwilightBeginUtc: String? = null
    @JvmField
    var CivilTwilightEndUtc: String? = null
    @JvmField
    var NauticalTwilightBeginUtc: String? = null
    @JvmField
    var NauticalTwilightEndUtc: String? = null
    @JvmField
    var AstronomicalTwilightBeginUtc: String? = null
    @JvmField
    var AstronomicalTwilightEndUtc: String? = null

    constructor() {} // explicitly create default constructor for Android Room

    @RequiresApi(api = Build.VERSION_CODES.O)
    constructor(location: Location, sunriseSunsetResponse: SunriseSunsetResponse) {
        SolarDate = sunriseSunsetResponse.request!!.RequestDate
        LocationId = location.Id
        DayLength = sunriseSunsetResponse.dayLength
        SunriseUtc = sunriseSunsetResponse.sunrise
        SunsetUtc = sunriseSunsetResponse.sunset
        SolarNoonUtc = sunriseSunsetResponse.solarNoon
        CivilTwilightBeginUtc = sunriseSunsetResponse.civilTwilightBegin
        CivilTwilightEndUtc = sunriseSunsetResponse.civilTwilightEnd
        NauticalTwilightBeginUtc = sunriseSunsetResponse.nauticalTwilightBegin
        NauticalTwilightEndUtc = sunriseSunsetResponse.nauticalTwilightEnd
        AstronomicalTwilightBeginUtc = sunriseSunsetResponse.astronomicalTwilightBegin
        AstronomicalTwilightEndUtc = sunriseSunsetResponse.astronomicalTwilightEnd
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun GetLocalZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime {
        val utcDateTime = GetUtcZonedDateTime(solarTimeTypeEnum)
        val zoneId = ZoneId.systemDefault()
        return utcDateTime.withZoneSameInstant(zoneId)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    private fun GetUtcZonedDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): ZonedDateTime {
        val localDateTime = getLocalDateTime(solarTimeTypeEnum)
        val zoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC)
        return localDateTime.atZone(zoneId)
    }

    @Throws(Exception::class)
    private fun getLocalDateTime(solarTimeTypeEnum: SolarTimeTypeEnum): LocalDateTime {
        val utcString: String?
        utcString = when (solarTimeTypeEnum) {
            SolarTimeTypeEnum.Sunrise -> SunriseUtc
            SolarTimeTypeEnum.Sunset -> SunsetUtc
            SolarTimeTypeEnum.SolarNoon -> SolarNoonUtc
            SolarTimeTypeEnum.CivilTwilightBegin -> CivilTwilightBeginUtc
            SolarTimeTypeEnum.CivilTwilightEnd -> CivilTwilightEndUtc
            SolarTimeTypeEnum.NauticalTwilightBegin -> NauticalTwilightBeginUtc
            SolarTimeTypeEnum.NauticalTwilightEnd -> NauticalTwilightEndUtc
            SolarTimeTypeEnum.AstronomicalTwilightBegin -> AstronomicalTwilightBeginUtc
            SolarTimeTypeEnum.AstronomicalTwilightEnd -> AstronomicalTwilightEndUtc
            else -> throw Exception("Enum not implemented")
        }
        return LocalDateTime.parse(utcString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}