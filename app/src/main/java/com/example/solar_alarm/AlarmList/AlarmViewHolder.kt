package com.example.solar_alarm.AlarmList

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
import android.annotation.TargetApi
import com.example.solar_alarm.AlarmList.OnToggleAlarmListener
import com.example.solar_alarm.AlarmList.AlarmRecycleViewAdapter
import com.example.solar_alarm.AlarmList.AlarmListViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Service.GpsTracker
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
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.Service.RescheduleAlarmService
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import android.app.AlarmManager
import android.app.PendingIntent
import com.example.solar_alarm.CreateAlarm.DayUtil
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import android.widget.AdapterView.OnItemSelectedListener
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.TimeResponseTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.GetSolarTimeTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.LocationIdDatePairExistsTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.SolarAlarmNameExistsTask
import com.example.solar_alarm.CreateAlarm.AlarmScheduler
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse
import com.example.solar_alarm.Data.AlarmRepository
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
import android.content.DialogInterface
import android.view.View
import android.widget.*
import androidx.lifecycle.LifecycleService
import com.example.solar_alarm.sunrise_sunset_http.Results
import java.lang.Exception

class AlarmViewHolder(itemView: View, listener: OnToggleAlarmListener) : RecyclerView.ViewHolder(itemView) {
    private val alarmTime: TextView
    private val alarmDate: TextView
    private val alarmRecurring: ImageView
    private val alarmRecurringDays: TextView
    private val alarmTitle: TextView
    var parent_layout: LinearLayout
    var alarmStarted: Switch
    private val listener: OnToggleAlarmListener

    init {
        alarmTime = itemView.findViewById(R.id.item_alarm_time)
        alarmDate = itemView.findViewById(R.id.item_alarm_date)
        alarmStarted = itemView.findViewById(R.id.item_alarm_started)
        alarmRecurring = itemView.findViewById(R.id.item_alarm_recurring)
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays)
        alarmTitle = itemView.findViewById(R.id.item_alarm_title)
        parent_layout = itemView.findViewById(R.id.parent_layout)
        this.listener = listener
    }

    inner class GetSolarTime : AsyncTask<Int?, Void?, SolarTime?>() {
        @TargetApi(Build.VERSION_CODES.O) //@Override
        protected override fun doInBackground(vararg p0: Int?): SolarTime? {
            var solarTime: SolarTime? = null
            try {
                solarTime = SolarTimeRepository().GetById(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return solarTime
        }
    }

    /*
    * SolarTime*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun bind(solarAlarm: SolarAlarm) {
        val solarTime = GetSolarTime().execute(solarAlarm.SolarTimeId).get()!!
        val zonedDateTime = solarAlarm.SolarTimeTypeId?.let { solarTime.GetLocalZonedDateTime(it) }
        zonedDateTime?.hour
        val localTime = zonedDateTime?.toLocalTime()
        val alarmText = zonedDateTime?.let { Converters.toTimeString(it) }
        alarmDate.text = alarmText!![0]
        alarmTime.text = alarmText[1]
        alarmStarted.isChecked = solarAlarm.Active
        if (solarAlarm.Recurring) {
            alarmRecurring.setImageResource(R.drawable.ic_repeat_black_24dp)
            alarmRecurringDays.text = solarAlarm.recurringDaysText
        } else {
            alarmRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp)
            alarmRecurringDays.text = "Once Off"
        }
        alarmTitle.text = String.format("%s | %d", solarAlarm.Name, solarAlarm.Id)

//        alarmStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//                listener.onToggle(solarAlarm);
//            }
//        });
    }
}