package com.example.solar_alarm.sunrise_sunset_http

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpRequests @RequiresApi(api = Build.VERSION_CODES.O) constructor()
{
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun GetSolarData(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
    {
        var sunriseSunsetResponse = SunriseSunsetResponse()
        makeApiCall(sunriseSunsetRequest)

        return sunriseSunsetResponse
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun GetSolarDataOLD(sunriseSunsetRequest: SunriseSunsetRequest): SunriseSunsetResponse
    {
        var sunriseSunsetResponse = SunriseSunsetResponse()
        val query                 = sunriseSunsetRequest.getParamsString()
        val url                   = URL("https://api.sunrise-sunset.org/json$query")

        GlobalScope.launch(Dispatchers.IO) {
        //runBlocking {
            var httpUrlConnection = url.openConnection() as HttpURLConnection

            try
            {
                var httpUrlConnection = url.openConnection() as HttpURLConnection

                httpUrlConnection.requestMethod  = "GET"
                httpUrlConnection.doInput        = true
                httpUrlConnection.doOutput       = true
                httpUrlConnection.connectTimeout = 5000
                httpUrlConnection.readTimeout    = 5000

                var inputLine: String?
                val jsonResult     = StringBuilder()
                val bufferedReader = BufferedReader(InputStreamReader(httpUrlConnection.inputStream))

                while (bufferedReader.readLine().also { inputLine = it } != null)
                {
                    jsonResult.append(inputLine)
                }

                bufferedReader.close()

                //launch(Dispatchers.Main) {
                    val gson = Gson()

                    sunriseSunsetResponse = gson.fromJson(jsonResult.toString(), SunriseSunsetResponse::class.java)
                    sunriseSunsetResponse.request = sunriseSunsetRequest
                //}
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                throw e
            }
            finally
            {
                httpUrlConnection.disconnect()
            }
        }

        if (sunriseSunsetResponse.results == null)
        {
            //throw NetworkErrorException("Failed to make request to $url")
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



interface ApiService {
    @GET("endpoint")
    fun fetchData(@Query("parameter") parameterValue: String): Call<SunriseSunsetResponse>
}


object RetrofitClient {
    private const val BASE_URL = "https://api.sunrise-sunset.org/json/"

    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun makeApiCall(sunriseSunsetRequest: SunriseSunsetRequest)
{
    val apiService = RetrofitClient.apiService

    var i =0

    i++

    GlobalScope.launch(Dispatchers.IO) {
        val call: Call<SunriseSunsetResponse> = apiService.fetchData(sunriseSunsetRequest.getParamsString())

        call.enqueue(object : Callback<SunriseSunsetResponse>
        {
            override fun onResponse(call: Call<SunriseSunsetResponse>, response: Response<SunriseSunsetResponse>)
            {
                if (response.isSuccessful)
                {
                    val apiResponse = response.body()
                    // Handle the ApiResponse object as needed
                    println("Response: $apiResponse")
                }
                else
                {
                    // Handle unsuccessful response
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SunriseSunsetResponse>, t: Throwable)
            {
                // Handle failure
                println("Error: ${t.message}")
            }
        })
    }
}
