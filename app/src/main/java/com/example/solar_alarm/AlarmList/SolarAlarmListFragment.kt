package com.example.solar_alarm.alarmList

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solar_alarm.createAlarm.AlarmScheduler
import com.example.solar_alarm.createAlarm.CreateAlarmFragment
import com.example.solar_alarm.createAlarm.UpdateAlarmFragment
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.location.LocationCreateFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.databinding.FragmentListalarmsBinding
import com.example.solar_alarm.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        val solarAlarmListAdapter = SolarAlarmListAdapter(
            emptyList(),
            onToggleListener = this,
            onItemClick = { solarAlarmWithDetails ->
                val updateAlarmFragment = UpdateAlarmFragment.newInstance(solarAlarmWithDetails.solarAlarm)
                replaceFragment(updateAlarmFragment)
            },
            onItemLongClick = { solarAlarmWithDetails ->
                showDeleteDialog(solarAlarmWithDetails)
            },
            onEditClick = { solarAlarmWithDetails ->
                val updateAlarmFragment = UpdateAlarmFragment.newInstance(solarAlarmWithDetails.solarAlarm)
                replaceFragment(updateAlarmFragment)
            },
            onDeleteClick = { solarAlarmWithDetails ->
                showDeleteDialog(solarAlarmWithDetails)
            }
        )
        val recyclerView = binding.fragmentListalarmsRecylerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = solarAlarmListAdapter

        solarAlarmViewModel.allSolarAlarmsWithDetails.observe(viewLifecycleOwner) { solarAlarms ->
            solarAlarmListAdapter.UpdateSolarAlarms(solarAlarms)
        }

        binding.addButton.setOnClickListener { showPopupMenu(it) }

        return binding.root
    }

    override fun onToggle(solarAlarmWithDetails: SolarAlarmWithDetails, isChecked: Boolean) {
        val solarAlarm = solarAlarmWithDetails.solarAlarm
        val solarTime = solarAlarmWithDetails.solarTime
        val location = solarAlarmWithDetails.location
        val currentContext = context ?: return

        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !PermissionUtils.hasExactAlarmPermission(currentContext)) {
                PermissionUtils.showExactAlarmDialog(requireContext())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    solarAlarm.Active = true
                    val targetSolarTime = AlarmScheduler.resolveTargetSolarTime(
                        solarAlarm,
                        listOf(solarTime),
                        fetchSolarTime = { date ->
                            solarTimeViewModel.getSolarTime(location, date).getOrNull()
                        }
                    )
                    solarAlarm.SolarTimeId = targetSolarTime.Id
                    solarAlarmViewModel.update(solarAlarm)

                    AlarmScheduler(
                        solarAlarm,
                        targetSolarTime,
                        solarAlarm.OffsetHours,
                        solarAlarm.OffsetMinutes
                    ).schedule(currentContext)
                } catch (e: Exception) {
                    if (e is kotlinx.coroutines.CancellationException) throw e
                    e.printStackTrace()
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to enable alarm", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            solarAlarm.Active = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    AlarmScheduler(
                        solarAlarm,
                        solarTime,
                        solarAlarm.OffsetHours,
                        solarAlarm.OffsetMinutes
                    ).cancel(currentContext)
                    AlarmScheduler.cancelSnooze(currentContext, solarAlarm.Id)

                    solarAlarmViewModel.update(solarAlarm)
                    if (isAdded) {
                        Toast.makeText(requireContext(), "${solarAlarm.Name} turned off", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    if (e is kotlinx.coroutines.CancellationException) throw e
                    e.printStackTrace()
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to disable alarm", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun showDeleteDialog(solarAlarmWithDetails: SolarAlarmWithDetails) {
        val solarAlarm = solarAlarmWithDetails.solarAlarm
        val solarTime = solarAlarmWithDetails.solarTime
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete '${solarAlarm.Name}'?")
            .setPositiveButton("Delete") { _, _ ->
                val currentContext = context
                if (currentContext != null) {
                    AlarmScheduler(
                        solarAlarm,
                        solarTime,
                        solarAlarm.OffsetHours,
                        solarAlarm.OffsetMinutes
                    ).cancel(currentContext)
                }
                solarAlarmViewModel.delete(solarAlarm)
                Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun replaceFragment(fragment: Fragment)
    {
        val navActivity = activity as? com.example.solar_alarm.activities.NavActivity
        if (navActivity != null)
        {
            navActivity.replaceFragment(fragment)
        }
        else
        {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
