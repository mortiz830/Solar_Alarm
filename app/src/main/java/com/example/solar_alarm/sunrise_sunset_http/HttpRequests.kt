package com.example.solar_alarm.sunrise_sunset_http

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
open class HttpRequests @Inject constructor(
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) : SolarRemoteDataSource
{
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    override suspend fun getSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
    {
        var sunriseSunsetResponse = SunriseSunsetResponse()

        try
        {
            sunriseSunsetResponse         = makeGetRequest(sunriseSunsetRequest.getFullUrl())
            sunriseSunsetResponse.request = sunriseSunsetRequest
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        return sunriseSunsetResponse
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    private suspend fun makeGetRequest(url: String): SunriseSunsetResponse
    {
        return withContext(Dispatchers.IO)
        {
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful)
                {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body?.string() ?: ""
                gson.fromJson(responseBody, SunriseSunsetResponse::class.java)
            }
        }
    }
}
