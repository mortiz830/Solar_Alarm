package com.example.solar_alarm.location

// Repair: Fixed broken package/import lines
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.solar_alarm.activities.NavActivity
import com.example.solar_alarm.alarmList.SolarAlarmListFragment
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.LocationViewModelFactory
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModelFactory
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModelFactory
import com.example.solar_alarm.R
import com.example.solar_alarm.service.GpsTracker
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentAddLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
class LocationCreateFragment : Fragment(), OnMapReadyCallback
{
    private val locationListViewModel: LocationListViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as SolarAlarmApp).locationRepository)
    }
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels {
        SolarTimeViewModelFactory((requireActivity().application as SolarAlarmApp).solarTimeRepository)
    }
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels {
        SolarAlarmViewModelFactory((requireActivity().application as SolarAlarmApp).solarAlarmRepository)
    }
    
    private lateinit var binding: FragmentAddLocationBinding
    private var latLng: LatLng? = null

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
                val nameExists = locationListViewModel.doesLocationNameExists(locationName)
                val latLongExists = locationListViewModel.doesLocationLatLongExists(latitude, longitude)

                if (!isAdded) return@launch

                if (nameExists) {
                    Toast.makeText(requireContext(), "Location Name Already Exists!", Toast.LENGTH_LONG).show()
                } else if (latLongExists) {
                    Toast.makeText(requireContext(), "Location Point Already Exists!", Toast.LENGTH_LONG).show()
                } else {
                    saveLocation(locationName, latitude, longitude)
                    (activity as? NavActivity)?.replaceFragment(SolarAlarmListFragment())
                }
            }
        })

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun saveLocation(name: String, latitude: Double, longitude: Double)
    {
        val location = Location(0, name, latitude, longitude)
        locationListViewModel.insert(location)
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
