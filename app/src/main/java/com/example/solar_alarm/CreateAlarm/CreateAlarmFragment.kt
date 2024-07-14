package com.example.solar_alarm.CreateAlarm

import android.database.sqlite.SQLiteConstraintException
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
import androidx.test.core.app.ApplicationProvider
import com.example.solar_alarm.Activities.NavActivity
import com.example.solar_alarm.AlarmList.AlarmListFragment
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Tables.*
import com.example.solar_alarm.Data.ViewModels.*
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentCreatealarmBinding
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.*

@RequiresApi(Build.VERSION_CODES.O)
class CreateAlarmFragment constructor(locationViewModel: LocationViewModel): Fragment() {
    private lateinit var binding: FragmentCreatealarmBinding
    private var locationViewModel: LocationViewModel = locationViewModel

    private val solarAlarmViewModel: SolarAlarmViewModel by viewModels {
        SolarAlarmViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarAlarmRepository)
    }

    private val solarTimeViewModel: SolarTimeViewModel by viewModels {
        SolarTimeViewModelFactory((ApplicationProvider.getApplicationContext() as SolarAlarmApp).solarTimeRepository)
    }
    
    private var solarTimeRepository = SolarAlarmApp().solarTimeRepository
    private var locationRepository = SolarAlarmApp().locationRepository
    private var solarAlarmRepository = SolarAlarmApp().solarAlarmRepository

    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu\nhh:mm a")

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding   = FragmentCreatealarmBinding.inflate(layoutInflater)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        var locationStrings: List<String> = emptyList()
        locationViewModel.All.observe(viewLifecycleOwner, Observer
        {
            locations ->
            locationStrings = locationViewModel.getLocationStrings(locations)
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
                val locationString = locationStrings[locationPosition]
                val locationSplit = locationString.split(",").toTypedArray()
                var newSelectedLocation = locationViewModel.getById(locationSplit[0].toInt())
                var date = LocalDate.now()

                if (newSelectedLocation != null)
                    for (i in 0..13)
                    {
                        try
                        {
                            runBlocking {
                                var solarTime = solarTimeRepository.getSolarTime(newSelectedLocation, date)

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
                            Toast.makeText(context, "Solar Time exists!", Toast.LENGTH_LONG).show()
                            //throw e
                        }
                    }
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
                this.scheduleAlarm(solarTimes[0], offsetTypeEnum, solarTimeTypeItem)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
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
    fun getSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean
    {
        return solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarm)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
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

    inner class LocationIdDatePairExistsTask : AsyncTask<Any?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: Any?): Boolean? {
            val location = p0[0] as Location
            val localDate = p0[1] as LocalDate
            var result = false
            try {
                result = solarTimeRepository.doesLocationIdDatePairExists(location.Id, localDate)
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