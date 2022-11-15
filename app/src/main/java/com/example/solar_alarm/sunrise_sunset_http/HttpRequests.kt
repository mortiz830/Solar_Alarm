package com.example.solar_alarm.sunrise_sunset_http

import androidx.annotation.RequiresApi
import android.os.Build
import kotlin.Throws
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpRequests @RequiresApi(api = Build.VERSION_CODES.O) constructor(sunriseSunsetRequest: SunriseSunsetRequest) {
    private val httpUrlConnection: HttpURLConnection

    init {
        val query = getParamsString(sunriseSunsetRequest)
        val url = URL("https://api.sunrise-sunset.org/json$query")
        httpUrlConnection = url.openConnection() as HttpURLConnection
        httpUrlConnection.requestMethod = "GET"
    }

    @Throws(IOException::class)
    fun GetSolarData(sunriseSunsetRequest: SunriseSunsetRequest?): SunriseSunsetResponse? {
        var sunriseSunsetResponse: SunriseSunsetResponse? = null
        try {
            httpUrlConnection.doInput = true
            httpUrlConnection.doOutput = true
            httpUrlConnection.connectTimeout = 5000
            httpUrlConnection.readTimeout = 5000
            val bufferedReader = BufferedReader(InputStreamReader(httpUrlConnection.inputStream))
            var inputLine: String?
            val content = StringBuilder()
            while (bufferedReader.readLine().also { inputLine = it } != null) {
                content.append(inputLine)
            }
            val gson = Gson()
            sunriseSunsetResponse = gson.fromJson(content.toString(), SunriseSunsetResponse::class.java)
            bufferedReader.close()
            sunriseSunsetResponse.request = sunriseSunsetRequest
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sunriseSunsetResponse
    }

    companion object {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Throws(UnsupportedEncodingException::class)
        fun getParamsString(sunriseSunsetRequest: SunriseSunsetRequest): String {
            //https://api.sunrise-sunset.org/json?date=2021-5-10&lat=40.67441&lng=-73.43162&formatted=0
            return String.format("?date=%s-%s-%s&lat=%s&lng=%s&formatted=0",
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.year.toString(), "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.month.value.toString(), "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.dayOfMonth.toString(), "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.Latitude.toString(), "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.Longitude.toString(), "UTF-8"))
        }
    }
}