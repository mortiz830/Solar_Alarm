package com.example.solar_alarm.AlarmList

import android.os.Bundle
import com.example.solar_alarm.R
import com.example.solar_alarm.Data.Alarm
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Service.GpsTracker
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solar_alarm.CreateAlarm.UpdateAlarmFragment
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.ViewModels.*
import com.example.solar_alarm.SolarAlarmApp
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class AlarmListFragment : Fragment(), OnToggleAlarmListener {

    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).locationRepository)
    }

    private val solarTimeViewModel: SolarTimeViewModel by viewModels {
        SolarTimeViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarTimeRepository)
    }
    // https://github.com/android/compose-samples
    private val solarAlarmViewModel: SolarAlarmViewModel by viewModels {
        SolarAlarmViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarAlarmRepository)
    }

    private var alarmRecyclerViewAdapter: AlarmRecycleViewAdapter? = null
    private var alarmsListViewModel: AlarmListViewModel? = null
    private lateinit var alarmsRecyclerView: RecyclerView
    private lateinit var addAlarm: Button
    private lateinit var addLocation: Button
    private var gpsTracker: GpsTracker? = null
    var latitude: TextView? = null
    var longitude: TextView? = null
    var zoneId: ZoneId? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmRecyclerViewAdapter = AlarmRecycleViewAdapter(this)
        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmListViewModel::class.java)
        alarmsListViewModel!!.getSolarAlarmLiveData()?.observe(this) { alarmListViewModels ->
            if (alarmListViewModels != null) {
                alarmRecyclerViewAdapter!!.setAlarms(alarmListViewModels as MutableList<SolarAlarm>)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_listalarms, container, false)
        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView)
        alarmsRecyclerView.setLayoutManager(LinearLayoutManager(context))
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter)
        //configureOnClickRecyclerView()
        zoneId = TimeZone.getDefault().toZoneId()
        latitude = view.findViewById(R.id.fragment_listalarms_latitude)
        longitude = view.findViewById(R.id.fragment_listalarms_longitude)
        //addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm)
        addAlarm.setOnClickListener(View.OnClickListener { v -> Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment) })
        //addLocation = view.findViewById(R.id.fragment_listAlarms_addLocation)
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
/*
    private fun configureOnClickRecyclerView() {
        ItemClickSupport.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemClickListener(ItemClickSupport.OnItemClickListener { recyclerView, position, v ->
                    val alarm = alarmRecyclerViewAdapter!!.getAlarm(position)
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    val updateAlarmFragment = UpdateAlarmFragment()
                    updateAlarmFragment.arguments = bundle
                    val manager = fragmentManager
                    manager!!.beginTransaction().replace(R.id.activity_main_nav_host_fragment, updateAlarmFragment).commit()
                })
        ItemClickSupport.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemLongClickListener(ItemClickSupport.OnItemLongClickListener { recyclerView, position, v ->
                    val alarm = alarmRecyclerViewAdapter!!.getAlarm(position)
                    // 2 - Show result in a Toast
                    //Toast.makeText(getContext(), "You long clicked on user : "+alarm.getTitle(), Toast.LENGTH_SHORT).show();
                    //alarmsListViewModel.delete(alarmRecyclerViewAdapter.removeItem(position));
                    false
                })
    }
    */
}