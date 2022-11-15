package com.example.solar_alarm.Service

import android.content.Intent
import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.AlarmRepository
import android.os.IBinder
import androidx.lifecycle.LifecycleService

class RescheduleAlarmService : LifecycleService() {
    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmRepository = AlarmRepository(application)
        alarmRepository.alarmsLiveData?.observe(this) { alarms ->
            for (a in alarms!!) {
                if (a?.isStarted!!) {
                    a?.schedule(applicationContext)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}