package com.example.solar_alarm.Activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity()
{
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory((application as SolarAlarmApp).locationRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}