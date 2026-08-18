package com.example.solar_alarm.data.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.data.repositories.SolarAlarmRepository
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SolarAlarmViewModel @Inject constructor(private val repository: SolarAlarmRepository) : ViewModel()
{
    val allSolarAlarms: LiveData<List<SolarAlarm>> = repository.all.asLiveData()
    val allSolarAlarmsWithDetails: LiveData<List<SolarAlarmWithDetails>> = repository.allWithDetails.asLiveData()

    fun insert(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.insert(solarAlarm) }

    fun update(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.update(solarAlarm) }
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
