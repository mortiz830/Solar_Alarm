package com.example.solar_alarm.Data.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.Data.Repositories.LocationRepository
import com.example.solar_alarm.Data.Tables.Location
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class LocationListViewModel(private val repository: LocationRepository) : ViewModel()
{
    val AllLocations: LiveData<List<Location>> = repository.all.asLiveData()

    fun Insert(location: Location) = viewModelScope.launch { repository.Insert(location) }

    fun getLocationString(location: Location?): String
    {
        return location?.let { "${it.Id}, ${it.Name}, ${it.Latitude}, ${it.Longitude}, ${it.CreateDateTimeUtc}" } ?: "Location not found."
    }

    fun getByName(locationName: String) : Location
    {
        return repository.GetByName(locationName)
    }

    fun getById(locationId: Int) : Location
    {
        return repository.GetById(locationId)
    }

    fun getLocationStrings(locations: List<Location>): List<String> {
        return locations.map { getLocationString(it) }
    }

    suspend fun DoesLocationNameExists(name: String?): Boolean
    {
        return repository.DoesLocationNameExists(name)
    }

    suspend fun DoesLocationLatLongExists(latitude: Double, longitude: Double): Boolean
    {
        return repository.DoesLocationLatLongExists(latitude, longitude)
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
