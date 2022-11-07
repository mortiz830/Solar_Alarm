package com.example.solar_alarm.CreateAlarm

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
import androidx.room.Dao
import androidx.room.Update
import androidx.room.Delete
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
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solar_alarm.Data.Migrations.StaticDataMigration
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.solar_alarm.Data.AlarmDao
import kotlin.jvm.Volatile
import com.example.solar_alarm.Data.AlarmDatabase
import androidx.room.Room
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
import androidx.lifecycle.LifecycleService
import com.example.solar_alarm.sunrise_sunset_http.Results
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
            solarTime.GetLocalZonedDateTime(solarAlarm.SolarTimeTypeId)
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