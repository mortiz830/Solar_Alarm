package com.example.solar_alarm.createAlarm

import android.R
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

    private lateinit var solarAlarm: SolarAlarm
    private var solarTimes: List<SolarTime> = emptyList()
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        solarAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("solarAlarm", SolarAlarm::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("solarAlarm")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdatealarmBinding.inflate(inflater, container, false)
        
        setupSpinners()
        setupPickers()
        populateFields()

        binding.fragmentUpdatealarmRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.fragmentUpdatealarmRecurringOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.fragmentUpdatealarmAlarmtimeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, l: Long) {
                val selected = adapterView.getItemAtPosition(position).toString()
                binding.fragmentUpdatealarmOffsetPickers.visibility = if (selected == "Before" || selected == "After") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.fragmentUpdatealarmLocationSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, l: Long) {
                val selectedLocation = locationListViewModel.allLocations.value?.getOrNull(position)
                selectedLocation?.let { loadSolarTimes(it) }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.fragmentUpdatealarmSaveAlarm.setOnClickListener { saveChanges() }

        return binding.root
    }

    private fun setupSpinners() {
        locationListViewModel.allLocations.observe(viewLifecycleOwner) { locations ->
            val names = locations.map { it.Name }
            binding.fragmentUpdatealarmLocationSpinner.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, names)
            
            // Set initial selection
            val index = locations.indexOfFirst { it.Id == solarAlarm.LocationId }
            if (index != -1) binding.fragmentUpdatealarmLocationSpinner.setSelection(index)
        }

        binding.fragmentUpdatealarmAlarmtimeSpinner.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, OffsetTypeEnum.entries.map { it.Name })
        binding.fragmentUpdatealarmSettimeSpinner.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, SolarTimeTypeEnum.entries.map { it.Name })
    }

    private fun setupPickers() {
        binding.fragmentUpdatealarmSetHours.minValue = 0
        binding.fragmentUpdatealarmSetHours.maxValue = 23
        binding.fragmentUpdatealarmSetMins.minValue = 0
        binding.fragmentUpdatealarmSetMins.maxValue = 59
    }

    private fun populateFields() {
        binding.fragmentUpdatealarmTitle.setText(solarAlarm.Name)
        binding.fragmentUpdatealarmRecurring.isChecked = solarAlarm.Recurring
        binding.fragmentUpdatealarmRecurringOptions.visibility = if (solarAlarm.Recurring) View.VISIBLE else View.GONE
        
        binding.fragmentUpdatealarmCheckMon.isChecked = solarAlarm.Monday
        binding.fragmentUpdatealarmCheckTue.isChecked = solarAlarm.Tuesday
        binding.fragmentUpdatealarmCheckWed.isChecked = solarAlarm.Wednesday
        binding.fragmentUpdatealarmCheckThu.isChecked = solarAlarm.Thursday
        binding.fragmentUpdatealarmCheckFri.isChecked = solarAlarm.Friday
        binding.fragmentUpdatealarmCheckSat.isChecked = solarAlarm.Saturday
        binding.fragmentUpdatealarmCheckSun.isChecked = solarAlarm.Sunday

        val offsetIndex = OffsetTypeEnum.entries.indexOfFirst { it == solarAlarm.OffsetTypeId }
        if (offsetIndex != -1) binding.fragmentUpdatealarmAlarmtimeSpinner.setSelection(offsetIndex)

        val timeTypeIndex = SolarTimeTypeEnum.entries.indexOfFirst { it == solarAlarm.SolarTimeTypeId }
        if (timeTypeIndex != -1) binding.fragmentUpdatealarmSettimeSpinner.setSelection(timeTypeIndex)

        binding.fragmentUpdatealarmSetHours.value = solarAlarm.OffsetHours
        binding.fragmentUpdatealarmSetMins.value = solarAlarm.OffsetMinutes
    }

    private fun loadSolarTimes(location: Location) {
        lifecycleScope.launch {
            val times = mutableListOf<SolarTime>()
            var date = LocalDate.now()
            for (i in 0..7) {
                solarTimeRepository.getSolarTime(location, date)?.let { times.add(it) }
                date = date.plusDays(1)
            }
            solarTimes = times
            updateSolarDataDisplay()
        }
    }

    private fun updateSolarDataDisplay() {
        if (solarTimes.isNotEmpty() && _binding != null) {
            binding.fragmentUpdatealarmSunriseData.text = "Sunrise: " + solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(dateTimeFormatter)
            binding.fragmentUpdatealarmSolarnoonData.text = "Solar Noon: " + solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(dateTimeFormatter)
            binding.fragmentUpdatealarmSunsetData.text = "Sunset: " + solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(dateTimeFormatter)
        }
    }

    private fun saveChanges() {
        val selectedLocation = locationListViewModel.allLocations.value?.getOrNull(binding.fragmentUpdatealarmLocationSpinner.selectedItemPosition)
        if (selectedLocation == null) {
            Toast.makeText(requireContext(), "Select a location", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAlarm = solarAlarm.copy(
            Name = binding.fragmentUpdatealarmTitle.text.toString(),
            LocationId = selectedLocation.Id,
            Recurring = binding.fragmentUpdatealarmRecurring.isChecked,
            Monday = binding.fragmentUpdatealarmCheckMon.isChecked,
            Tuesday = binding.fragmentUpdatealarmCheckTue.isChecked,
            Wednesday = binding.fragmentUpdatealarmCheckWed.isChecked,
            Thursday = binding.fragmentUpdatealarmCheckThu.isChecked,
            Friday = binding.fragmentUpdatealarmCheckFri.isChecked,
            Saturday = binding.fragmentUpdatealarmCheckSat.isChecked,
            Sunday = binding.fragmentUpdatealarmCheckSun.isChecked,
            OffsetTypeId = OffsetTypeEnum.entries[binding.fragmentUpdatealarmAlarmtimeSpinner.selectedItemPosition],
            SolarTimeTypeId = SolarTimeTypeEnum.entries[binding.fragmentUpdatealarmSettimeSpinner.selectedItemPosition],
            OffsetHours = binding.fragmentUpdatealarmSetHours.value,
            OffsetMinutes = binding.fragmentUpdatealarmSetMins.value
        ).apply { Id = solarAlarm.Id }

        lifecycleScope.launch {
            solarAlarmViewModel.update(updatedAlarm)
            
            // Reschedule if active
            if (updatedAlarm.Active && solarTimes.isNotEmpty()) {
                AlarmScheduler(updatedAlarm, solarTimes[0], updatedAlarm.OffsetHours, updatedAlarm.OffsetMinutes).schedule(requireContext())
            }
            
            (activity as? NavActivity)?.replaceFragment(SolarAlarmListFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
