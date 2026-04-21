package com.example.solar_alarm.CreateAlarm

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
import androidx.lifecycle.Observer
import com.example.solar_alarm.Activities.NavActivity
import com.example.solar_alarm.AlarmList.SolarAlarmListFragment
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.ViewModels.LocationListViewModel
import com.example.solar_alarm.Data.ViewModels.SolarAlarmViewModel
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModel
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentCreatealarmBinding
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CreateAlarmFragment constructor(locationListViewModel: LocationListViewModel, solarTimeViewModel: SolarTimeViewModel,
                                      solarAlarmViewModel: SolarAlarmViewModel): Fragment()
{
    private lateinit var binding: FragmentCreatealarmBinding
    private var locationListViewModel: LocationListViewModel = locationListViewModel
    private val solarTimeViewModel: SolarTimeViewModel = solarTimeViewModel
    private val solarAlarmViewModel: SolarAlarmViewModel = solarAlarmViewModel

    private var solarAlarmRepository = SolarAlarmApp().solarAlarmRepository

    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = FragmentCreatealarmBinding.inflate(layoutInflater)
    }

    fun Location.GetSolarTimes() : ArrayList<SolarTime>
    {
        val solarTimes : ArrayList<SolarTime> = arrayListOf()
        var date                              = LocalDate.now()
        val thisLocation = this

        for (i in 1..7)
        {
            try
            {
                runBlocking {
                    val solarTime = SolarAlarmApp().solarTimeRepository.getSolarTime(thisLocation, date)

                    if (solarTime != null)
                    {
                        solarTimes.add(solarTime)
                    }
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
        locationListViewModel.AllLocations.observe(viewLifecycleOwner, Observer
        {
            locations ->
            val namesList = locationListViewModel.AllLocations.value.orEmpty().map { it.Name }
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
                val newSelectedLocation = locationListViewModel.AllLocations.value.orEmpty()[locationPosition]
                solarTimes              = newSelectedLocation.GetSolarTimes()

                try
                {
                    binding.fragmentCreatealarmSunriseData.text   = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(dateTimeFormatter)
                    binding.fragmentCreatealarmSolarnoonData.text = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(dateTimeFormatter)
                    binding.fragmentCreatealarmSunsetData.text    = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(dateTimeFormatter)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    throw e
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

            try
            {
                this.ScheduleAlarm(solarTimes[0], offsetTypeEnum, solarTimeTypeItem)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            (activity as NavActivity).replaceFragment(SolarAlarmListFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))
        }

        return binding.root
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Throws(Exception::class)
//    fun getSolarTime(locationItem: Location, date: LocalDate): SolarTime
//    {
//        val isLocationIdDatePairExists = getLocationIdDatePareExists(locationItem, date)
//        val solarTime: SolarTime
//        if (!isLocationIdDatePairExists)
//        {
//            try
//            {
//                val sunriseSunsetRequest = SunriseSunsetRequest(locationItem.Latitude.toFloat(), locationItem.Longitude.toFloat(), date)
//                solarTime = TimeResponseTask().execute(sunriseSunsetRequest, locationItem).get()!!
//                //solarTimeRepository!!.Insert(solarTime)
//            }
//            catch (e: Exception)
//            {
//                e.printStackTrace()
//                throw e
//            }
//        }
//        else
//        {
//            solarTime = GetSolarTimeTask().execute(locationItem.Id, date).get()!!
//        }
//
//        return solarTime
//    }

//    @Throws(Exception::class)
//    fun getLocationIdDatePareExists(locationItem: Location?, date: LocalDate?): Boolean {
//        return try {
//            LocationIdDatePairExistsTask().execute(locationItem, date).get()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    @Throws(Exception::class)
//    fun getSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean
//    {
//        return solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarm)
//    }

    @Throws(Exception::class)
    private fun ScheduleAlarm(solarTimeItem: SolarTime, alarmTypeId: OffsetTypeEnum, solarTimeTypeId: SolarTimeTypeEnum)
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

        var success = true

        runBlocking  {
            try
            {
                solarAlarmRepository.Insert(solarAlarmItem)
            }
            catch (sqLiteConstraintException: SQLiteConstraintException)
            {
                Toast.makeText(getContext(), "Alarm named '${solarAlarmItem.Name}' with location ID ${solarTimeItem.LocationId} already exists\n ${sqLiteConstraintException.message}", Toast.LENGTH_LONG).show();
                success = false
            }
            catch (exception: Exception)
            {
                exception.printStackTrace()
                Toast.makeText(getContext(), "Unable to create alarm.", Toast.LENGTH_LONG).show();
                success = false
            }
        }

        if (success)
        {
            context?.let {
                AlarmScheduler(solarAlarmItem,
                    solarTimeItem,
                    binding.fragmentCreatealarmSetHours.value,
                    binding.fragmentCreatealarmSetHours.value).schedule(it)
            }
        }
    }

    public fun UpdateAlarmAfterDismiss()
    {

    }

//    inner class TimeResponseTask : AsyncTask<Any?, Void?, SolarTime?>() {
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        protected override fun doInBackground(vararg p0: Any?): SolarTime? {
//            lateinit var solarTime: SolarTime
//            try
//            {
//                val sunriseSunsetRequest  = p0[0] as SunriseSunsetRequest
//                val location              = p0[1] as Location
//                val httpRequests          = HttpRequests(sunriseSunsetRequest)
//
//                runBlocking{
//                    val sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest)
//
//                    if (sunriseSunsetResponse != null)
//                    {
//                        solarTime = SolarTime(sunriseSunsetResponse.request!!.RequestDate,
//                                              location.Id,
//                                              sunriseSunsetResponse.dayLength!!,
//                                              sunriseSunsetResponse.sunrise,
//                                              sunriseSunsetResponse.sunset,
//                                              sunriseSunsetResponse.solarNoon,
//                                              sunriseSunsetResponse.civilTwilightBegin,
//                                              sunriseSunsetResponse.civilTwilightEnd,
//                                              sunriseSunsetResponse.nauticalTwilightBegin,
//                                              sunriseSunsetResponse.nauticalTwilightEnd,
//                                              sunriseSunsetResponse.astronomicalTwilightBegin,
//                                              sunriseSunsetResponse.astronomicalTwilightEnd)
//                    }
//                }
//            }
//            catch (e: Exception)
//            {
//                e.printStackTrace()
//                //Toast.makeText(getContext(), "Unable to get times!", Toast.LENGTH_LONG).show();
//            }
//            return solarTime
//        }
//    }

//    inner class LocationIdDatePairExistsTask : AsyncTask<Any?, Void?, Boolean>() {
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        protected override fun doInBackground(vararg p0: Any?): Boolean? {
//            val location = p0[0] as Location
//            val localDate = p0[1] as LocalDate
//            var result = false
//            try {
//                result = solarTimeRepository.doesLocationIdDatePairExists(location.Id, localDate)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(context, "Location / Date Pair exists!", Toast.LENGTH_LONG).show()
//            }
//            return result
//        }
//    }

//    inner class GetSolarTimeTask : AsyncTask<Any?, Void?, SolarTime?>() {
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        protected override suspend fun doInBackground(vararg p0: Any?): SolarTime? {
//            val locationId = p0[0] as Int
//            val localDate = p0[1] as LocalDate
//            return solarTimeRepository!!.getSolarTime(locationId, localDate)
//        }
//    }

    /*
    inner class SolarAlarmNameExistsTask : AsyncTask<SolarAlarm?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: SolarAlarm): Boolean? {
            var result = false
            try {
                val solarAlarmItem = p0[0]
                result = solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarmItem.Name, solarAlarmItem.LocationId)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Solar Alarm already exists!", Toast.LENGTH_LONG).show()
            }
            return result
        }
    }
*/
//    fun stringToLocation(locationString: String): Location {
//        val values = locationString.split(",") // Split the string using comma as a delimiter
//
//        // Assuming the order is: Id, Name, Latitude, Longitude, CreateDateTimeUtc
//        return Location(
//            Id = values[0].toInt(),
//            Name = values[1],
//            Latitude = values[2].toDouble(),
//            Longitude = values[3].toDouble(),
//        )
//    }

    fun setPickers() {
        binding.fragmentCreatealarmSetHours.minValue = 0
        binding.fragmentCreatealarmSetHours.maxValue = 23
        binding.fragmentCreatealarmSetMins.minValue = 0
        binding.fragmentCreatealarmSetMins.maxValue = 59
    }
}
