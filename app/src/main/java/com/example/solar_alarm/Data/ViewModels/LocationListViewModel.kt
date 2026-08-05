package com.example.solar_alarm.data.viewmodels

// Fixed broken line and properties
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.data.repositories.LocationRepository
import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.launch

class LocationListViewModel(private val repository: LocationRepository) : ViewModel()
{
    val allLocations: LiveData<List<Location>> = repository.all.asLiveData()

    fun insert(location: Location) = viewModelScope.launch { repository.insert(location) }

    fun update(location: Location) = viewModelScope.launch { repository.update(location) }

    fun delete(location: Location) = viewModelScope.launch { repository.delete(location) }

    fun doesLocationNameExists(name: String): Boolean {
        return allLocations.value?.any { it.Name == name } ?: false
    }

    fun doesLocationLatLongExists(lat: Double, long: Double): Boolean {
        return allLocations.value?.any { it.Latitude == lat && it.Longitude == long } ?: false
    }
}

class LocationViewModelFactory(private val repository: LocationRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass == LocationListViewModel::class.java)
        {
            @Suppress("UNCHECKED_CAST")
            return LocationListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
