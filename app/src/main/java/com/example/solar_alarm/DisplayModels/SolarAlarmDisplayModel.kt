package com.example.solar_alarm.DisplayModels

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Tables.SolarAlarm
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Repositories.LocationRepository
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import java.lang.Exception
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.HashMap

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarAlarmDisplayModel(private val _Application: Application, private val _SolarAlarm: SolarAlarm) : AndroidViewModel(_Application) {
    private var _LocationDisplayModel: LocationDisplayModel? = null
    private var _SolarTimeDisplayModel: SolarTimeDisplayModel? = null
    private var _RecurrenceDays: HashMap<DayOfWeek, Boolean>? = null
    fun GetLocation(): LocationDisplayModel {
        if (_LocationDisplayModel == null) {
            //val location = LocationRepository().GetById(_SolarAlarm.LocationId)
            //_LocationDisplayModel = location?.let { LocationDisplayModel(_Application, it) }
        }
        return _LocationDisplayModel!!
    }

    fun GetSolarTime(): SolarTimeDisplayModel {
        if (_SolarTimeDisplayModel == null) {
            //val solarTime = SolarTimeRepository().GetById(_SolarAlarm.LocationId)
            //_SolarTimeDisplayModel = SolarTimeDisplayModel(_Application, solarTime)
        }
        return _SolarTimeDisplayModel!!
    }

    fun GetName(): String? {
        return _SolarAlarm.Name
    }

    fun GetRecurrenceDays(): Map<DayOfWeek, Boolean> {
        if (_RecurrenceDays == null) {
            _RecurrenceDays = HashMap()
            _RecurrenceDays!![DayOfWeek.MONDAY] = _SolarAlarm.Monday
            _RecurrenceDays!![DayOfWeek.TUESDAY] = _SolarAlarm.Tuesday
            _RecurrenceDays!![DayOfWeek.WEDNESDAY] = _SolarAlarm.Wednesday
            _RecurrenceDays!![DayOfWeek.THURSDAY] = _SolarAlarm.Thursday
            _RecurrenceDays!![DayOfWeek.FRIDAY] = _SolarAlarm.Friday
            _RecurrenceDays!![DayOfWeek.SATURDAY] = _SolarAlarm.Saturday
            _RecurrenceDays!![DayOfWeek.SUNDAY] = _SolarAlarm.Sunday
        }
        return _RecurrenceDays!!
    }

    fun IsRecurring(): Boolean {
        return _SolarAlarm!!.Recurring
    }

    fun GetSetAlarmTime(): ZonedDateTime? {
        try {
            return when (_SolarAlarm!!.SolarTimeTypeId) {
                SolarTimeTypeEnum.Sunrise -> _SolarTimeDisplayModel?.sunrise
                SolarTimeTypeEnum.Sunset -> _SolarTimeDisplayModel?.sunset
                SolarTimeTypeEnum.SolarNoon -> _SolarTimeDisplayModel?.solarNoon
                SolarTimeTypeEnum.CivilTwilightBegin -> _SolarTimeDisplayModel?.civilTwilightBegin
                SolarTimeTypeEnum.CivilTwilightEnd -> _SolarTimeDisplayModel?.civilTwilightEnd
                SolarTimeTypeEnum.NauticalTwilightBegin -> _SolarTimeDisplayModel?.nauticalTwilightBegin
                SolarTimeTypeEnum.NauticalTwilightEnd -> _SolarTimeDisplayModel?.nauticalTwilightEnd
                SolarTimeTypeEnum.AstronomicalTwilightBegin -> _SolarTimeDisplayModel?.astronomicalTwilightBegin
                SolarTimeTypeEnum.AstronomicalTwilightEnd -> _SolarTimeDisplayModel?.astronomicalTwilightEnd
                else -> {return null}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}