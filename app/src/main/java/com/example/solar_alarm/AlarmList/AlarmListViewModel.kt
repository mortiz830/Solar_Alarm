package com.example.solar_alarm.alarmList

// Repair: Fixed broken package/import lines
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.solar_alarm.data.AlarmDisplayData
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.SolarAlarmApp

@RequiresApi(api = Build.VERSION_CODES.O)
class AlarmListViewModel(solarAlarmApp: SolarAlarmApp, locationListViewModel: LocationListViewModel, solarTimeViewModel: SolarTimeViewModel,
                         solarAlarmViewModel: SolarAlarmViewModel) : AndroidViewModel(solarAlarmApp)
{
    var alarmDisplayLiveData: LiveData<List<AlarmDisplayData?>?>? = null

    init {
    }

    fun update(alarm: SolarAlarm?) {
    }

    fun delete(alarm: SolarAlarm?) {
    }

    fun getSolarAlarmLiveData(): LiveData<List<SolarAlarm?>?>? {
        return null
    }
}
