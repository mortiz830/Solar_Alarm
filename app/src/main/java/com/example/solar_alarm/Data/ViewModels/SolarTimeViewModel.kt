package com.example.solar_alarm.data.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface SolarTimeUiState {
    object Loading : SolarTimeUiState
    data class Success(val solarTimes: List<SolarTime>) : SolarTimeUiState
    data class Error(val message: String) : SolarTimeUiState
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SolarTimeViewModel @Inject constructor(private val repository: SolarTimeRepository) : ViewModel()
{
    val uiState: StateFlow<SolarTimeUiState> = repository.all
        .map<List<SolarTime>, SolarTimeUiState> { solarTimes -> SolarTimeUiState.Success(solarTimes) }
        .catch { throwable -> emit(SolarTimeUiState.Error(throwable.localizedMessage ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SolarTimeUiState.Loading
        )

    val solarTimesState: StateFlow<List<SolarTime>> = repository.all
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _selectedLocationSolarTimesState = MutableStateFlow<List<SolarTime>>(emptyList())
    val selectedLocationSolarTimesState: StateFlow<List<SolarTime>> = _selectedLocationSolarTimesState.asStateFlow()

    private val _errorMessageState = MutableStateFlow<String?>(null)
    val errorMessageState: StateFlow<String?> = _errorMessageState.asStateFlow()

    val allSolarTimes: LiveData<List<SolarTime>> = repository.all.asLiveData()
    val selectedLocationSolarTimes: LiveData<List<SolarTime>> = _selectedLocationSolarTimesState.asLiveData()
    val errorMessage: LiveData<String?> = _errorMessageState.asLiveData()

    fun insert(solarTime: SolarTime) = viewModelScope.launch { repository.insert(solarTime) }

    suspend fun getWeeklySolarTimes(location: Location, startDate: LocalDate = LocalDate.now()): Result<List<SolarTime>>
    {
        val result = repository.getWeeklySolarTimes(location, startDate)
        result.onSuccess { times ->
            _selectedLocationSolarTimesState.value = times
            _errorMessageState.value = null
        }.onFailure { error ->
            _errorMessageState.value = error.localizedMessage ?: "Failed to fetch solar times"
        }
        return result
    }

    fun loadWeeklySolarTimes(location: Location, startDate: LocalDate = LocalDate.now())
    {
        viewModelScope.launch {
            getWeeklySolarTimes(location, startDate)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class SolarTimeViewModelFactory(private val repository: SolarTimeRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(SolarTimeViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return SolarTimeViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
