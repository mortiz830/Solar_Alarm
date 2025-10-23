package com.example.solar_alarm.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Service.AlarmService
import com.example.solar_alarm.Service.RescheduleAlarmService
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AlarmBroadcastReceiver : BroadcastReceiver()
{
    private lateinit var solarAlarm: SolarAlarm

    override fun onReceive(context: Context, intent: Intent)
    {
        try
        {
            solarAlarm = GetSolarAlarmFromIntent(intent)

            if (Intent.ACTION_BOOT_COMPLETED == intent.action)
            {
                val toastText = String.format("Alarm Reboot")
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                startRescheduleAlarmsService(context)
            }
            else
            {
                val toastText = String.format("Alarm Received")
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

                if (!solarAlarm.Recurring)
                {
                    startAlarmService(context, intent)
                }

                run {
                    if (alarmIsToday(intent))
                    {
                        startAlarmService(context, intent)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            Log.e("AlarmBroadcastReceiver", "Error", e)
        }
    }

    private fun alarmIsToday(intent: Intent): Boolean
    {
        var alarmIsToday = false
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_WEEK]

        if (solarAlarm.Active)
        {
            if (solarAlarm.Recurring)
            {
                when (today)
                {
                    Calendar.MONDAY    -> alarmIsToday = solarAlarm.Monday
                    Calendar.TUESDAY   -> alarmIsToday = solarAlarm.Tuesday
                    Calendar.WEDNESDAY -> alarmIsToday = solarAlarm.Wednesday
                    Calendar.THURSDAY  -> alarmIsToday = solarAlarm.Thursday
                    Calendar.FRIDAY    -> alarmIsToday = solarAlarm.Friday
                    Calendar.SATURDAY  -> alarmIsToday = solarAlarm.Saturday
                    Calendar.SUNDAY    -> alarmIsToday = solarAlarm.Sunday
                    else               -> alarmIsToday = false
                }
            }
        }

        return alarmIsToday
    }

    private fun startAlarmService(context: Context, intent: Intent)
    {
        val intentService = Intent(context, AlarmService::class.java)

        intentService.putExtra("TITLE", solarAlarm.Name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            context.startForegroundService(intentService)
        }
        else
        {
            context.startService(intentService)
        }
    }

    private fun startRescheduleAlarmsService(context: Context)
    {

        val intentService = Intent(context, RescheduleAlarmService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            context.startForegroundService(intentService)
        }
        else
        {
            context.startService(intentService)
        }
    }

    companion object
    {
        fun GetSolarAlarmFromIntent(intent: Intent) : SolarAlarm
        {
            try
            {
                val solarAlarm = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU)
                {
                    intent.getParcelableExtra("SolarAlarm", SolarAlarm::class.java)
                }
                else
                {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("SolarAlarm")
                }

                if (solarAlarm != null)
                {
                    Log.d("AlarmBroadcastReceiver", "solarAlarm is not null")
                }
                else
                {
                    Log.d("AlarmBroadcastReceiver", "solarAlarm is null")
                    throw NullPointerException()
                }

                return solarAlarm
            }
            catch (e: Exception)
            {
                throw e
            }
        }
    }
}
