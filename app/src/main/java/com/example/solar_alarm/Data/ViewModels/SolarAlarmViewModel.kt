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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SolarAlarmUiState {
    object Loading : SolarAlarmUiState
    data class Success(val alarms: List<SolarAlarmWithDetails>) : SolarAlarmUiState
    data class Error(val message: String) : SolarAlarmUiState
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SolarAlarmViewModel @Inject constructor(private val repository: SolarAlarmRepository) : ViewModel()
{
    val uiState: StateFlow<SolarAlarmUiState> = repository.allWithDetails
        .map<List<SolarAlarmWithDetails>, SolarAlarmUiState> { alarms -> SolarAlarmUiState.Success(alarms) }
        .catch { throwable -> emit(SolarAlarmUiState.Error(throwable.localizedMessage ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SolarAlarmUiState.Loading
        )

    val solarAlarmsState: StateFlow<List<SolarAlarm>> = repository.all
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val solarAlarmsWithDetailsState: StateFlow<List<SolarAlarmWithDetails>> = repository.allWithDetails
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val allSolarAlarms: LiveData<List<SolarAlarm>> = repository.all.asLiveData()
    val allSolarAlarmsWithDetails: LiveData<List<SolarAlarmWithDetails>> = repository.allWithDetails.asLiveData()

    fun insert(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.insert(solarAlarm) }

    fun update(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.update(solarAlarm) }

    fun delete(solarAlarm: SolarAlarm) = viewModelScope.launch { repository.delete(solarAlarm) }
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
