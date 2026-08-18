package com.example.solar_alarm.createAlarm

import android.database.sqlite.SQLiteConstraintException
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
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.databinding.FragmentCreatealarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class CreateAlarmFragment : Fragment()
{
    private var _binding: FragmentCreatealarmBinding? = null
    private val binding get() = _binding!!

    private val locationListViewModel: LocationListViewModel by activityViewModels()
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels()
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels()

    @Inject
    lateinit var solarTimeRepository: SolarTimeRepository

    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    suspend fun Location.getSolarTimes() : ArrayList<SolarTime>
    {
        val solarTimes : ArrayList<SolarTime> = arrayListOf()
        var date                              = LocalDate.now()
        val thisLocation = this

        for (i in 1..7)
        {
            try
            {
                val solarTime = solarTimeRepository.getSolarTime(thisLocation, date)

                if (solarTime != null)
                {
                    solarTimes.add(solarTime)
                }

                date = date.plusDays(1)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                throw e
            }
        }

        return solarTimes
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatealarmBinding.inflate(inflater, container, false)
        
        locationListViewModel.allLocations.observe(viewLifecycleOwner, Observer
        {
            locations ->
            val namesList = locations.map { it.Name }
            binding.fragmentCreatealarmLocationSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, namesList )
        })

        binding.fragmentCreatealarmAlarmtimeSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, OffsetTypeEnum.values())
        binding.fragmentCreatealarmSettimeSpinner.adapter   = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, SolarTimeTypeEnum.values())

        var solarTimes : ArrayList<SolarTime> = arrayListOf()

        setPickers()
        binding.fragmentCreatealarmLocationSpinner.onItemSelectedListener = object : OnItemSelectedListener
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, locationPosition: Int, l: Long)
            {
                val newSelectedLocation = locationListViewModel.allLocations.value?.getOrNull(locationPosition)
                
                lifecycleScope.launch {
                    if (newSelectedLocation != null) {
                        solarTimes = newSelectedLocation.getSolarTimes()
                        if (solarTimes.isNotEmpty() && _binding != null) {
                            binding.fragmentCreatealarmSunriseData.text   = solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(dateTimeFormatter)
                            binding.fragmentCreatealarmSolarnoonData.text = solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(dateTimeFormatter)
                            binding.fragmentCreatealarmSunsetData.text    = solarTimes[0].getLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(dateTimeFormatter)
                        }
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.fragmentCreatealarmRecurring.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                binding.fragmentCreatealarmRecurringOptions.visibility = View.VISIBLE
            }
            else
            {

                binding.fragmentCreatealarmRecurringOptions.visibility = View.GONE
            }
        }

        binding.fragmentCreatealarmAlarmtimeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                if (adapterView.getItemAtPosition(position).toString() == "Before" || adapterView.getItemAtPosition(position).toString() == "After") {
                    binding.fragmentCreatealarmSetHours.visibility = View.VISIBLE
                    binding.fragmentCreatealarmSetMins.visibility = View.VISIBLE
                } else {
                    binding.fragmentCreatealarmSetHours.visibility = View.GONE
                    binding.fragmentCreatealarmSetMins.visibility = View.GONE
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.fragmentCreatealarmScheduleAlarm.setOnClickListener { v ->
            val offsetTypeEnum    = binding.fragmentCreatealarmAlarmtimeSpinner.selectedItem as OffsetTypeEnum
            val solarTimeTypeItem = binding.fragmentCreatealarmSettimeSpinner.selectedItem   as SolarTimeTypeEnum

            if (solarTimes.isNotEmpty()) {
                scheduleAlarm(solarTimes[0], offsetTypeEnum, solarTimeTypeItem)
            }
        }

        return binding.root
    }

    private fun scheduleAlarm(solarTimeItem: SolarTime, alarmTypeId: OffsetTypeEnum, solarTimeTypeId: SolarTimeTypeEnum)
    {
        val solarAlarmItem = SolarAlarm(true,
                                        binding.fragmentCreatealarmTitle.text.toString(),
                                        solarTimeItem.LocationId,
                                        solarTimeItem.Id,
                                        binding.fragmentCreatealarmRecurring.isChecked,
                                        binding.fragmentCreatealarmCheckMon.isChecked,
                                        binding.fragmentCreatealarmCheckTue.isChecked,
                                        binding.fragmentCreatealarmCheckWed.isChecked,
                                        binding.fragmentCreatealarmCheckThu.isChecked,
                                        binding.fragmentCreatealarmCheckFri.isChecked,
                                        binding.fragmentCreatealarmCheckSat.isChecked,
                                        binding.fragmentCreatealarmCheckSun.isChecked,
                                        alarmTypeId,
                                        solarTimeTypeId,
                                        binding.fragmentCreatealarmSetHours.value,
                                        binding.fragmentCreatealarmSetMins.value)

        lifecycleScope.launch {
            try
            {
                solarAlarmViewModel.insert(solarAlarmItem)
                
                val currentContext = context
                if (currentContext != null) {
                    AlarmScheduler(solarAlarmItem,
                        solarTimeItem,
                        solarAlarmItem.OffsetHours,
                        solarAlarmItem.OffsetMinutes).schedule(currentContext)
                    
                    (activity as? NavActivity)?.replaceFragment(SolarAlarmListFragment())
                }
            }
            catch (sqLiteConstraintException: SQLiteConstraintException)
            {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Alarm already exists", Toast.LENGTH_LONG).show()
                }
            }
            catch (exception: Exception)
            {
                if (exception is kotlinx.coroutines.CancellationException) throw exception
                exception.printStackTrace()
                if (isAdded) {
                    Toast.makeText(requireContext(), "Unable to create alarm.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setPickers() {
        binding.fragmentCreatealarmSetHours.minValue = 0
        binding.fragmentCreatealarmSetHours.maxValue = 23
        binding.fragmentCreatealarmSetMins.minValue = 0
        binding.fragmentCreatealarmSetMins.maxValue = 59
    }
}
