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

@RequiresApi(Build.VERSION_CODES.O)
class AlarmScheduler(private val solarAlarm: SolarAlarm, private val solarTime: SolarTime, private val hours: Int, private val mins: Int)
{
    private var started = false
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)

    fun GetIntent(context: Context) : Intent
    {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra(AlarmBroadcastReceiver.RECURRING, solarAlarm.Recurring)
        intent.putExtra(AlarmBroadcastReceiver.MONDAY,    solarAlarm.Monday)
        intent.putExtra(AlarmBroadcastReceiver.TUESDAY,   solarAlarm.Tuesday)
        intent.putExtra(AlarmBroadcastReceiver.WEDNESDAY, solarAlarm.Wednesday)
        intent.putExtra(AlarmBroadcastReceiver.THURSDAY,  solarAlarm.Thursday)
        intent.putExtra(AlarmBroadcastReceiver.FRIDAY,    solarAlarm.Friday)
        intent.putExtra(AlarmBroadcastReceiver.SATURDAY,  solarAlarm.Saturday)
        intent.putExtra(AlarmBroadcastReceiver.SUNDAY,    solarAlarm.Sunday)
        intent.putExtra(AlarmBroadcastReceiver.TITLE,     solarAlarm.Name)

        return intent
    }

    fun getCalendarInstance(localZonedDateTime: ZonedDateTime) : Calendar
    {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis          = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = localZonedDateTime.hour
        calendar[Calendar.MINUTE]      = localZonedDateTime.minute
        calendar[Calendar.SECOND]      = 0
        calendar[Calendar.MILLISECOND] = 0

        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis())
        {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }

        return calendar
    }

    fun GetPendingIntent(context: Context, intent : Intent) : PendingIntent
    {
        try
        {
            return PendingIntent.getBroadcast(context, solarAlarm.Id, intent, PendingIntent.FLAG_IMMUTABLE)// BROKEN AFTER CONVERSION
        }
        catch (exception: Exception)
        {
            exception.printStackTrace()
            throw exception
        }
    }

    fun GetLocalZonedDateTime() : ZonedDateTime
    {
        val localZonedDateTime = if (true) ZonedDateTime.now().plusMinutes(mins.toLong() + 1) else  // DEBUG_STATEMENT makes alarm ring immediately
            solarAlarm.SolarTimeTypeId?.let { solarTime.GetLocalZonedDateTime(it) }!!

        if (solarAlarm.OffsetTypeId == OffsetTypeEnum.Before)
        {
            localZonedDateTime.minusHours(hours.toLong())
            localZonedDateTime.minusMinutes(mins.toLong())
        }
        else if (solarAlarm.OffsetTypeId == OffsetTypeEnum.After)
        {
            localZonedDateTime.plusHours(hours.toLong())
            localZonedDateTime.plusMinutes(mins.toLong())
        }

        return localZonedDateTime
    }

    fun schedule(context: Context)
    {
        val intent             = GetIntent(context)
        val localZonedDateTime = GetLocalZonedDateTime()
        val calendar           = getCalendarInstance(localZonedDateTime)
        val alarmManager       = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var pendingIntent      = GetPendingIntent(context, intent)

        if (solarAlarm.Recurring)
        {
            val toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, recurringDaysText, localZonedDateTime.hour, localZonedDateTime.minute, solarAlarm.Id)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            val RUN_DAILY = (24 * 60 * 60 * 1000).toLong()

            try
            {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, RUN_DAILY, pendingIntent)
            }
            catch (exception: Exception)
            {
                exception.printStackTrace()
            }
        }
        else
        {
            val toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, DayUtil.toDay(calendar[Calendar.DAY_OF_WEEK]), localZonedDateTime.hour, localZonedDateTime.minute, solarAlarm.Id)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

            try
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            catch (exception: Exception)
            {
                exception.printStackTrace()
            }
        }

        started = true
    }

    private val recurringDaysText: String
        get()
        {
            var days = ""

            if (solarAlarm.Recurring)
            {
                if (solarAlarm.Monday)    days += "Mo "
                if (solarAlarm.Tuesday)   days += "Tu "
                if (solarAlarm.Wednesday) days += "We "
                if (solarAlarm.Thursday)  days += "Th "
                if (solarAlarm.Friday)    days += "Fr "
                if (solarAlarm.Saturday)  days += "Sa "
                if (solarAlarm.Sunday)    days += "Su "
            }

            return days
        }
}