package com.example.solar_alarm.sunrise_sunset_http

import android.os.Build
import androidx.annotation.RequiresApi
import java.net.URLEncoder
import java.time.LocalDate

class SunriseSunsetRequest(var Latitude: Float, var Longitude: Float, var RequestDate: LocalDate)
{
    @RequiresApi(Build.VERSION_CODES.O)
    fun getParamsString(): String
    {
        //https://api.sunrise-sunset.org/json?date=2021-5-10&lat=40.67441&lng=-73.43162&formatted=0
        return String.format("date=%s-%s-%s&lat=%s&lng=%s&formatted=0",
            URLEncoder.encode(RequestDate.year.toString(),        "UTF-8"),
            URLEncoder.encode(RequestDate.month.value.toString(), "UTF-8"),
            URLEncoder.encode(RequestDate.dayOfMonth.toString(),  "UTF-8"),
            URLEncoder.encode(Latitude.toString(),                "UTF-8"),
            URLEncoder.encode(Longitude.toString(),               "UTF-8"))
    }
}