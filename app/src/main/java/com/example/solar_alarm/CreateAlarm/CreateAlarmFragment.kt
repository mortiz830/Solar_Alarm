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
import com.example.solar_alarm.data.viewmodels.*
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentCreatealarmBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CreateAlarmFragment : Fragment()
{
    private lateinit var binding: FragmentCreatealarmBinding
    private val locationListViewModel: LocationListViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as SolarAlarmApp).locationRepository)
    }
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels {
        SolarTimeViewModelFactory((requireActivity().application as SolarAlarmApp).solarTimeRepository)
    }
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels {
        SolarAlarmViewModelFactory((requireActivity().application as SolarAlarmApp).solarAlarmRepository)
    }

    private val solarAlarmRepository by lazy { (requireActivity().application as SolarAlarmApp).solarAlarmRepository }

    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = FragmentCreatealarmBinding.inflate(layoutInflater)
    }

    suspend fun Location.getSolarTimes() : ArrayList<SolarTime>
    {
        val solarTimes : ArrayList<SolarTime> = arrayListOf()
        var date                              = LocalDate.now()
        val thisLocation = this
        val solarTimeRepository = (requireActivity().application as SolarAlarmApp).solarTimeRepository

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
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
                        if (solarTimes.isNotEmpty()) {
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

            (activity as NavActivity).replaceFragment(SolarAlarmListFragment())
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
                                        solarTimeTypeId)

        lifecycleScope.launch {
            try
            {
                solarAlarmRepository.insert(solarAlarmItem)
                
                context?.let {
                    AlarmScheduler(solarAlarmItem,
                        solarTimeItem,
                        binding.fragmentCreatealarmSetHours.value,
                        binding.fragmentCreatealarmSetMins.value).schedule(it)
                }
            }
            catch (sqLiteConstraintException: SQLiteConstraintException)
            {
                Toast.makeText(getContext(), "Alarm named '${solarAlarmItem.Name}' with location ID ${solarTimeItem.LocationId} already exists\n ${sqLiteConstraintException.message}", Toast.LENGTH_LONG).show();
            }
            catch (exception: Exception)
            {
                exception.printStackTrace()
                Toast.makeText(getContext(), "Unable to create alarm.", Toast.LENGTH_LONG).show();
            }
        }
    }

    fun setPickers() {
        binding.fragmentCreatealarmSetHours.minValue = 0
        binding.fragmentCreatealarmSetHours.maxValue = 23
        binding.fragmentCreatealarmSetMins.minValue = 0
        binding.fragmentCreatealarmSetMins.maxValue = 59
    }
}
