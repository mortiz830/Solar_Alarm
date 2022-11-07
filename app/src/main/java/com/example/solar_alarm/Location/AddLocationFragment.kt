package com.example.solar_alarm.Location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.Data.Alarm
import android.content.Intent
import com.example.solar_alarm.Service.AlarmService
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.example.solar_alarm.AlarmList.OnToggleAlarmListener
import com.example.solar_alarm.AlarmList.AlarmRecycleViewAdapter
import com.example.solar_alarm.AlarmList.AlarmListViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Service.GpsTracker
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solar_alarm.AlarmList.ItemClickSupport
import com.example.solar_alarm.CreateAlarm.UpdateAlarmFragment
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.LocationRepository
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.AlarmDisplayDataDao
import com.example.solar_alarm.Data.AlarmDisplayData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.AlarmList.AlarmViewHolder
import android.os.AsyncTask
import kotlin.Throws
import com.example.solar_alarm.AlarmList.AlarmViewHolder.GetSolarTime
import com.example.solar_alarm.Data.Converters
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import android.app.NotificationChannel
import com.example.solar_alarm.Application.App
import android.app.NotificationManager
import android.content.BroadcastReceiver
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.Service.RescheduleAlarmService
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import android.app.AlarmManager
import android.app.PendingIntent
import com.example.solar_alarm.CreateAlarm.DayUtil
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import android.widget.AdapterView.OnItemSelectedListener
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.TimeResponseTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.GetSolarTimeTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.LocationIdDatePairExistsTask
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment.SolarAlarmNameExistsTask
import com.example.solar_alarm.CreateAlarm.AlarmScheduler
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse
import com.example.solar_alarm.Data.AlarmRepository
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel
import androidx.lifecycle.ViewModelProvider
import com.example.solar_alarm.AlarmList.AlarmListFragment
import androidx.room.Dao
import androidx.room.Update
import androidx.room.Delete
import com.example.solar_alarm.Data.Repositories.RepositoryBase
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Repositories.LocationRepository.IsTimeUnitTypesExistsTask
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Daos.TimezoneDao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solar_alarm.Data.Migrations.StaticDataMigration
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.solar_alarm.Data.AlarmDao
import kotlin.jvm.Volatile
import com.example.solar_alarm.Data.AlarmDatabase
import androidx.room.Room
import androidx.lifecycle.LifecycleOwner
import com.example.solar_alarm.DisplayModels.TimezoneDisplayModel
import com.example.solar_alarm.Data.Repositories.TimezoneRepository
import com.example.solar_alarm.DisplayModels.LocationDisplayModel
import com.example.solar_alarm.DisplayModels.SolarTimeDisplayModel
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.example.solar_alarm.Location.TimeZoneResults
import com.google.android.gms.maps.SupportMapFragment
import com.example.solar_alarm.Location.AddLocationFragment.LocationNameExistsTask
import com.example.solar_alarm.Location.AddLocationFragment.LocationPointExistsTask
import com.google.gson.Gson
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.example.solar_alarm.Location.AddLocationFragment.TimeZoneTask
import android.media.MediaPlayer
import android.os.Vibrator
import com.example.solar_alarm.Activities.RingActivity
import androidx.core.app.NotificationCompat
import android.os.IBinder
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.content.DialogInterface
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import androidx.navigation.Navigation
import com.example.solar_alarm.Data.Tables.*
import com.example.solar_alarm.sunrise_sunset_http.Results
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

class AddLocationFragment : Fragment(), OnMapReadyCallback {
    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_add_location_addLocationButton)
    var addLocationButton: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_add_location_Latitude)
    var latitudeText: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_add_location_Longitude)
    var longitudeText: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_add_location_TimeZone)
    var timeZoneText: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_add_location_LocationNameText)
    var locationNameText: EditText? = null
    var googleMap: GoogleMap? = null
    private var gpsTracker: GpsTracker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    var timeZoneID = 0
    private var httpUrlConnection: HttpURLConnection? = null
    var isLocationNameExists: Boolean? = null
    var isLocationLatitudeExists = false
    var isLocationLongitudeExists = false
    var isLocationPointExists = false
    var locationName: String? = null
    var timeZoneResults: TimeZoneResults? = null
    var locationRepository: LocationRepository? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationRepository = LocationRepository()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)
        getCurrentLocation(view)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_add_location_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        ButterKnife.bind(this, view)
        latitudeText!!.text = latitude.toString()
        longitudeText!!.text = longitude.toString()
        timeZoneText!!.text = TimeZone.getDefault().toZoneId().toString()
        addLocationButton!!.setOnClickListener { view ->
            locationName = locationNameText!!.text.toString()
            try {
                isLocationNameExists = LocationNameExistsTask().execute(locationName).get()
                isLocationPointExists = LocationPointExistsTask().execute(latitude, longitude).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (!isLocationNameExists!! && !isLocationPointExists) {
                saveLocation()
                Navigation.findNavController(view).navigate(R.id.action_addLocationFragment_to_alarmsListFragment)
            } else if (isLocationNameExists!! && isLocationPointExists) {
                Toast.makeText(context, "Location Name & Point Already Exists!", Toast.LENGTH_LONG).show()
            } else if (isLocationNameExists!!) Toast.makeText(context, "Location Name Already Exists!", Toast.LENGTH_LONG).show() else if (isLocationPointExists) Toast.makeText(context, "Location Point Already Exists!", Toast.LENGTH_LONG).show()
        }
        return view
    }

    fun getCurrentLocation(view: View) {
        gpsTracker = GpsTracker(view.context)
        if (gpsTracker!!.canGetLocation()) {
            latitude = gpsTracker!!.latitude
            longitude = gpsTracker!!.longitude
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

    @Throws(IOException::class)
    fun getTimeZone(latitude: Double, longitude: Double) {
        val timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
        val query = String.format("?key=%s&format=%s&by=position&lat=%s&lng=%s",
                URLEncoder.encode(resources.getString(R.string.time_zone_api_key), "UTF-8"),
                URLEncoder.encode(resources.getString(R.string.url_format), "UTF-8"),
                URLEncoder.encode(latitude.toString(), "UTF-8"),
                URLEncoder.encode(longitude.toString(), "UTF-8"),
                URLEncoder.encode(timeStamp, "UTF-8"))
        val url = URL("http://api.timezonedb.com/v2.1/get-time-zone$query")
        httpUrlConnection = url.openConnection() as HttpURLConnection
        httpUrlConnection!!.requestMethod = "GET"
        httpUrlConnection!!.doOutput = true
        httpUrlConnection!!.connectTimeout = 5000
        httpUrlConnection!!.readTimeout = 5000
        val bufferedReader = BufferedReader(InputStreamReader(httpUrlConnection!!.inputStream))
        var inputLine: String?
        val content = StringBuilder()
        while (bufferedReader.readLine().also { inputLine = it } != null) {
            content.append(inputLine)
        }
        val gson = Gson()
        timeZoneResults = gson.fromJson(content.toString(), TimeZoneResults::class.java)
        bufferedReader.close()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun saveLocation() {
        val locationName = locationNameText!!.text.toString()
        val location = Location()
        location.Name = locationName
        location.TimezoneId = timeZoneID
        location.Latitude = latitude
        location.Longitude = longitude
        locationRepository!!.Insert(location)
        Toast.makeText(context, "New Location Created", Toast.LENGTH_LONG).show()
    }

    fun saveTimeZone() {
        val timezone = Timezone()
        timezone.CountryCode = timeZoneResults!!.countryCode
        timezone.CountryName = timeZoneResults!!.countryName
        timezone.ZoneName = timeZoneResults!!.zoneName
        timezone.Abbreviation = timeZoneResults!!.abbreviation
        timezone.GmtOffset = timeZoneResults!!.gmtOffset
        timezone.Dst = timeZoneResults!!.dst
        timezone.ZoneStart = timeZoneResults!!.zoneStart
        timezone.ZoneEnd = timeZoneResults!!.zoneEnd
        timezone.NextAbbreviation = timeZoneResults!!.nextAbbreviation
        timezone.Timestamp = timeZoneResults!!.timestamp
        timezone.Id = timeZoneID
    }

    private inner class TimeZoneTask : AsyncTask<Void?, Void?, Void?>() {
        protected override fun doInBackground(vararg voids: Void): Void? {
            try {
                getTimeZone(latitude, longitude)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(unused: Void?) {
            super.onPostExecute(unused)
            timeZoneText.setText(timeZoneResults.getZoneName())
            saveTimeZone()
        }
    }

    private inner class LocationNameExistsTask : AsyncTask<String?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg strings: String): Boolean {
            var result = false
            try {
                result = locationRepository!!.isLocationNameExists(locationName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    private inner class LocationPointExistsTask : AsyncTask<Double?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        protected override fun doInBackground(vararg doubles: Double): Boolean {
            try {
                isLocationLatitudeExists = locationRepository!!.isLocationLatitudeExists(latitude)
                isLocationLongitudeExists = locationRepository!!.isLocationLongitudeExists(longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isLocationLatitudeExists && isLocationLongitudeExists
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        val current = LatLng(latitude, longitude)
        googleMap!!.addMarker(MarkerOptions().position(current).title("$latitude, $longitude"))
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(current))
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.0f))
        googleMap!!.setOnMapClickListener { latLng ->
            googleMap!!.clear()
            googleMap!!.addMarker(MarkerOptions().position(latLng).title(latLng.latitude.toString() + ", " + latLng.longitude))
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f))
            latitude = latLng.latitude
            longitude = latLng.longitude
            latitudeText!!.text = latitude.toString()
            longitudeText!!.text = longitude.toString()
            TimeZoneTask().execute()
        }
    }
}