package com.example.solar_alarm.Data

import android.content.Intent
import android.widget.Toast
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import android.app.AlarmManager
import android.app.PendingIntent
import com.example.solar_alarm.CreateAlarm.DayUtil
import android.content.Context
import android.util.Log
import androidx.room.*
import java.lang.Exception
import java.util.*

@Entity(tableName = "alarm_table")
class Alarm {
    @PrimaryKey
    var alarmId: Int
    var hour: Int
    var minute: Int
    var isStarted: Boolean
        private set
    var isRecurring: Boolean
    var isMonday: Boolean
    var isTuesday: Boolean
        set(tuesday) {
            isMonday = tuesday
        }
    var isWednesday: Boolean
    var isThursday: Boolean
    var isFriday: Boolean
    var isSaturday: Boolean
    var isSunday: Boolean
    var title: String
    var created: Long

    constructor(alarmId: Int, hour: Int, minute: Int, title: String, created: Long, started: Boolean, recurring: Boolean, monday: Boolean, tuesday: Boolean, wednesday: Boolean, thursday: Boolean, friday: Boolean, saturday: Boolean, sunday: Boolean) {
        this.alarmId = alarmId
        this.hour = hour
        this.minute = minute
        isStarted = started
        isRecurring = recurring
        isMonday = monday
        isTuesday = tuesday
        isWednesday = wednesday
        isThursday = thursday
        isFriday = friday
        isSaturday = saturday
        isSunday = sunday
        this.title = title
        this.created = created
    }

    constructor(alarm: Alarm) {
        alarmId = alarm.alarmId
        hour = alarm.hour
        minute = alarm.minute
        isStarted = alarm.isStarted
        isRecurring = alarm.isRecurring
        isMonday = alarm.isMonday
        isTuesday = alarm.isTuesday
        isWednesday = alarm.isWednesday
        isThursday = alarm.isThursday
        isFriday = alarm.isFriday
        isSaturday = alarm.isSaturday
        isSunday = alarm.isSunday
        title = alarm.title
        created = alarm.created
    }

    fun schedule(context: Context?) {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(AlarmBroadcastReceiver.Companion.RECURRING, isRecurring)
        intent.putExtra(AlarmBroadcastReceiver.Companion.MONDAY, isMonday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.TUESDAY, isTuesday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.WEDNESDAY, isWednesday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.THURSDAY, isThursday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.FRIDAY, isFriday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.SATURDAY, isSaturday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.SUNDAY, isSunday)
        intent.putExtra(AlarmBroadcastReceiver.Companion.TITLE, title)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }
        if (!isRecurring) {
            var toastText: String? = null
            try {
                toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", title, DayUtil.toDay(calendar[Calendar.DAY_OF_WEEK]), hour, minute, alarmId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmPendingIntent
            )
        } else {
            val toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", title, recurringDaysText, hour, minute, alarmId)
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            val RUN_DAILY = (24 * 60 * 60 * 1000).toLong()
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    RUN_DAILY,
                    alarmPendingIntent
            )
        }
        isStarted = true
    }

    fun cancelAlarm(context: Context?) {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        isStarted = false
        val toastText = String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId)
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        Log.i("cancel", toastText)
    }

    val recurringDaysText: String?
        get() {
            if (!isRecurring) {
                return null
            }
            var days = ""
            if (isMonday) {
                days += "Mo "
            }
            if (isTuesday) {
                days += "Tu "
            }
            if (isWednesday) {
                days += "We "
            }
            if (isThursday) {
                days += "Th "
            }
            if (isFriday) {
                days += "Fr "
            }
            if (isSaturday) {
                days += "Sa "
            }
            if (isSunday) {
                days += "Su "
            }
            return days
        }
}