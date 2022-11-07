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
import androidx.navigation.Navigation
import com.example.solar_alarm.sunrise_sunset_http.Results
import java.time.ZoneId
import java.util.*

class AlarmListFragment : Fragment(), OnToggleAlarmListener {
    private var alarmRecyclerViewAdapter: AlarmRecycleViewAdapter? = null
    private var alarmsListViewModel: AlarmListViewModel? = null
    private var alarmsRecyclerView: RecyclerView? = null
    private var addAlarm: Button? = null
    private var addLocation: Button? = null
    private var gpsTracker: GpsTracker? = null
    var timeZone: TextView? = null
    var latitude: TextView? = null
    var longitude: TextView? = null
    var zoneId: ZoneId? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmRecyclerViewAdapter = AlarmRecycleViewAdapter(this)
        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmListViewModel::class.java)
        alarmsListViewModel.getSolarAlarmLiveData().observe(this) { alarmListViewModels ->
            if (alarmListViewModels != null) {
                alarmRecyclerViewAdapter!!.setAlarms(alarmListViewModels)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_listalarms, container, false)
        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView)
        alarmsRecyclerView.setLayoutManager(LinearLayoutManager(context))
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter)
        configureOnClickRecyclerView()
        zoneId = TimeZone.getDefault().toZoneId()
        timeZone = view.findViewById(R.id.fragment_listalarms_timezone)
        latitude = view.findViewById(R.id.fragment_listalarms_latitude)
        longitude = view.findViewById(R.id.fragment_listalarms_longitude)
        timeZone.setText(zoneId.toString())
        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm)
        addAlarm.setOnClickListener(View.OnClickListener { v -> Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment) })
        addLocation = view.findViewById(R.id.fragment_listAlarms_addLocation)
        addLocation.setOnClickListener(View.OnClickListener { v -> Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_addLocationFragment) })
        getLocation(view)
        return view
    }

    override fun onToggle(alarm: Alarm) {
        if (alarm.isStarted) {
            alarm.cancelAlarm(context)
            //alarmsListViewModel.update(alarm);
        } else {
            alarm.schedule(context)
            //alarmsListViewModel.update(alarm);
        }
    }

    fun getLocation(view: View) {
        gpsTracker = GpsTracker(view.context)
        if (gpsTracker!!.canGetLocation()) {
            val lat = gpsTracker!!.latitude
            val lon = gpsTracker!!.longitude
            latitude!!.text = lat.toString()
            longitude!!.text = lon.toString()
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

    private fun configureOnClickRecyclerView() {
        ItemClickSupport.Companion.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemClickListener(ItemClickSupport.OnItemClickListener { recyclerView, position, v ->
                    val alarm = alarmRecyclerViewAdapter!!.getAlarm(position)
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    val updateAlarmFragment = UpdateAlarmFragment()
                    updateAlarmFragment.arguments = bundle
                    val manager = fragmentManager
                    manager!!.beginTransaction().replace(R.id.activity_main_nav_host_fragment, updateAlarmFragment).commit()
                })
        ItemClickSupport.Companion.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemLongClickListener(ItemClickSupport.OnItemLongClickListener { recyclerView, position, v ->
                    val alarm = alarmRecyclerViewAdapter!!.getAlarm(position)
                    // 2 - Show result in a Toast
                    //Toast.makeText(getContext(), "You long clicked on user : "+alarm.getTitle(), Toast.LENGTH_SHORT).show();
                    //alarmsListViewModel.delete(alarmRecyclerViewAdapter.removeItem(position));
                    false
                })
    }
}