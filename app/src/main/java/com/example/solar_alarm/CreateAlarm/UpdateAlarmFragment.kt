package com.example.solar_alarm.createAlarm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.solar_alarm.activities.NavActivity
import com.example.solar_alarm.alarmList.SolarAlarmListFragment
import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.databinding.FragmentUpdatealarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class UpdateAlarmFragment : Fragment() {

    private var _binding: FragmentUpdatealarmBinding? = null
    private val binding get() = _binding!!

    private val locationListViewModel: LocationListViewModel by activityViewModels()
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels()

    @Inject
    lateinit var solarTimeRepository: SolarTimeRepository

    private var solarAlarm: SolarAlarm? = null
    private var solarTimes: ArrayList<SolarTime> = arrayListOf()
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    companion object {
        private const val ARG_SOLAR_ALARM = "solarAlarm"

        fun newInstance(solarAlarm: SolarAlarm): UpdateAlarmFragment {
            val fragment = UpdateAlarmFragment()
            val args = Bundle().apply {
                putParcelable(ARG_SOLAR_ALARM, solarAlarm)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        solarAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_SOLAR_ALARM, SolarAlarm::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_SOLAR_ALARM)
        }
    }

    suspend fun Location.getSolarTimes(): ArrayList<SolarTime> {
        val list: ArrayList<SolarTime> = arrayListOf()
        var date = LocalDate.now()
        val thisLocation = this

        for (i in 1..7) {
            try {
                val st = solarTimeRepository.getSolarTime(thisLocation, date)
                if (st != null) {
                    list.add(st)
                }
                date = date.plusDays(1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatealarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarm = solarAlarm
        if (alarm == null) {
            Toast.makeText(requireContext(), "Alarm not found", Toast.LENGTH_SHORT).show()
            (activity as? NavActivity)?.replaceFragment(SolarAlarmListFragment())
            return
        }

        setPickers()

        // Populate initial values from existing alarm
        binding.fragmentUpdatealarmTitle.setText(alarm.Name)
        binding.fragmentUpdatealarmRecurring.isChecked = alarm.Recurring
        binding.fragmentUpdatealarmRecurringOptions.visibility =
            if (alarm.Recurring) View.VISIBLE else View.GONE

        binding.fragmentUpdatealarmCheckMon.isChecked = alarm.Monday
        binding.fragmentUpdatealarmCheckTue.isChecked = alarm.Tuesday
        binding.fragmentUpdatealarmCheckWed.isChecked = alarm.Wednesday
        binding.fragmentUpdatealarmCheckThu.isChecked = alarm.Thursday
        binding.fragmentUpdatealarmCheckFri.isChecked = alarm.Friday
        binding.fragmentUpdatealarmCheckSat.isChecked = alarm.Saturday
        binding.fragmentUpdatealarmCheckSun.isChecked = alarm.Sunday

        binding.fragmentUpdatealarmSetHours.value = alarm.OffsetHours
        binding.fragmentUpdatealarmSetMins.value = alarm.OffsetMinutes

        binding.fragmentUpdatealarmRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.fragmentUpdatealarmRecurringOptions.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        binding.fragmentUpdatealarmAlarmtimeSpinner.adapter = ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            OffsetTypeEnum.values()
        )
        binding.fragmentUpdatealarmSettimeSpinner.adapter = ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            SolarTimeTypeEnum.values()
        )

        // Select current offset type & solar time type
        val offsetPos = OffsetTypeEnum.values().indexOf(alarm.OffsetTypeId)
        if (offsetPos >= 0) {
            binding.fragmentUpdatealarmAlarmtimeSpinner.setSelection(offsetPos)
        }

        val solarTimeTypePos = SolarTimeTypeEnum.values().indexOf(alarm.SolarTimeTypeId)
        if (solarTimeTypePos >= 0) {
            binding.fragmentUpdatealarmSettimeSpinner.setSelection(solarTimeTypePos)
        }

        locationListViewModel.allLocations.observe(viewLifecycleOwner, Observer { locations ->
            val namesList = locations.map { it.Name }
            binding.fragmentUpdatealarmLocationSpinner.adapter = ArrayAdapter(
                requireActivity().baseContext,
                android.R.layout.simple_spinner_item,
                namesList
            )

            // Select current location
            val initialLocationIndex = locations.indexOfFirst { it.Id == alarm.LocationId }
            if (initialLocationIndex >= 0) {
                binding.fragmentUpdatealarmLocationSpinner.setSelection(initialLocationIndex)
            }
        })

        binding.fragmentUpdatealarmLocationSpinner.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    locationPosition: Int,
                    l: Long
                ) {
                    val newSelectedLocation =
                        locationListViewModel.allLocations.value?.getOrNull(locationPosition)
                    lifecycleScope.launch {
                        if (newSelectedLocation != null) {
                            solarTimes = newSelectedLocation.getSolarTimes()
                            if (solarTimes.isNotEmpty() && _binding != null) {
                                binding.fragmentUpdatealarmSunriseData.text =
                                    solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise)
                                        .format(dateTimeFormatter)
                                binding.fragmentUpdatealarmSolarnoonData.text =
                                    solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon)
                                        .format(dateTimeFormatter)
                                binding.fragmentUpdatealarmSunsetData.text =
                                    solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunset)
                                        .format(dateTimeFormatter)
                            }
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        binding.fragmentUpdatealarmAlarmtimeSpinner.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    position: Int,
                    l: Long
                ) {
                    val selectedItem = adapterView.getItemAtPosition(position).toString()
                    if (selectedItem == "Before" || selectedItem == "After") {
                        binding.fragmentUpdatealarmSetHours.visibility = View.VISIBLE
                        binding.fragmentUpdatealarmSetMins.visibility = View.VISIBLE
                    } else {
                        binding.fragmentUpdatealarmSetHours.visibility = View.GONE
                        binding.fragmentUpdatealarmSetMins.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        binding.fragmentUpdatealarmScheduleAlarm.setOnClickListener {
            updateAlarm(alarm)
        }
    }

    private fun updateAlarm(alarm: SolarAlarm) {
        val offsetTypeEnum =
            binding.fragmentUpdatealarmAlarmtimeSpinner.selectedItem as OffsetTypeEnum
        val solarTimeTypeItem =
            binding.fragmentUpdatealarmSettimeSpinner.selectedItem as SolarTimeTypeEnum

        val selectedLocationIndex =
            binding.fragmentUpdatealarmLocationSpinner.selectedItemPosition
        val selectedLocation =
            locationListViewModel.allLocations.value?.getOrNull(selectedLocationIndex)

        alarm.Name = binding.fragmentUpdatealarmTitle.text.toString()
        if (selectedLocation != null) {
            alarm.LocationId = selectedLocation.Id
        }
        if (solarTimes.isNotEmpty()) {
            alarm.SolarTimeId = solarTimes[0].Id
        }
        alarm.Recurring = binding.fragmentUpdatealarmRecurring.isChecked
        alarm.Monday = binding.fragmentUpdatealarmCheckMon.isChecked
        alarm.Tuesday = binding.fragmentUpdatealarmCheckTue.isChecked
        alarm.Wednesday = binding.fragmentUpdatealarmCheckWed.isChecked
        alarm.Thursday = binding.fragmentUpdatealarmCheckThu.isChecked
        alarm.Friday = binding.fragmentUpdatealarmCheckFri.isChecked
        alarm.Saturday = binding.fragmentUpdatealarmCheckSat.isChecked
        alarm.Sunday = binding.fragmentUpdatealarmCheckSun.isChecked
        alarm.OffsetTypeId = offsetTypeEnum
        alarm.SolarTimeTypeId = solarTimeTypeItem
        alarm.OffsetHours = binding.fragmentUpdatealarmSetHours.value
        alarm.OffsetMinutes = binding.fragmentUpdatealarmSetMins.value

        lifecycleScope.launch {
            try {
                solarAlarmViewModel.update(alarm)

                val currentContext = context
                if (currentContext != null && solarTimes.isNotEmpty()) {
                    AlarmScheduler(
                        alarm,
                        solarTimes[0],
                        alarm.OffsetHours,
                        alarm.OffsetMinutes
                    ).schedule(currentContext)
                }
                Toast.makeText(requireContext(), "Alarm updated", Toast.LENGTH_SHORT).show()
                (activity as? NavActivity)?.replaceFragment(SolarAlarmListFragment())
            } catch (exception: Exception) {
                if (exception is kotlinx.coroutines.CancellationException) throw exception
                exception.printStackTrace()
                if (isAdded) {
                    Toast.makeText(requireContext(), "Unable to update alarm.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun setPickers() {
        binding.fragmentUpdatealarmSetHours.minValue = 0
        binding.fragmentUpdatealarmSetHours.maxValue = 23
        binding.fragmentUpdatealarmSetMins.minValue = 0
        binding.fragmentUpdatealarmSetMins.maxValue = 59
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
