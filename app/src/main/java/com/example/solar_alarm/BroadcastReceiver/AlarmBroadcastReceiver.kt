package com.example.solar_alarm.BroadcastReceiver

import android.content.Intent
import com.example.solar_alarm.Service.AlarmService
import android.os.Build
import android.content.BroadcastReceiver
import android.widget.Toast
import com.example.solar_alarm.Service.RescheduleAlarmService
import android.content.Context
import android.util.Log
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                val toastText = String.format("Alarm Reboot")
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                startRescheduleAlarmsService(context)
            } else {
                val toastText = String.format("Alarm Received")
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                if (!intent.getBooleanExtra(RECURRING, false)) {
                    startAlarmService(context, intent)
                }
                run {
                    if (alarmIsToday(intent)) {
                        startAlarmService(context, intent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmBroadcastReceiver", "Error", e)
        }
    }

    private fun alarmIsToday(intent: Intent): Boolean {
        var alarmIsToday = false
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_WEEK]
        when (today) {
            Calendar.MONDAY -> {
                if (intent.getBooleanExtra(MONDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(TUESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.TUESDAY -> {
                if (intent.getBooleanExtra(TUESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.WEDNESDAY -> {
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.THURSDAY -> {
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.FRIDAY -> {
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.SATURDAY -> {
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.SUNDAY -> if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
        }
        return alarmIsToday
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    companion object {
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val TITLE = "TITLE"
    }
}