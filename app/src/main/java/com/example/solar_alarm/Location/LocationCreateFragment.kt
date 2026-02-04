package com.example.solar_alarm.Location

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.solar_alarm.Activities.NavActivity
import com.example.solar_alarm.AlarmList.SolarAlarmListFragment
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.ViewModels.LocationListViewModel
import com.example.solar_alarm.Data.ViewModels.SolarAlarmViewModel
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModel
import com.example.solar_alarm.R
import com.example.solar_alarm.Service.GpsTracker
import com.example.solar_alarm.databinding.FragmentAddLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class LocationCreateFragment constructor(locationListViewModel: LocationListViewModel, solarTimeViewModel: SolarTimeViewModel,
                                         solarAlarmViewModel: SolarAlarmViewModel): Fragment(), OnMapReadyCallback
{
    private var locationListViewModel : LocationListViewModel = locationListViewModel
    private val solarTimeViewModel: SolarTimeViewModel = solarTimeViewModel
    private val solarAlarmViewModel: SolarAlarmViewModel = solarAlarmViewModel
    private lateinit var binding: FragmentAddLocationBinding
    private var latLng: LatLng? = null

    private var httpUrlConnection: HttpURLConnection? = null

    //var isLocationLatitudeExists = false
    //var isLocationLongitudeExists = false

    //var x = (application as SolarAlarmApp).locationRepository

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddLocationBinding.inflate(layoutInflater)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = binding.root
        getCurrentLocation(view)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.fragment_add_location_map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)
        //ButterKnife.bind(this, view)
        binding.fragmentAddLocationLatitude.text  = latLng?.latitude.toString()
        binding.fragmentAddLocationLongitude.text = latLng?.longitude.toString()
        binding.fragmentAddLocationTimeZone.text  = TimeZone.getDefault().toZoneId().toString()

        binding.fragmentAddLocationAddLocationButton.setOnClickListener(View.OnClickListener
        {
            val locationName = binding.fragmentAddLocationLocationNameText.text.toString()
            if (locationName.isBlank()) {
                Toast.makeText(context, "Location name cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val newScale = 4
            val latitude = BigDecimal(binding.fragmentAddLocationLatitude.text.toString()).setScale(newScale, RoundingMode.HALF_UP).toDouble()
            val longitude = BigDecimal(binding.fragmentAddLocationLongitude.text.toString()).setScale(newScale, RoundingMode.HALF_UP).toDouble()

            lifecycleScope.launch {
                val nameExists = locationListViewModel.DoesLocationNameExists(locationName)
                val latLongExists = locationListViewModel.DoesLocationLatLongExists(latitude, longitude)

                if (nameExists) {
                    Toast.makeText(context, "Location Name Already Exists!", Toast.LENGTH_LONG).show()
                } else if (latLongExists) {
                    Toast.makeText(context, "Location Point Already Exists!", Toast.LENGTH_LONG).show()
                } else {
                    saveLocation(locationName, latitude, longitude)
                    (activity as NavActivity).replaceFragment(SolarAlarmListFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))
                }
            }
        })

//        addLocationButton!!.setOnClickListener { view ->
//            var locationName = binding.fragmentAddLocationLocationNameText.text.toString()
//            var isLocationNameExists = false
//            var isLocationPointExists = false
//
//            try {
//                isLocationNameExists = locationViewModel.DoesLocationNameExists(locationName)
//                isLocationPointExists = locationViewModel.DoesLocationLatLongExists(latLng?.latitude.toString(), latLng?.longitude.toString())
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            if (!isLocationNameExists!! && !isLocationPointExists) {
//                saveLocation()
//                Navigation.findNavController(view).navigate(R.id.action_addLocationFragment_to_alarmsListFragment)
//            } else if (isLocationNameExists!! && isLocationPointExists) {
//                Toast.makeText(context, "Location Name & Point Already Exists!", Toast.LENGTH_LONG).show()
//            } else if (isLocationNameExists!!) Toast.makeText(context, "Location Name Already Exists!", Toast.LENGTH_LONG).show() else if (isLocationPointExists) Toast.makeText(context, "Location Point Already Exists!", Toast.LENGTH_LONG).show()
//        }
        return view
    }


    private fun getCurrentLocation(view: View)
    {
        var gpsTracker = GpsTracker(view.context)

        if (gpsTracker.canGetLocation())
        {
            latLng = LatLng(gpsTracker.latitude, gpsTracker.longitude)
        }
        else
        {
            gpsTracker.showSettingsAlert()
        }
    }

    @Throws(IOException::class)
    fun getTimeZone(latitude: Double, longitude: Double)
    {
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
    fun saveLocation(name: String, latitude: Double, longitude: Double)
    {
        val location = Location(0, name, latitude, longitude)

        locationListViewModel.Insert(location)
        // val locationNew = locationListViewModel.getByName(location.Name)
        // if (locationNew != null) {
        //     Toast.makeText(context, "New Location ${locationNew.Id} ${locationNew.Name} Created", Toast.LENGTH_LONG).show()
        // }
    }

    inner class LocationNameExistsTask : AsyncTask<String?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun doInBackground(vararg p0: String?): Boolean? {
            var result = false
            try {
                var locationName = binding.fragmentAddLocationLocationNameText.text.toString()
                //result = locationViewModel.DoesLocationNameExists(locationName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    inner class LocationPointExistsTask : AsyncTask<Double?, Void?, Boolean>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun doInBackground(vararg p0: Double?): Boolean? {
            var isLocationLatitudeExists : Boolean = false
            var isLocationLongitudeExists : Boolean = false
            try {
                //isLocationLatitudeExists = locationRepository!!.isLocationLatitudeExists(latitude)
                //isLocationLongitudeExists = locationRepository!!.isLocationLongitudeExists(longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isLocationLatitudeExists && isLocationLongitudeExists
        }
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        val markerOptions = MarkerOptions().position(latLng!!).title("$latLng.latitude, $latLng.longitude")

        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng!!))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 11.0f))

        googleMap.setOnMapClickListener {latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title(latLng.latitude.toString() + ", " + latLng.longitude))
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f))

            binding.fragmentAddLocationLatitude.text  = latLng.latitude.toString()
            binding.fragmentAddLocationLongitude.text = latLng.longitude.toString()
        }
    }
}