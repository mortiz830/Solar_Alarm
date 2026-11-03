package com.example.solar_alarm.createAlarm

// Repair: Fixed broken package/import lines
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
        if (DebugSettings.ringOneMinuteAfterCreation) {
            intent.putExtra(DebugSettings.EXTRA_IS_DEBUG_ONE_MINUTE, true)
        }
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
        var localZonedDateTime = if (DebugSettings.ringOneMinuteAfterCreation) {
            ZonedDateTime.now().plusMinutes(1)
        } else {
            solarTime.getLocalZonedDateTime(solarAlarm.SolarTimeTypeId)
        }

        if (!DebugSettings.ringOneMinuteAfterCreation) {
            if (solarAlarm.OffsetTypeId == OffsetTypeEnum.Before)
            {
                localZonedDateTime = localZonedDateTime.minusHours(hours.toLong()).minusMinutes(mins.toLong())
            }
            else if (solarAlarm.OffsetTypeId == OffsetTypeEnum.After)
            {
                localZonedDateTime = localZonedDateTime.plusHours(hours.toLong()).plusMinutes(mins.toLong())
            }
        }

        return localZonedDateTime
    }

    fun schedule(context: Context)
    {
        val intent             = getIntent(context)
        val isDebugOneMinute   = DebugSettings.ringOneMinuteAfterCreation
        val localZonedDateTime = getLocalZonedDateTime()
        val calendar           = getCalendarInstance(localZonedDateTime)
        val alarmManager       = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent      = getPendingIntent(context, intent)

        val triggerTimeMillis: Long
        val toastText: String

        if (isDebugOneMinute) {
            triggerTimeMillis = System.currentTimeMillis() + 60 * 1000L
            toastText = "[DEBUG] Alarm '${solarAlarm.Name}' scheduled to ring in 1 minute!"
        } else {
            triggerTimeMillis = calendar.timeInMillis
            toastText = if (solarAlarm.Recurring) {
                String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, recurringDaysText, localZonedDateTime.hour, localZonedDateTime.minute)
            } else {
                String.format("One Time Alarm %s scheduled for at %02d:%02d", solarAlarm.Name, localZonedDateTime.hour, localZonedDateTime.minute)
            }
        }
        
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

        try {
            // Always set an exact one-shot alarm. 
            // Recurring solar alarms must be rescheduled manually to account for shifting times.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, 
                triggerTimeMillis, 
                pendingIntent
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        started = true
    }

    fun cancel(context: Context) {
        try {
            val intent = getIntent(context)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                solarAlarm.Id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            cancelSnooze(context, solarAlarm.Id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    companion object {
        const val EXTRA_IS_SNOOZE = "EXTRA_IS_SNOOZE"
        const val ACTION_SNOOZE_ALARM = "com.example.solar_alarm.ACTION_SNOOZE_ALARM"
        const val DEFAULT_SNOOZE_MINUTES = 10
        const val SNOOZE_REQUEST_CODE_OFFSET = 100000

        fun snooze(context: Context, solarAlarm: SolarAlarm, snoozeMinutes: Int = DEFAULT_SNOOZE_MINUTES) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra("SolarAlarm", solarAlarm)
                putExtra(EXTRA_IS_SNOOZE, true)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                solarAlarm.Id + SNOOZE_REQUEST_CODE_OFFSET,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val triggerTimeMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
                Toast.makeText(context, "Alarm snoozed for $snoozeMinutes minutes", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cancelSnooze(context: Context, solarAlarmId: Int) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
                    action = ACTION_SNOOZE_ALARM
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    solarAlarmId + SNOOZE_REQUEST_CODE_OFFSET,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
