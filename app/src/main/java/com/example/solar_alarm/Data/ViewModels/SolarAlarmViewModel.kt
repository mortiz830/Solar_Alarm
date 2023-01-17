package com.example.solar_alarm.Data.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Tables.SolarAlarm
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmViewModel(private val repository: SolarAlarmRepository) : ViewModel()
{
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val AllSolarAlarms: LiveData<List<SolarAlarm>> = repository.all.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun Insert(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.Insert(solarAlarm) }
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
