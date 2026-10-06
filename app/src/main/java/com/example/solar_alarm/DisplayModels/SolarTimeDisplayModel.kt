package com.example.solar_alarm.displayModels

// Repair: Fixed broken package/import lines
import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import com.example.solar_alarm.data.tables.SolarTime
import kotlin.Throws
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import java.lang.Exception
import java.time.ZonedDateTime

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarTimeDisplayModel(private val _Application: Application, private val _SolarTime: SolarTime?) {
    @get:Throws(Exception::class)
    val sunrise: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise)

    @get:Throws(Exception::class)
    val sunset: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.Sunset)

    @get:Throws(Exception::class)
    val solarNoon: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon)

    @get:Throws(Exception::class)
    val civilTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightBegin)

    @get:Throws(Exception::class)
    val civilTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightEnd)

    @get:Throws(Exception::class)
    val nauticalTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightBegin)

    @get:Throws(Exception::class)
    val nauticalTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightEnd)

    @get:Throws(Exception::class)
    val astronomicalTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightBegin)

    @get:Throws(Exception::class)
    val astronomicalTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.getLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightEnd)
}