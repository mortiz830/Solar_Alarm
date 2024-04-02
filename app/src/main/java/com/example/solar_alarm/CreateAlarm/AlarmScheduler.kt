package com.example.solar_alarm.CreateAlarm

import android.content.Intent
import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlin.Throws
import android.widget.Toast
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import java.lang.Exception
import java.time.ZonedDateTime
import java.util.*

class AlarmScheduler(private val solarAlarm: SolarAlarm, private val solarTime: SolarTime, private val hours: Int, private val mins: Int) {
    private var started = false
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun schedule(context: Context?) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(AlarmBroadcastReceiver.Companion.RECURRING, solarAlarm.Recurring)
        intent.putExtra(AlarmBroadcastReceiver.Companion.MONDAY, solarAlarm.Monday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.TUESDAY, solarAlarm.Tuesday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.WEDNESDAY, solarAlarm.Wednesday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.THURSDAY, solarAlarm.Thursday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.FRIDAY, solarAlarm.Friday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.SATURDAY, solarAlarm.Saturday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.SUNDAY, solarAlarm.Sunday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.TITLE, solarAlarm.Name)
        val localZonedDateTime: ZonedDateTime
        localZonedDateTime = if (true) ZonedDateTime.now().plusMinutes(mins.toLong()) else  // DEBUG_STATEMENT makes alarm ring immediately
            solarAlarm.SolarTimeTypeId?.let { solarTime.GetLocalZonedDateTime(it) }!!
        if (solarAlarm.OffsetTypeId == OffsetTypeEnum.Before) {
            localZonedDateTime.minusHours(hours.toLong())
            localZonedDateTime.minusMinutes(mins.toLong())
        } else if (solarAlarm.OffsetTypeId == OffsetTypeEnum.After) {
            localZonedDateTime.plusHours(hours.toLong())
            localZonedDateTime.plusMinutes(mins.toLong())
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = localZonedDateTime.hour
        calendar[Calendar.MINUTE] = localZonedDateTime.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmPendingIntent = PendingIntent.getBroadcast(context, solarAlarm.Id, intent, 0)
        if (solarAlarm.Recurring) {
            val toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, recurringDaysText, localZonedDateTime.hour, localZonedDateTime.minute, solarAlarm.Id)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            val RUN_DAILY = (24 * 60 * 60 * 1000).toLong()
            try {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, RUN_DAILY, alarmPendingIntent)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        } else {
            val toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, DayUtil.toDay(calendar[Calendar.DAY_OF_WEEK]), localZonedDateTime.hour, localZonedDateTime.minute, solarAlarm.Id)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        started = true
    }

    val recurringDaysText: String?
        get() {
            if (!solarAlarm.Recurring) {
                return null
            }
            var days = ""
            if (solarAlarm.Monday) {
                days += "Mo "
            }
            if (solarAlarm.Tuesday) {
                days += "Tu "
            }
            if (solarAlarm.Wednesday) {
                days += "We "
            }
            if (solarAlarm.Thursday) {
                days += "Th "
            }
            if (solarAlarm.Friday) {
                days += "Fr "
            }
            if (solarAlarm.Saturday) {
                days += "Sa "
            }
            if (solarAlarm.Sunday) {
                days += "Su "
            }
            return days
        }
}