package com.example.solar_alarm.data.viewmodels

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
