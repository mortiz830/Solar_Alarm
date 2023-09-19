package com.example.solar_alarm

import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import java.util.Calendar

class HttpTestCase
{
    @Test
    fun HttpGetTest()
    {
        val sunriseSunsetRequest = SunriseSunsetRequest(40.67441.toFloat(), (-73.43162).toFloat(), LocalDate.EPOCH)

        try
        {
            val httpRequests = HttpRequests(sunriseSunsetRequest)
            val response     = httpRequests.GetSolarData(sunriseSunsetRequest)

            Assert.assertNotNull(response)
            Assert.assertSame("OK", response!!.status)
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }
}
