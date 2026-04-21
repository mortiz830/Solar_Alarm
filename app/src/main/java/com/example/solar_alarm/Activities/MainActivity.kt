package com.example.solar_alarm.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.solar_alarm.Data.ViewModels.LocationListViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp


class MainActivity : AppCompatActivity()
{
    private val locationListViewModel: LocationListViewModel by viewModels {
        val app = application as SolarAlarmApp
        LocationViewModelFactory(app.locationRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
