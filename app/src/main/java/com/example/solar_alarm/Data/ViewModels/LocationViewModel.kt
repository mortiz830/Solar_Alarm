package com.example.solar_alarm.Data.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.Data.Repositories.LocationRepository
import com.example.solar_alarm.Data.Tables.Location
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class LocationViewModel(private val repository: LocationRepository) : ViewModel()
{
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val All: LiveData<List<Location>> = repository.all//.asLiveData()

    fun GetAll(): LiveData<List<Location>> {
        return repository.GetAllLocations()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun Insert(location: Location) = viewModelScope.launch { repository.Insert(location) }

    fun DoesLocationLatLongExists(latitude: Double, longitude: Double) = viewModelScope.launch { repository.DoesLocationLatLongExists(latitude, longitude) }

    fun DoesLocationNameExists(locationName : String) = viewModelScope.launch { repository.DoesLocationNameExists(locationName) }

    fun MaxId() = viewModelScope.launch { repository.MaxId() }

    fun getLocationString(location: Location?): String {
        return location?.let { "${it.Id}, ${it.Name}, ${it.Latitude}, ${it.Longitude}, ${it.CreateDateTimeUtc}" } ?: "Location not found."
    }

    fun getByName(locationName: String) : Location?
    {
        return repository.GetByName(locationName)
    }

    fun getById(locationId: Int) : Location?
    {
        return repository.GetById(locationId)
    }

    fun getLocationStrings(locations: List<Location>): List<String> {
        return locations.map { getLocationString(it) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class LocationViewModelFactory(private val repository: LocationRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
