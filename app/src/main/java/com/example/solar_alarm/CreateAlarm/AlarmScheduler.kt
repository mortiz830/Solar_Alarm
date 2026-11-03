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
import java.time.DayOfWeek
import java.time.LocalDate
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
        calendar.timeInMillis = localZonedDateTime.toInstant().toEpochMilli()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
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
            val dayName = localZonedDateTime.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
            val monthName = localZonedDateTime.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            val dateStr = "${dayName} ${localZonedDateTime.dayOfMonth}-$monthName"
            toastText = if (solarAlarm.Recurring) {
                String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, dateStr, localZonedDateTime.hour, localZonedDateTime.minute)
            } else {
                String.format("One Time Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, dateStr, localZonedDateTime.hour, localZonedDateTime.minute)
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

        /**
         * Calculates the exact trigger ZonedDateTime for a given SolarAlarm and SolarTime,
         * taking into account OffsetType (Before/After/Exact), OffsetHours, and OffsetMinutes.
         */
        fun calculateTriggerDateTime(alarm: SolarAlarm, solarTime: SolarTime): ZonedDateTime {
            var zdt = solarTime.getLocalZonedDateTime(alarm.SolarTimeTypeId)
            if (alarm.OffsetTypeId == OffsetTypeEnum.Before) {
                zdt = zdt.minusHours(alarm.OffsetHours.toLong()).minusMinutes(alarm.OffsetMinutes.toLong())
            } else if (alarm.OffsetTypeId == OffsetTypeEnum.After) {
                zdt = zdt.plusHours(alarm.OffsetHours.toLong()).plusMinutes(alarm.OffsetMinutes.toLong())
            }
            return zdt
        }

        /**
         * Determines the next valid target LocalDate on or after [now] when this alarm should ring.
         * - If today's calculated trigger time is still in the future AND today is active (or one-time), returns today.
         * - If today's trigger time has passed (or today is not active), returns the next active recurring date (or tomorrow for one-time).
         */
        fun findTargetDate(
            alarm: SolarAlarm,
            todaySolarTime: SolarTime?,
            now: ZonedDateTime = ZonedDateTime.now()
        ): LocalDate {
            val today = now.toLocalDate()

            fun isDayActive(date: LocalDate): Boolean {
                if (!alarm.Recurring) return false
                val dayOfWeek = date.dayOfWeek.value // 1 (Mon) .. 7 (Sun)
                return when (dayOfWeek) {
                    1 -> alarm.Monday
                    2 -> alarm.Tuesday
                    3 -> alarm.Wednesday
                    4 -> alarm.Thursday
                    5 -> alarm.Friday
                    6 -> alarm.Saturday
                    7 -> alarm.Sunday
                    else -> false
                }
            }

            if (alarm.Recurring) {
                // Check if today can be the target day
                if (isDayActive(today) && todaySolarTime != null) {
                    val todayTrigger = calculateTriggerDateTime(alarm, todaySolarTime)
                    if (todayTrigger.isAfter(now)) {
                        return today
                    }
                }

                // Search upcoming days starting from tomorrow up to 7 days ahead
                var checkDate = today.plusDays(1)
                for (i in 1..7) {
                    if (isDayActive(checkDate)) {
                        return checkDate
                    }
                    checkDate = checkDate.plusDays(1)
                }

                return today.plusDays(1)
            } else {
                // One-time alarm
                if (todaySolarTime != null) {
                    val todayTrigger = calculateTriggerDateTime(alarm, todaySolarTime)
                    if (todayTrigger.isAfter(now)) {
                        return today
                    }
                }
                return today.plusDays(1)
            }
        }

        /**
         * Resolves the appropriate SolarTime matching the next active target date.
         */
        suspend fun resolveTargetSolarTime(
            alarm: SolarAlarm,
            solarTimes: List<SolarTime>,
            fetchSolarTime: (suspend (LocalDate) -> SolarTime?)? = null,
            now: ZonedDateTime = ZonedDateTime.now()
        ): SolarTime {
            if (solarTimes.isEmpty()) {
                throw IllegalArgumentException("solarTimes list must not be empty")
            }

            if (DebugSettings.ringOneMinuteAfterCreation) {
                return solarTimes[0]
            }

            val todaySolarTime = solarTimes.find { it.SolarDate == now.toLocalDate() } ?: solarTimes[0]
            val targetDate = findTargetDate(alarm, todaySolarTime, now)

            // Look in the pre-fetched list first
            val matchedInList = solarTimes.find { it.SolarDate == targetDate }
            if (matchedInList != null) {
                return matchedInList
            }

            // If not found in the list, fetch via lambda if provided
            if (fetchSolarTime != null) {
                val fetched = fetchSolarTime(targetDate)
                if (fetched != null) {
                    return fetched
                }
            }

            // Fallback: if targetDate is tomorrow or later, pick the next available solar time if present
            return if (solarTimes.size > 1 && targetDate.isAfter(todaySolarTime.SolarDate)) solarTimes[1] else todaySolarTime
        }
    }
}
