package com.example.solar_alarm.CreateAlarm

import com.example.solar_alarm.Data.Alarm
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.solar_alarm.Data.AlarmRepository

class CreateAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository

    init {
        alarmRepository = AlarmRepository(application)
    }

    fun insert(alarm: Alarm?) {
        alarmRepository.insert(alarm)
    }

    fun update(alarm: Alarm?) {
        alarmRepository.update(alarm)
    }
}