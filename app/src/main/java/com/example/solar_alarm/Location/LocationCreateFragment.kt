package com.example.solar_alarm.location

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.solar_alarm.activities.NavActivity
import com.example.solar_alarm.alarmList.SolarAlarmListFragment
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.R
import com.example.solar_alarm.service.GpsTracker
import com.example.solar_alarm.util.PermissionUtils
import com.example.solar_alarm.databinding.FragmentAddLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class LocationCreateFragment : Fragment(), OnMapReadyCallback
{
    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!

    private var gpsTracker: GpsTracker? = null

    private val locationListViewModel: LocationListViewModel by activityViewModels()
    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels()
    private val solarAlarmViewModel: SolarAlarmViewModel by activityViewModels()
    
    private var latLng: LatLng? = null
    private var googleMap: GoogleMap? = null

    private val requestLocationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineGranted || coarseGranted) {
                checkGpsAndInit()
            } else {
                if (isAdded) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("Location permission is needed to automatically detect your coordinates. You can still manually tap anywhere on the map to set your location.")
                        .setPositiveButton("OK", null)
                        .setNeutralButton("Settings") { _, _ ->
                            PermissionUtils.openAppSettings(requireContext())
                        }
                        .show()
                }
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        val view = binding.root
        
        startLocationTracking()
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.fragment_add_location_map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        
        binding.fragmentAddLocationLatitude.text  = (latLng?.latitude ?: 0.0).toString()
        binding.fragmentAddLocationLongitude.text = (latLng?.longitude ?: 0.0).toString()
        binding.fragmentAddLocationTimeZone.text  = TimeZone.getDefault().toZoneId().toString()

        binding.fragmentAddLocationAddLocationButton.setOnClickListener(View.OnClickListener
        {
            val locationName = binding.fragmentAddLocationLocationNameText.text.toString().trim()
            if (locationName.isBlank()) {
                Toast.makeText(context, "Location name cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val latStr = binding.fragmentAddLocationLatitude.text.toString()
            val lonStr = binding.fragmentAddLocationLongitude.text.toString()

            val newScale = 4
            val latitude = (latStr.toDoubleOrNull()?.let { BigDecimal(it) } ?: BigDecimal.ZERO)
                .setScale(newScale, RoundingMode.HALF_UP).toDouble()
            val longitude = (lonStr.toDoubleOrNull()?.let { BigDecimal(it) } ?: BigDecimal.ZERO)
                .setScale(newScale, RoundingMode.HALF_UP).toDouble()

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

    override fun onDestroyView() {
        super.onDestroyView()
        gpsTracker?.stopUsingGPS()
        gpsTracker = null
        googleMap = null
        _binding = null
    }

    private fun startLocationTracking()
    {
        val currentContext = context ?: return
        if (PermissionUtils.hasLocationPermission(currentContext)) {
            checkGpsAndInit()
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkGpsAndInit() {
        val currentContext = context ?: return
        if (!PermissionUtils.isLocationProviderEnabled(currentContext)) {
            if (isAdded) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Services Disabled")
                    .setMessage("GPS / Location is disabled on your device. Would you like to enable it in Settings to automatically detect your coordinates?")
                    .setPositiveButton("Settings") { _, _ ->
                        PermissionUtils.openLocationSettings(requireContext())
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(requireContext(), "You can tap on the map to choose your location.", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
            return
        }
        initGpsAndCenterMap()
    }

    private fun initGpsAndCenterMap() {
        val currentContext = context ?: return
        gpsTracker = GpsTracker(currentContext)
        val location = gpsTracker?.getLocation()

        if (gpsTracker?.canGetLocation() == true && location != null) {
            val curLatLng = LatLng(location.latitude, location.longitude)
            latLng = curLatLng
            if (_binding != null) {
                binding.fragmentAddLocationLatitude.text = curLatLng.latitude.toString()
                binding.fragmentAddLocationLongitude.text = curLatLng.longitude.toString()
            }
            googleMap?.let { map ->
                map.clear()
                map.addMarker(MarkerOptions().position(curLatLng).title("${curLatLng.latitude}, ${curLatLng.longitude}"))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 11.0f))
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun saveLocation(name: String, latitude: Double, longitude: Double)
    {
        val location = Location(0, name, latitude, longitude)
        locationListViewModel.insert(location)
    }

    override fun onMapReady(map: GoogleMap)
    {
        this.googleMap = map
        val position = latLng ?: LatLng(0.0, 0.0)
        val zoom = if (latLng != null) 11.0f else 2.0f

        map.clear()
        map.addMarker(MarkerOptions().position(position).title("${position.latitude}, ${position.longitude}"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom))

        map.setOnMapClickListener { clickedLatLng ->
            latLng = clickedLatLng
            map.clear()
            map.addMarker(MarkerOptions().position(clickedLatLng).title("${clickedLatLng.latitude}, ${clickedLatLng.longitude}"))
            map.animateCamera(CameraUpdateFactory.newLatLng(clickedLatLng))

            if (_binding != null) {
                binding.fragmentAddLocationLatitude.text  = clickedLatLng.latitude.toString()
                binding.fragmentAddLocationLongitude.text = clickedLatLng.longitude.toString()
            }
        }
    }
}
