package com.example.solar_alarm.AlarmList

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.Data.ViewModels.SolarAlarmViewModel
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModel
import com.example.solar_alarm.Location.LocationCreateFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.Service.GpsTracker
import com.example.solar_alarm.databinding.FragmentListalarmsBinding
import java.time.ZoneId
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmListFragment constructor(private var locationViewModel   : LocationViewModel,
                                         private val solarTimeViewModel  : SolarTimeViewModel,
                                         private val solarAlarmViewModel : SolarAlarmViewModel)
    : Fragment(), OnToggleAlarmListener
{
    private lateinit var fragmentListalarmsBinding: FragmentListalarmsBinding
    private lateinit var solarAlarmListAdapter: SolarAlarmListAdapter
    private lateinit var recyclerView: RecyclerView

    private var gpsTracker: GpsTracker? = null
    var latitude: TextView? = null
    var longitude: TextView? = null
    private var zoneId: ZoneId? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        fragmentListalarmsBinding = FragmentListalarmsBinding.inflate(layoutInflater, container, false)
        solarAlarmListAdapter     = SolarAlarmListAdapter(emptyList())
        recyclerView              = fragmentListalarmsBinding.fragmentListalarmsRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(solarAlarmListAdapter)

        solarAlarmViewModel.AllSolarAlarms.observe(viewLifecycleOwner, androidx.lifecycle.Observer { solarAlarms -> solarAlarmListAdapter.UpdateSolarAlarms(solarAlarms)})

        zoneId = TimeZone.getDefault().toZoneId()
        latitude = fragmentListalarmsBinding.fragmentListalarmsLatitude
        longitude = fragmentListalarmsBinding.fragmentListalarmsLongitude
        fragmentListalarmsBinding.addButton.setOnClickListener { showPopupMenu(it) }
        GetLocation(fragmentListalarmsBinding.root)

        return fragmentListalarmsBinding.getRoot()
    }

//    override fun onToggle(alarm: Alarm) {
//        if (alarm.isStarted) {
//            alarm.cancelAlarm(context)
//            //alarmsListViewModel.update(alarm);
//        } else {
//            alarm.schedule(context)
//            //alarmsListViewModel.update(alarm);
//        }
//    }

    fun GetLocation(view: View)
    {
        gpsTracker = GpsTracker(view.context)

        if (gpsTracker!!.canGetLocation())
        {
            val lat = gpsTracker!!.latitude
            val lon = gpsTracker!!.longitude
            latitude!!.text = lat.toString()
            longitude!!.text = lon.toString()
        }
        else
        {
            gpsTracker!!.showSettingsAlert()
        }
    }

    internal fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.fab_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_option_create_location -> {
                    replaceFragment(LocationCreateFragment(locationViewModel, solarTimeViewModel, solarAlarmViewModel))
                    //fab.hide()
                    true
                }
                R.id.action_option_create_alarm -> {
                    replaceFragment(CreateAlarmFragment(locationViewModel, solarTimeViewModel, solarAlarmViewModel))
                    //fab.hide()
                    true
                }
                else -> false
            }
        }

        popup.show()
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
