package com.example.solar_alarm.DisplayModels

import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import com.example.solar_alarm.Data.Tables.*

@RequiresApi(api = Build.VERSION_CODES.O)
class LocationDisplayModel(private val _Application: Application, private val _Location: Location?) {

    fun GetId(): Int {
        return _Location!!.Id
    }

    fun GetName(): String? {
        return _Location!!.Name
    }

    fun GetLatitude(): Double {
        return _Location!!.Latitude
    }

    fun GetLongitude(): Double {
        return _Location!!.Longitude
    }
}