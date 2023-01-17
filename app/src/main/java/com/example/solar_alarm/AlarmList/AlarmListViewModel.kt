package com.example.solar_alarm.AlarmList


import android.os.Build
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.AlarmDisplayData
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Tables.*
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory
import com.example.solar_alarm.SolarAlarmApp

@RequiresApi(api = Build.VERSION_CODES.O)
class AlarmListViewModel(solarAlarmApp: SolarAlarmApp) : AndroidViewModel(solarAlarmApp)
{
    //private val locationViewModel: LocationViewModel by viewModels { LocationViewModelFactory(solarAlarmApp.locationRepository) }
/*
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory((getApplication() as SolarAlarmApp).locationRepository)
    }
*/
    //private val solarTimeRepository: SolarTimeRepository? = null
    //private val solarAlarmRepository: SolarAlarmRepository
    //private val locationRepository: LocationRepository? = null
    //val alarmsLiveData: LiveData<List<SolarAlarm?>?>?
    //val solarTimeLiveData: LiveData<List<SolarTime?>?>?
    //val locationLiveData = locationViewModel.AllLocations
    //private val alarmDisplayDataDao: AlarmDisplayDataDao? = null
    var alarmDisplayLiveData: LiveData<List<AlarmDisplayData?>?>? = null

    //String rawQuery = "SELECT DISTINCT " + Location.Id + ","
    init {

        // to be deleted
        //val db: SolarAlarmDatabase? = SolarAlarmDatabase.getDatabase(solarAlarmApp, )
        //if (db != null) {
        //    alarmDisplayLiveData = db.alarmDisplayDataDao().loadAlarmData()
        //}
        //solarAlarmRepository = SolarAlarmRepository()
        //alarmsLiveData = solarAlarmRepository.all
        //solarTimeLiveData = SolarTimeRepository().all
    }

    fun update(alarm: SolarAlarm?) {
        //solarAlarmRepository.Update(alarm)
    }

    fun delete(alarm: SolarAlarm?) {
        //solarAlarmRepository.Delete(alarm)
    }

    fun getSolarAlarmLiveData(): LiveData<List<SolarAlarm?>?>? {
        return null//alarmsLiveData
    }
}