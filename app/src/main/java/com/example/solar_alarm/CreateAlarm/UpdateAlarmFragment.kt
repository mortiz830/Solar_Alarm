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
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import com.example.solar_alarm.sunrise_sunset_http.Results

class UpdateAlarmFragment : Fragment() {
    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_timePicker)
    var timePicker: TimePicker? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_title)
    var title: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_scheduleAlarm)
    var scheduleAlarm: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_recurring)
    var recurring: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkMon)
    var mon: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkTue)
    var tue: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkWed)
    var wed: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkThu)
    var thu: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkFri)
    var fri: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkSat)
    var sat: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkSun)
    var sun: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_recurring_options)
    var recurringOptions: LinearLayout? = null
    private var updateAlarmViewModel: AlarmListViewModel? = null
    private val updatedAlarm: SolarAlarmDisplayModel? = null
    var location = 0
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        location = bundle!!.getInt("position")
        updateAlarmViewModel = ViewModelProvider(requireParentFragment()).get(AlarmListViewModel::class.java)
        //        updateAlarmViewModel.getAlarmDisplayLiveData().observe(this, new Observer<List<AlarmDisplayData>>() {
//            @Override
//            public void onChanged(List<SolarAlarmDisplayModel> alarms) {
//                if(alarms != null)
//                {
//                   updatedAlarm = new SolarAlarmDisplayModel(alarms.get(location));
//                   timePicker.setHour(updatedAlarm.getHour());
//                   timePicker.setMinute(updatedAlarm.getMinute());
//                   title.setText(updatedAlarm.getTitle());
//                   recurring.setChecked(updatedAlarm.isRecurring());
//                   mon.setChecked(updatedAlarm.isMonday());
//                   tue.setChecked(updatedAlarm.isTuesday());
//                   wed.setChecked(updatedAlarm.isWednesday());
//                   thu.setChecked(updatedAlarm.isThursday());
//                   fri.setChecked(updatedAlarm.isFriday());
//                   sat.setChecked(updatedAlarm.isSaturday());
//                   sun.setChecked(updatedAlarm.isSunday());
//                }
//            }
//        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_updatealarm, container, false)
        ButterKnife.bind(this, view)
        recurring!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                recurringOptions!!.visibility = View.VISIBLE
            } else {
                recurringOptions!!.visibility = View.GONE
            }
        }
        scheduleAlarm!!.setOnClickListener {
            updateAlarm()
            val alarmListFragment = AlarmListFragment()
            val manager = fragmentManager
            manager!!.beginTransaction().replace(R.id.activity_main_nav_host_fragment, alarmListFragment).commit()
        }
        return view
    }

    private fun updateAlarm() {
//        updatedAlarm.setHour(TimePickerUtil.getTimePickerHour(timePicker));
//        updatedAlarm.setMinute(TimePickerUtil.getTimePickerMinute(timePicker));
//        updatedAlarm.setTitle(title.getText().toString());
//        updatedAlarm.setCreated(System.currentTimeMillis());
//        updatedAlarm.setRecurring(recurring.isChecked());
//        updatedAlarm.setMonday(mon.isChecked());
//        updatedAlarm.setTuesday(tue.isChecked());
//        updatedAlarm.setWednesday(wed.isChecked());
//        updatedAlarm.setThursday(thu.isChecked());
//        updatedAlarm.setFriday(fri.isChecked());
//        updatedAlarm.setSaturday(sat.isChecked());
//        updatedAlarm.setSunday(sun.isChecked());
//
//        updateAlarmViewModel.update(updatedAlarm);
//
//        updatedAlarm.schedule(getContext());
    }
}