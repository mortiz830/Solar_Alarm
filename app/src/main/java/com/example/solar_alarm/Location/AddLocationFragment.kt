package com.example.solar_alarm.Location

import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.Service.GpsTracker
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.solar_alarm.Data.Repositories.LocationRepository
import android.os.AsyncTask
import kotlin.Throws
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.solar_alarm.Data.Tables.*
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
    private var httpUrlConnection: HttpURLConnection? = null
    var isLocationNameExists: Boolean? = null
    var isLocationLatitudeExists = false
    var isLocationLongitudeExists = false
    var isLocationPointExists = false
    var locationName: String? = null
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
        bufferedReader.close()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun saveLocation() {
        val locationName = locationNameText!!.text.toString()
        val location = Location()
        location.Name = locationName
        location.Latitude = latitude
        location.Longitude = longitude
        locationRepository!!.Insert(location)
        Toast.makeText(context, "New Location Created", Toast.LENGTH_LONG).show()
    }

    inner class LocationNameExistsTask : AsyncTask<String?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun doInBackground(vararg p0: String?): Boolean? {
            var result = false
            try {
                result = locationRepository!!.isLocationNameExists(locationName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    inner class LocationPointExistsTask : AsyncTask<Double?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun doInBackground(vararg p0: Double?): Boolean? {
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
        }
    }
}