package com.example.solar_alarm.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.data.repositories.LocationRepository
import com.example.solar_alarm.data.tables.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LocationUiState {
    object Loading : LocationUiState
    data class Success(val locations: List<Location>) : LocationUiState
    data class Error(val message: String) : LocationUiState
}

@HiltViewModel
class LocationListViewModel @Inject constructor(private val repository: LocationRepository) : ViewModel()
{
    val uiState: StateFlow<LocationUiState> = repository.all
        .map<List<Location>, LocationUiState> { locations -> LocationUiState.Success(locations) }
        .catch { throwable -> emit(LocationUiState.Error(throwable.localizedMessage ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = LocationUiState.Loading
        )

    val locationsState: StateFlow<List<Location>> = repository.all
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val allLocations: LiveData<List<Location>> = repository.all.asLiveData()

    fun insert(location: Location) = viewModelScope.launch { repository.insert(location) }

    fun getLocationString(location: Location?): String
    {
        return location?.let { "${it.Id}, ${it.Name}, ${it.Latitude}, ${it.Longitude}, ${it.CreateDateTimeUtc}" } ?: "Location not found."
    }

    suspend fun getByName(locationName: String) : Location
    {
        return repository.getByName(locationName)
    }

    suspend fun getById(locationId: Int) : Location
    {
        return repository.getById(locationId)
    }

    fun getLocationStrings(locations: List<Location>): List<String> {
        return locations.map { getLocationString(it) }
    }

    suspend fun doesLocationNameExists(name: String?): Boolean
    {
        return repository.doesLocationNameExists(name)
    }

    suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean
    {
        return repository.doesLocationLatLongExists(latitude, longitude)
    }
}

class LocationViewModelFactory(private val repository: LocationRepository) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass == LocationListViewModel::class.java)
        {
            return LocationListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
