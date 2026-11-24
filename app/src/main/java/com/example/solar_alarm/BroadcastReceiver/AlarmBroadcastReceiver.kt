package com.example.solar_alarm.broadcastReceiver

// Fixed broken line
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.solar_alarm.createAlarm.AlarmScheduler
import com.example.solar_alarm.createAlarm.DebugSettings
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.service.AlarmService
import com.example.solar_alarm.service.RescheduleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver() {
    private lateinit var solarAlarm: SolarAlarm

    @Inject
    lateinit var rescheduleHelper: RescheduleHelper

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action = intent.action
            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == "android.intent.action.QUICKBOOT_POWERON") {
                Log.d("ALARM_DEBUG", "Received boot/replacement action: $action. Rescheduling all active alarms.")
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        rescheduleHelper.rescheduleAllActiveAlarms(context.applicationContext)
                    } catch (e: Exception) {
                        Log.e("ALARM_DEBUG", "Error rescheduling on boot: ${e.message}", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
                return
            }

            // 1. Initialize data
            val dd = getSolarAlarmFromIntent(intent)

            if (dd != null)
            {
                solarAlarm = dd
                val isDebugOneMinute = intent.getBooleanExtra(DebugSettings.EXTRA_IS_DEBUG_ONE_MINUTE, false)
                val isSnooze = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_SNOOZE, false)
                Log.d("ALARM_DEBUG", "Received alarm: ${solarAlarm.Name}, isDebugOneMinute: $isDebugOneMinute, isSnooze: $isSnooze")

                // 2. Logic check: If it's recurring and not a debug or snooze alarm, check if today is an active day
                if (!isDebugOneMinute && !isSnooze && solarAlarm.Recurring && !alarmIsToday()) {
                    Log.d("ALARM_DEBUG", "Alarm skipped: Not scheduled for today.")
                    return
                }

                // 3. Start the AlarmService to handle notification, music and full screen intent
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("SolarAlarm", solarAlarm)
                    putExtra(AlarmScheduler.EXTRA_IS_SNOOZE, isSnooze)
                }

                context.startForegroundService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("ALARM_DEBUG", "Error in onReceive: ${e.message}")
        }
    }

    private fun alarmIsToday(): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar[Calendar.DAY_OF_WEEK]

        return when (today) {
            Calendar.MONDAY    -> solarAlarm.Monday
            Calendar.TUESDAY   -> solarAlarm.Tuesday
            Calendar.WEDNESDAY -> solarAlarm.Wednesday
            Calendar.THURSDAY  -> solarAlarm.Thursday
            Calendar.FRIDAY    -> solarAlarm.Friday
            Calendar.SATURDAY  -> solarAlarm.Saturday
            Calendar.SUNDAY    -> solarAlarm.Sunday
            else               -> false
        }
    }

    companion object {
        fun getSolarAlarmFromIntent(intent: Intent): SolarAlarm?
        {
            val solarAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                intent.getParcelableExtra("SolarAlarm", SolarAlarm::class.java)
            }
            else
            {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<SolarAlarm>("SolarAlarm")
            }
            return solarAlarm
        }
    }
}
