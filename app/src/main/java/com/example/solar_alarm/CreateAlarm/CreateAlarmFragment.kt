package com.example.solar_alarm.CreateAlarm

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import com.example.solar_alarm.Activities.NavActivity
import com.example.solar_alarm.AlarmList.AlarmListFragment
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Tables.*
import com.example.solar_alarm.Data.ViewModels.*
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentCreatealarmBinding
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.system.*

@RequiresApi(Build.VERSION_CODES.O)
class CreateAlarmFragment constructor(locationViewModel: LocationViewModel): Fragment() {
    private lateinit var binding: FragmentCreatealarmBinding
    private var locationViewModel: LocationViewModel = locationViewModel

    private val solarAlarmViewModel: SolarAlarmViewModel by viewModels {
        SolarAlarmViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarAlarmRepository)
    }

//    private val locationViewModel: LocationViewModel by viewModels {
//        LocationViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).locationRepository)
//    }

    private val solarTimeViewModel: SolarTimeViewModel by viewModels {
        SolarTimeViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarTimeRepository)
    }

    //private var Locations: List<Location>? = null
    private var solarTimeRepository = SolarAlarmApp().solarTimeRepository
    private var locationRepository = SolarAlarmApp().locationRepository
    private lateinit var solarAlarmRepository: SolarAlarmRepository

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding   = FragmentCreatealarmBinding.inflate(layoutInflater)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        locationViewModel.All.observe(viewLifecycleOwner, Observer
        {
            locations ->

            val locationStrings: List<String> = locationViewModel.getLocationStrings(locations)
            val namesList: List<String> = locationStrings.map { stringToLocation(it).Name }
            binding.fragmentCreatealarmLocationSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, namesList)

        })

        binding.fragmentCreatealarmAlarmtimeSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, OffsetTypeEnum.values())
        binding.fragmentCreatealarmSettimeSpinner.adapter   = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, SolarTimeTypeEnum.values())

        val solarTimes : /*solarTimeViewModel.AllSolarTimes.value as*/ ArrayList<SolarTime> = arrayListOf()

        setPickers()
        binding.fragmentCreatealarmLocationSpinner.onItemSelectedListener = object : OnItemSelectedListener
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, locationPosition: Int, l: Long)
            {
                val locationString : String = adapterView.getItemAtPosition(locationPosition) as String
                var selectedLocation : Location? = Location(0,"",0.0,0.0,
                    OffsetDateTime.of(OffsetDateTime.now().toLocalDateTime(), ZoneOffset.UTC))
                lifecycleScope.launch {
                    var newSelectedLocation = locationViewModel.getByName(locationString)




                var date = LocalDate.now()
                for (i in 0..13)
                {
                    runBlocking {
                        try
                        {

                            val location : Location? = newSelectedLocation
                            val solarTime =
                                location?.let { solarTimeRepository.getSolarTime(location, date) }


                            if (solarTime != null)
                            {
                                solarTimes.add(solarTime)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Solar Time exists!", Toast.LENGTH_LONG).show()
                        }

                        date = date.plusDays(1)
                    }
                }}
                try
                {
                    binding.fragmentCreatealarmSunriseData.text   = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                    binding.fragmentCreatealarmSolarnoonData.text = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                    binding.fragmentCreatealarmSunsetData.text    = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
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
            val alarmTimeItem = binding.fragmentCreatealarmAlarmtimeSpinner.selectedItem as OffsetTypeEnum
            val solarTimeTypeItem = binding.fragmentCreatealarmSettimeSpinner.selectedItem as SolarTimeTypeEnum

            for (i in solarTimes.indices) {
                try {
                    this.scheduleAlarm(solarTimes[i], alarmTimeItem, solarTimeTypeItem)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            (activity as NavActivity).replaceFragment(AlarmListFragment())
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

    @Throws(Exception::class)
    fun getLocationIdDatePareExists(locationItem: Location?, date: LocalDate?): Boolean {
        return try {
            LocationIdDatePairExistsTask().execute(locationItem, date).get()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    suspend fun getSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean
    {
        return solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarm)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    private fun scheduleAlarm(solarTimeItem: SolarTime, alarmTypeId: OffsetTypeEnum, solarTimeTypeId: SolarTimeTypeEnum)
    {
//        val solarAlarmItem = SolarAlarm(0,
//                                        if (binding.fragmentCreatealarmTitle.text.toString() === "") "" else binding.fragmentCreatealarmTitle.text.toString(),
//                                  true,
//                                        solarTimeItem.LocationId,
//                                        solarTimeItem.Id,
//                                        binding.fragmentCreatealarmRecurring.isChecked,
//                                        binding.fragmentCreatealarmCheckMon.isChecked,
//                                        binding.fragmentCreatealarmCheckTue.isChecked,
//                                        binding.fragmentCreatealarmCheckWed.isChecked,
//                                        binding.fragmentCreatealarmCheckThu.isChecked,
//                                        binding.fragmentCreatealarmCheckFri.isChecked,
//                                        binding.fragmentCreatealarmCheckSat.isChecked,
//                                        binding.fragmentCreatealarmCheckSun.isChecked,
//                                        alarmTypeId,
//                                        solarTimeTypeId)

        val solarAlarmItem = SolarAlarm(true,
                                  "NAME TO BE ADDED HERE",
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
                                        solarTimeTypeId
        )

        val isSolarAlarmNameLocationIdPairExists : Deferred<Boolean>

        val time = measureTimeMillis  {
            isSolarAlarmNameLocationIdPairExists = GlobalScope.async { getSolarAlarmNameLocationIdPairExists(solarAlarmItem) }
        }

        if (!isSolarAlarmNameLocationIdPairExists.getCompleted())
        {
            solarAlarmViewModel.Insert(solarAlarmItem)
        }
        else
        {
            Toast.makeText(context, "Alarm already exists!", Toast.LENGTH_LONG).show()
        }
        val alarmScheduler = AlarmScheduler(solarAlarmItem, solarTimeItem, binding.fragmentCreatealarmSetHours.value, binding.fragmentCreatealarmSetHours.value)
        alarmScheduler.schedule(context)
    }

    inner class TimeResponseTask : AsyncTask<Any?, Void?, SolarTime?>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: Any?): SolarTime? {
            lateinit var solarTime: SolarTime
            try
            {
                val sunriseSunsetRequest  = p0[0] as SunriseSunsetRequest
                val location              = p0[1] as Location
                val httpRequests          = HttpRequests(sunriseSunsetRequest)

                runBlocking{
                    val sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest)

                    if (sunriseSunsetResponse != null)
                    {
                        solarTime = SolarTime(sunriseSunsetResponse.request!!.RequestDate,
                                              location.Id,
                                              sunriseSunsetResponse.dayLength!!,
                                              sunriseSunsetResponse.sunrise,
                                              sunriseSunsetResponse.sunset,
                                              sunriseSunsetResponse.solarNoon,
                                              sunriseSunsetResponse.civilTwilightBegin,
                                              sunriseSunsetResponse.civilTwilightEnd,
                                              sunriseSunsetResponse.nauticalTwilightBegin,
                                              sunriseSunsetResponse.nauticalTwilightEnd,
                                              sunriseSunsetResponse.astronomicalTwilightBegin,
                                              sunriseSunsetResponse.astronomicalTwilightEnd)
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                //Toast.makeText(getContext(), "Unable to get times!", Toast.LENGTH_LONG).show();
            }
            return solarTime
        }
    }

    inner class LocationIdDatePairExistsTask : AsyncTask<Any?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: Any?): Boolean? {
            val location = p0[0] as Location
            val localDate = p0[1] as LocalDate
            var result = false
            try {
                //result = solarTimeRepository!!.isLocationIDDatePairExists(location.Id, localDate)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Location / Date Pair exists!", Toast.LENGTH_LONG).show()
            }
            return result
        }
    }

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
    fun stringToLocation(locationString: String): Location {
        val values = locationString.split(",") // Split the string using comma as a delimiter

        // Assuming the order is: Id, Name, Latitude, Longitude, CreateDateTimeUtc
        return Location(
            Id = values[0].toInt(),
            Name = values[1],
            Latitude = values[2].toDouble(),
            Longitude = values[3].toDouble(),
        )
    }
    fun setPickers() {
        binding.fragmentCreatealarmSetHours.minValue = 0
        binding.fragmentCreatealarmSetHours.maxValue = 23
        binding.fragmentCreatealarmSetMins.minValue = 0
        binding.fragmentCreatealarmSetMins.maxValue = 59
    }
}