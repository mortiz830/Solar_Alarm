package com.example.solar_alarm.sunrise_sunset_http

import android.accounts.NetworkErrorException
import androidx.annotation.RequiresApi
import android.os.Build
import kotlin.Throws
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpRequests @RequiresApi(api = Build.VERSION_CODES.O) constructor(sunriseSunsetRequest: SunriseSunsetRequest)
{
    private val httpUrlConnection: HttpURLConnection

    init
    {
        val query = getParamsString(sunriseSunsetRequest)
        val url   = URL("https://api.sunrise-sunset.org/json$query")

        httpUrlConnection = url.openConnection() as HttpURLConnection

        httpUrlConnection.requestMethod = "GET"
        httpUrlConnection.doInput = true
        httpUrlConnection.doOutput = true
        httpUrlConnection.connectTimeout = 5000
        httpUrlConnection.readTimeout = 5000
    }

    @Throws(IOException::class)
    fun GetSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
    {
        var sunriseSunsetResponse = SunriseSunsetResponse()

        GlobalScope.launch(Dispatchers.IO)
        {
            try
            {
                var inputLine: String?
                val jsonResult = StringBuilder()

                val bufferedReader = BufferedReader(InputStreamReader(httpUrlConnection.inputStream))

                while (bufferedReader.readLine().also { inputLine = it } != null)
                {
                    jsonResult.append(inputLine)
                }

                bufferedReader.close()

                val gson = Gson()
                sunriseSunsetResponse = gson.fromJson(jsonResult.toString(), SunriseSunsetResponse::class.java)

                sunriseSunsetResponse.request = sunriseSunsetRequest
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

        if (sunriseSunsetResponse.results == null)
        {
            throw NetworkErrorException("Failed to make request to https://api.sunrise-sunset.org")
        }

        return sunriseSunsetResponse
    }

    companion object
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Throws(UnsupportedEncodingException::class)
        fun getParamsString(sunriseSunsetRequest: SunriseSunsetRequest): String
        {
            //https://api.sunrise-sunset.org/json?date=2021-5-10&lat=40.67441&lng=-73.43162&formatted=0
            return String.format("?date=%s-%s-%s&lat=%s&lng=%s&formatted=0",
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.year.toString(),        "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.month.value.toString(), "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.RequestDate.dayOfMonth.toString(),  "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.Latitude.toString(),                "UTF-8"),
                    URLEncoder.encode(sunriseSunsetRequest.Longitude.toString(),               "UTF-8"))
        }
    }
}