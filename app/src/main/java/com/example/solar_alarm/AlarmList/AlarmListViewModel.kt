package com.example.solar_alarm.AlarmList

import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.LocationRepository
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.AlarmDisplayDataDao
import com.example.solar_alarm.Data.AlarmDisplayData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Tables.*

@RequiresApi(api = Build.VERSION_CODES.O)
class AlarmListViewModel(application: Application) : AndroidViewModel(application) {
    private val solarTimeRepository: SolarTimeRepository? = null
    private val solarAlarmRepository: SolarAlarmRepository
    private val locationRepository: LocationRepository? = null
    val alarmsLiveData: LiveData<List<SolarAlarm?>?>?
    val solarTimeLiveData: LiveData<List<SolarTime?>?>?
    val locationLiveData: LiveData<List<Location?>?>?
    private val alarmDisplayDataDao: AlarmDisplayDataDao? = null
    var alarmDisplayLiveData: LiveData<List<AlarmDisplayData?>?>? = null

    //String rawQuery = "SELECT DISTINCT " + Location.Id + ","
    init {

        // to be deleted
        val db: SolarAlarmDatabase? = SolarAlarmDatabase.Companion.getDatabase(application)
        if (db != null) {
            alarmDisplayLiveData = db.alarmDisplayDataDao().loadAlarmData()
        }
        solarAlarmRepository = SolarAlarmRepository()
        alarmsLiveData = solarAlarmRepository.all
        solarTimeLiveData = SolarTimeRepository().all
        locationLiveData = LocationRepository().all
    }

    fun update(alarm: SolarAlarm?) {
        solarAlarmRepository.Update(alarm)
    }

    fun delete(alarm: SolarAlarm?) {
        solarAlarmRepository.Delete(alarm)
    }

    fun getSolarAlarmLiveData(): LiveData<List<SolarAlarm?>?>? {
        return alarmsLiveData
    }
}