package com.example.solar_alarm.service

import android.Manifest
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
import androidx.core.app.ActivityCompat

class GpsTracker(context: Context) : LocationListener {
    private val mContext = context.applicationContext
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var currentLocation: Location? = null
    var latitude = 0.0
    var longitude = 0.0

    private var locationManager: LocationManager? = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getLocation(): Location? {
        try {
            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true
                
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    
                    if (isNetworkEnabled) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        currentLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }

                    if (isGPSEnabled && currentLocation == null) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                }
                
                currentLocation?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }
        } catch (e: Exception) {
            Log.e("GpsTracker", "Error getting location", e)
        }
        return currentLocation
    }

    fun stopUsingGPS() {
        locationManager?.removeUpdates(this)
    }

    fun canGetLocation(): Boolean = canGetLocation

    fun showSettingsAlert() {
        AlertDialog.Builder(mContext).apply {
            setTitle("GPS settings")
            setMessage("GPS is not enabled. Do you want to go to settings menu?")
            setPositiveButton("Settings") { _, _ ->
                mContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    override fun onLocationChanged(location: Location) {
        this.currentLocation = location
        this.latitude = location.latitude
        this.longitude = location.longitude
    }
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {}

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    }
}
