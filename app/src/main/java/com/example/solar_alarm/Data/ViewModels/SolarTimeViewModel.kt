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
    val allSolarTimes: LiveData<List<SolarTime>> = repository.all.asLiveData()

    fun insert(solarTime: SolarTime) = viewModelScope.launch { repository.insert(solarTime) }
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
