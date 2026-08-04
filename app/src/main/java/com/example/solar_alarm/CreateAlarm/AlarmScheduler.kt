package com.example.solar_alarm.createAlarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.solar_alarm.broadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarTime
import java.time.ZonedDateTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class AlarmScheduler(private val solarAlarm: SolarAlarm, private val solarTime: SolarTime, private val hours: Int, private val mins: Int)
{
    private var started = false

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getIntent(context: Context) : Intent
    {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra("SolarAlarm", solarAlarm)
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

    fun getPendingIntent(context: Context, intent : Intent) : PendingIntent
    {
        try
        {
            return PendingIntent.getBroadcast(context, solarAlarm.Id, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        catch (exception: Exception)
        {
            exception.printStackTrace()
            throw exception
        }
    }

    fun getLocalZonedDateTime() : ZonedDateTime
    {
        var localZonedDateTime = if (true) ZonedDateTime.now().plusMinutes(mins.toLong() + 1) else 
            solarTime.getLocalZonedDateTime(solarAlarm.SolarTimeTypeId)

        if (solarAlarm.OffsetTypeId == OffsetTypeEnum.Before)
        {
            localZonedDateTime = localZonedDateTime.minusHours(hours.toLong()).minusMinutes(mins.toLong())
        }
        else if (solarAlarm.OffsetTypeId == OffsetTypeEnum.After)
        {
            localZonedDateTime = localZonedDateTime.plusHours(hours.toLong()).plusMinutes(mins.toLong())
        }

        return localZonedDateTime
    }

    fun schedule(context: Context)
    {
        val intent             = getIntent(context)
        val localZonedDateTime = getLocalZonedDateTime()
        val calendar           = getCalendarInstance(localZonedDateTime)
        val alarmManager       = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent      = getPendingIntent(context, intent)

        if (solarAlarm.Recurring)
        {
            val toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, recurringDaysText, localZonedDateTime.hour, localZonedDateTime.minute)
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
            val toastText = String.format("One Time Alarm %s scheduled for at %02d:%02d", solarAlarm.Name, localZonedDateTime.hour, localZonedDateTime.minute)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

            try
            {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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
