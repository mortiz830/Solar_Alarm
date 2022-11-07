package com.example.solar_alarm.BroadcastReceiver

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
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val toastText = String.format("Alarm Reboot")
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            startRescheduleAlarmsService(context)
        } else {
            val toastText = String.format("Alarm Received")
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent)
            }
            run {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent)
                }
            }
        }
    }

    private fun alarmIsToday(intent: Intent): Boolean {
        var alarmIsToday = false
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_WEEK]
        when (today) {
            Calendar.MONDAY -> {
                if (intent.getBooleanExtra(MONDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(TUESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.TUESDAY -> {
                if (intent.getBooleanExtra(TUESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.WEDNESDAY -> {
                if (intent.getBooleanExtra(WEDNESDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.THURSDAY -> {
                if (intent.getBooleanExtra(THURSDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.FRIDAY -> {
                if (intent.getBooleanExtra(FRIDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.SATURDAY -> {
                if (intent.getBooleanExtra(SATURDAY, false)) alarmIsToday = true
                if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
            }
            Calendar.SUNDAY -> if (intent.getBooleanExtra(SUNDAY, false)) alarmIsToday = true
        }
        return alarmIsToday
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    companion object {
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val TITLE = "TITLE"
    }
}