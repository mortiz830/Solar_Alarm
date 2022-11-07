package com.example.solar_alarm.Data

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.Data.Alarm
import android.content.Intent
import com.example.solar_alarm.Service.AlarmService
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.example.solar_alarm.AlarmList.OnToggleAlarmListener
import com.example.solar_alarm.AlarmList.AlarmRecycleViewAdapter
import com.example.solar_alarm.AlarmList.AlarmListViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Service.GpsTracker
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import com.example.solar_alarm.Data.Tables.SolarAlarm
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solar_alarm.AlarmList.ItemClickSupport
import com.example.solar_alarm.CreateAlarm.UpdateAlarmFragment
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.LocationRepository
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.AlarmDisplayDataDao
import com.example.solar_alarm.Data.AlarmDisplayData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.AlarmList.AlarmViewHolder
import android.widget.LinearLayout
import android.os.AsyncTask
import kotlin.Throws
import com.example.solar_alarm.AlarmList.AlarmViewHolder.GetSolarTime
import com.example.solar_alarm.Data.Converters
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import android.app.NotificationChannel
import com.example.solar_alarm.Application.App
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.widget.Toast
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.Service.RescheduleAlarmService
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import android.app.AlarmManager
import android.app.PendingIntent
import com.example.solar_alarm.CreateAlarm.DayUtil
import android.widget.EditText
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.NumberPicker
import android.widget.ArrayAdapter
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.AdapterView
import android.widget.CompoundButton
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.TimeResponseTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.GetSolarTimeTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.LocationIdDatePairExistsTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.SolarAlarmNameExistsTask
import com.example.solar_alarm.CreateAlarm.AlarmScheduler
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse
import com.example.solar_alarm.Data.AlarmRepository
import android.widget.TimePicker
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel
import androidx.lifecycle.ViewModelProvider
import com.example.solar_alarm.AlarmList.AlarmListFragment
import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Tables.SolarTimeType
import com.example.solar_alarm.Data.Tables.Timezone
import com.example.solar_alarm.Data.Repositories.RepositoryBase
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Repositories.LocationRepository.IsTimeUnitTypesExistsTask
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Daos.TimezoneDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solar_alarm.Data.Migrations.StaticDataMigration
import com.example.solar_alarm.Data.AlarmDao
import kotlin.jvm.Volatile
import com.example.solar_alarm.Data.AlarmDatabase
import androidx.lifecycle.LifecycleOwner
import com.example.solar_alarm.DisplayModels.TimezoneDisplayModel
import com.example.solar_alarm.Data.Repositories.TimezoneRepository
import com.example.solar_alarm.DisplayModels.LocationDisplayModel
import com.example.solar_alarm.DisplayModels.SolarTimeDisplayModel
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.example.solar_alarm.Location.TimeZoneResults
import com.google.android.gms.maps.SupportMapFragment
import com.example.solar_alarm.Location.AddLocationFragment.LocationNameExistsTask
import com.example.solar_alarm.Location.AddLocationFragment.LocationPointExistsTask
import com.google.gson.Gson
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.example.solar_alarm.Location.AddLocationFragment.TimeZoneTask
import android.media.MediaPlayer
import android.os.Vibrator
import com.example.solar_alarm.Activities.RingActivity
import androidx.core.app.NotificationCompat
import android.os.IBinder
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.room.*
import com.example.solar_alarm.sunrise_sunset_http.Results
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