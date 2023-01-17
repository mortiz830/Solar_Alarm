package com.example.solar_alarm.Data.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class SolarTimeViewModel(private val repository: SolarTimeRepository) : ViewModel()
{
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val AllSolarTimes: LiveData<List<SolarTime>> = repository.all.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun Insert(solarTime: SolarTime) = viewModelScope.launch { repository.Insert(solarTime) }
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
