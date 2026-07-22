package com.example.solar_alarm.Data.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Data.Tables.SolarAlarmWithDetails
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmViewModel(private val repository: SolarAlarmRepository) : ViewModel()
{
    val allSolarAlarms: LiveData<List<SolarAlarm>> = repository.all.asLiveData()
    val allSolarAlarmsWithDetails: LiveData<List<SolarAlarmWithDetails>> = repository.allWithDetails.asLiveData()

    fun insert(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.insert(solarAlarm) }
}

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmViewModelFactory(private val repository: SolarAlarmRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(SolarAlarmViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return SolarAlarmViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
