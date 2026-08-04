package com.example.solar_alarm.alarmList

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.createAlarm.CreateAlarmFragment
import com.example.solar_alarm.data.viewmodels.*
import com.example.solar_alarm.location.LocationCreateFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.service.GpsTracker
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentListalarmsBinding
import java.time.ZoneId
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmListFragment : Fragment(), OnToggleAlarmListener
{
    private lateinit var fragmentListalarmsBinding: FragmentListalarmsBinding
    private lateinit var solarAlarmListAdapter: SolarAlarmListAdapter
    private lateinit var recyclerView: RecyclerView

    private val locationListViewModel: LocationListViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as SolarAlarmApp).locationRepository)
    }
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels {
        SolarTimeViewModelFactory((requireActivity().application as SolarAlarmApp).solarTimeRepository)
    }
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels {
        SolarAlarmViewModelFactory((requireActivity().application as SolarAlarmApp).solarAlarmRepository)
    }

    private var gpsTracker: GpsTracker? = null
    private var zoneId: ZoneId? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        fragmentListalarmsBinding = FragmentListalarmsBinding.inflate(layoutInflater, container, false)
        solarAlarmListAdapter     = SolarAlarmListAdapter(emptyList())
        recyclerView              = fragmentListalarmsBinding.fragmentListalarmsRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(solarAlarmListAdapter)

        solarAlarmViewModel.allSolarAlarmsWithDetails.observe(viewLifecycleOwner, androidx.lifecycle.Observer { solarAlarms -> solarAlarmListAdapter.UpdateSolarAlarms(solarAlarms)})

        zoneId = TimeZone.getDefault().toZoneId()
        fragmentListalarmsBinding.addButton.setOnClickListener { showPopupMenu(it) }
        getLocation(fragmentListalarmsBinding.root)

        return fragmentListalarmsBinding.getRoot()
    }

    fun getLocation(view: View)
    {
        gpsTracker = GpsTracker(view.context)

        if (!gpsTracker!!.canGetLocation())
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
                    replaceFragment(LocationCreateFragment())
                    true
                }
                R.id.action_option_create_alarm -> {
                    replaceFragment(CreateAlarmFragment())
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
