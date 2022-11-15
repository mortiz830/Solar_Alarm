package com.example.solar_alarm.DisplayModels

import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlin.Throws
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import java.lang.Exception
import java.time.ZonedDateTime

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarTimeDisplayModel(private val _Application: Application, private val _SolarTime: SolarTime?) {
    @get:Throws(Exception::class)
    val sunrise: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise)

    @get:Throws(Exception::class)
    val sunset: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset)

    @get:Throws(Exception::class)
    val solarNoon: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon)

    @get:Throws(Exception::class)
    val civilTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightBegin)

    @get:Throws(Exception::class)
    val civilTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightEnd)

    @get:Throws(Exception::class)
    val nauticalTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightBegin)

    @get:Throws(Exception::class)
    val nauticalTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightEnd)

    @get:Throws(Exception::class)
    val astronomicalTwilightBegin: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightBegin)

    @get:Throws(Exception::class)
    val astronomicalTwilightEnd: ZonedDateTime
        get() = _SolarTime!!.GetLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightEnd)
}