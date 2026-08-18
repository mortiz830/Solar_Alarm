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
import com.example.solar_alarm.createAlarm.CreateAlarmFragment
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.location.LocationCreateFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.service.GpsTracker
import com.example.solar_alarm.databinding.FragmentListalarmsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SolarAlarmListFragment : Fragment(), OnToggleAlarmListener
{
    private var _binding: FragmentListalarmsBinding? = null
    private val binding get() = _binding!!

    private val locationListViewModel: LocationListViewModel by activityViewModels()
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels()
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels()

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentListalarmsBinding.inflate(inflater, container, false)
        val solarAlarmListAdapter = SolarAlarmListAdapter(emptyList())
        val recyclerView = binding.fragmentListalarmsRecylerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = solarAlarmListAdapter

        solarAlarmViewModel.allSolarAlarmsWithDetails.observe(viewLifecycleOwner) { solarAlarms ->
            solarAlarmListAdapter.UpdateSolarAlarms(solarAlarms)
        }

        binding.addButton.setOnClickListener { showPopupMenu(it) }
        getLocation(binding.root)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getLocation(view: View)
    {
        val gpsTracker = GpsTracker(view.context)

        if (!gpsTracker.canGetLocation())
        {
            gpsTracker.showSettingsAlert()
        }
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

    private fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
