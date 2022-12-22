package com.example.solar_alarm.Data.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.solar_alarm.Data.Repositories.LocationRepository
import com.example.solar_alarm.Data.Tables.Location
import kotlinx.coroutines.launch

import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class LocationViewModel(private val repository: LocationRepository) : ViewModel()
{
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val AllLocations: LiveData<List<Location>> = repository.All.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun Insert(location: Location) = viewModelScope.launch { repository.Insert(location) }
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
