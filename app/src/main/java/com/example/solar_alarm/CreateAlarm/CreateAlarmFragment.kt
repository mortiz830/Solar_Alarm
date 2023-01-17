package com.example.solar_alarm.CreateAlarm

import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.LocationRepository
import android.os.AsyncTask
import kotlin.Throws
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import android.widget.AdapterView.OnItemSelectedListener
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.solar_alarm.Data.Tables.*
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.ArrayList

import kotlinx.coroutines.*
import kotlin.system.*
import kotlin.text.Typography.tm

class CreateAlarmFragment : Fragment() {
    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_title)
    var title: EditText? = null

    @BindView(R.id.fragment_createalarm_scheduleAlarm)
    var scheduleAlarm: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_recurring)
    var recurring: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkMon)
    var mon: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkTue)
    var tue: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkWed)
    var wed: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkThu)
    var thu: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkFri)
    var fri: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkSat)
    var sat: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_checkSun)
    var sun: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_recurring_options)
    var recurringOptions: LinearLayout? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_location_spinner)
    var locationSpinner: Spinner? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_sunrise_data)
    var sunriseData: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_solarnoon_data)
    var solarNoonData: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_sunset_data)
    var sunsetData: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_set_hours)
    var setHours: NumberPicker? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_createalarm_set_mins)
    var setMins: NumberPicker? = null
    var locationSpinnerAdapter: SpinnerAdapter? = null
    var alarmTimeAdapter: ArrayAdapter<CharSequence>? = null
    var setTimeAdapter: ArrayAdapter<CharSequence>? = null
    private var Locations: List<Location>? = null
    private var solarTimeRepository: SolarTimeRepository? = null
    private lateinit var solarAlarmRepository: SolarAlarmRepository

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locations = ArrayList()
        //solarTimeRepository = SolarTimeRepository()
        //solarAlarmRepository = SolarAlarmRepository()
//        val locationRepository = LocationRepository()
//        locationRepository.all.observe(this) { locations ->
//            Locations = locations as List<Location>?
//            locationSpinnerAdapter = SpinnerAdapter(activity, Locations)
//            locationSpinner!!.adapter = locationSpinnerAdapter
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_createalarm, container, false)
        val alarmTimeSpinner = view.findViewById<View>(R.id.fragment_createalarm_alarmtime_spinner) as Spinner
        alarmTimeSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, OffsetTypeEnum.values())
        val setTimeSpinner = view.findViewById<View>(R.id.fragment_createalarm_settime_spinner) as Spinner
        setTimeSpinner.adapter = ArrayAdapter(requireActivity().baseContext, android.R.layout.simple_spinner_item, SolarTimeTypeEnum.values())
        val solarTimes: MutableList<SolarTime> = ArrayList()
        ButterKnife.bind(this, view)
        setPickers()
        locationSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, locationPosition: Int, l: Long) {
                val locationItem = adapterView.getItemAtPosition(locationPosition) as Location
                var date = LocalDate.now()
                for (i in 0..13) {
                    try {
                        if (solarTimes.size == 14) {
                            solarTimes[i] = getSolarTime(locationItem, date)
                        } else {
                            solarTimes.add(getSolarTime(locationItem, date))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Solar Time exists!", Toast.LENGTH_LONG).show()
                    }
                    date = date.plusDays(1)
                }
                try {
                    sunriseData!!.text = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                    solarNoonData!!.text = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                    sunsetData!!.text = solarTimes[0].GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        recurring!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                recurringOptions!!.visibility = View.VISIBLE
            } else {
                recurringOptions!!.visibility = View.GONE
            }
        }
        alarmTimeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                if (adapterView.getItemAtPosition(position).toString() == "Before" || adapterView.getItemAtPosition(position).toString() == "After") {
                    setHours!!.visibility = View.VISIBLE
                    setMins!!.visibility = View.VISIBLE
                } else {
                    setHours!!.visibility = View.GONE
                    setMins!!.visibility = View.GONE
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        scheduleAlarm!!.setOnClickListener { v ->
            val alarmTimeItem = alarmTimeSpinner.selectedItem as OffsetTypeEnum
            val solarTimeTypeItem = setTimeSpinner.selectedItem as SolarTimeTypeEnum
            for (i in solarTimes.indices) {
                try {
                    scheduleAlarm(solarTimes[i], alarmTimeItem, solarTimeTypeItem)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Navigation.findNavController(v).navigate(R.id.action_createAlarmFragment_to_alarmsListFragment)
        }
        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getSolarTime(locationItem: Location, date: LocalDate): SolarTime {
        val isLocationIdDatePairExists = getLocationIdDatePareExists(locationItem, date)
        val solarTime: SolarTime
        if (!isLocationIdDatePairExists) {
            try {
                val sunriseSunsetRequest = SunriseSunsetRequest(locationItem.Latitude.toFloat(), locationItem.Longitude.toFloat(), date)
                solarTime = TimeResponseTask().execute(sunriseSunsetRequest, locationItem).get()!!
                //solarTimeRepository!!.Insert(solarTime)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        } else {
            solarTime = GetSolarTimeTask().execute(locationItem.Id, date).get()!!
        }
        return solarTime
    }

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
    private fun scheduleAlarm(solarTimeItem: SolarTime, alarmTypeId: OffsetTypeEnum, solarTimeTypeId: SolarTimeTypeEnum) {
//        val solarAlarmItem = SolarAlarm()
//        solarAlarmItem.Name = if (title!!.text.toString() === "") "" else title!!.text.toString()
//        solarAlarmItem.Active = true
//        solarAlarmItem.LocationId = solarTimeItem.LocationId
//        solarAlarmItem.SolarTimeId = solarTimeItem.Id
//        solarAlarmItem.Recurring = recurring!!.isChecked
//        solarAlarmItem.Monday = mon!!.isChecked
//        solarAlarmItem.Tuesday = tue!!.isChecked
//        solarAlarmItem.Wednesday = wed!!.isChecked
//        solarAlarmItem.Thursday = thu!!.isChecked
//        solarAlarmItem.Friday = fri!!.isChecked
//        solarAlarmItem.Saturday = sat!!.isChecked
//        solarAlarmItem.Sunday = sun!!.isChecked
//        solarAlarmItem.OffsetTypeId = alarmTypeId
//        solarAlarmItem.SolarTimeTypeId = solarTimeTypeId
        val isSolarAlarmNameLocationIdPairExists : Deferred<Boolean>

        val time = measureTimeMillis  {
            //isSolarAlarmNameLocationIdPairExists = GlobalScope.async { getSolarAlarmNameLocationIdPairExists(solarAlarmItem) }
        }

//        if (!isSolarAlarmNameLocationIdPairExists.getCompleted()) {
//            solarAlarmRepository!!.Insert(solarAlarmItem)
//        } else {
//            Toast.makeText(context, "Alarm already exists!", Toast.LENGTH_LONG).show()
//        }
//        val alarmScheduler = AlarmScheduler(solarAlarmItem, solarTimeItem, setHours!!.value, setMins!!.value)
//        alarmScheduler.schedule(context)
    }

    inner class TimeResponseTask : AsyncTask<Any?, Void?, SolarTime?>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: Any?): SolarTime? {
            var solarTime: SolarTime? = null
            try {
                val sunriseSunsetRequest = p0[0] as SunriseSunsetRequest
                val location = p0[1] as Location
                val httpRequests = HttpRequests(sunriseSunsetRequest)
                val sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest)
                //solarTime = SolarTime(location, sunriseSunsetResponse)
            } catch (e: Exception) {
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

    inner class GetSolarTimeTask : AsyncTask<Any?, Void?, SolarTime?>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg p0: Any?): SolarTime? {
            val locationId = p0[0] as Int
            val localDate = p0[1] as LocalDate
            return solarTimeRepository!!.getSolarTime(locationId, localDate)
        }
    }

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
    fun setPickers() {
        setHours!!.minValue = 0
        setHours!!.maxValue = 23
        setMins!!.minValue = 0
        setMins!!.maxValue = 59
    }
}