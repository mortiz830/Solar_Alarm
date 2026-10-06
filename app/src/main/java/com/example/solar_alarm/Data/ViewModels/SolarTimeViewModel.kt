package com.example.solar_alarm.data.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.tables.SolarTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SolarTimeViewModel @Inject constructor(private val repository: SolarTimeRepository) : ViewModel()
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
