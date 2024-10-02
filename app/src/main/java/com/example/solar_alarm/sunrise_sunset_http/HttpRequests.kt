package com.example.solar_alarm.sunrise_sunset_http

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HttpRequests @RequiresApi(api = Build.VERSION_CODES.O) constructor()
{
    private val client = OkHttpClient()
    private val gson   = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    suspend fun GetSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
    {
        var sunriseSunsetResponse = SunriseSunsetResponse()

        try
        {
            sunriseSunsetResponse         = makeGetRequest(sunriseSunsetRequest.getFullUrl())
            sunriseSunsetResponse.request = sunriseSunsetRequest
        }
        catch (e: Exception)
        {
            // Handle exceptions, e.g., network errors
            e.printStackTrace()
        }

        return sunriseSunsetResponse
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    private suspend fun makeGetRequest(url: String): SunriseSunsetResponse
    {
        val sunriseSunsetResponse = withContext(Dispatchers.IO)
        {
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful)
                {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body!!.string()

                try {
                    gson.fromJson(responseBody, SunriseSunsetResponse::class.java)
                } catch (e: Exception) {
                    TODO("Not yet implemented")
                }
            }
        }

        return sunriseSunsetResponse
    }

}