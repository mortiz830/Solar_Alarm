package com.example.solar_alarm.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class GpsTracker(context: Context) : LocationListener {
    private val mContext = context.applicationContext
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var currentLocation: Location? = null
    var latitude = 0.0
    var longitude = 0.0

    private var locationManager: LocationManager? = mContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isLocationProviderEnabled(): Boolean {
        isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
        return isGPSEnabled || isNetworkEnabled
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            if (!hasLocationPermission()) {
                Log.w("GpsTracker", "Location permission is not granted.")
                canGetLocation = false
                return null
            }

            if (!isLocationProviderEnabled()) {
                Log.w("GpsTracker", "Neither GPS nor Network location provider is enabled.")
                canGetLocation = false
                return null
            }

            if (isNetworkEnabled) {
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                currentLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if (isGPSEnabled && currentLocation == null) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }

            currentLocation?.let {
                latitude = it.latitude
                longitude = it.longitude
                canGetLocation = true
            } ?: run {
                canGetLocation = false
            }
        } catch (e: Exception) {
            Log.e("GpsTracker", "Error getting location", e)
            canGetLocation = false
        }
        return currentLocation
    }

    fun stopUsingGPS() {
        try {
            locationManager?.removeUpdates(this)
        } catch (e: Exception) {
            Log.e("GpsTracker", "Error removing location updates", e)
        }
    }

    fun canGetLocation(): Boolean = canGetLocation

    fun showSettingsAlert(activityContext: Context) {
        AlertDialog.Builder(activityContext).apply {
            setTitle("GPS Settings")
            setMessage("GPS is not enabled on your device. Do you want to go to the settings menu to enable it?")
            setPositiveButton("Settings") { _, _ ->
                activityContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    override fun onLocationChanged(location: Location) {
        this.currentLocation = location
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.canGetLocation = true
    }
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {}

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    }
}
