package com.example.solar_alarm

import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class HttpTestCase
{
    @Test
    fun HttpGetTest()
    {
        val sunriseSunsetRequest = SunriseSunsetRequest(40.67441.toFloat(), (-73.43162).toFloat(), LocalDate.EPOCH)

        try
        {
            val httpRequests          = HttpRequests()
            val sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest)

            Assert.assertNotNull(sunriseSunsetResponse)
            Assert.assertSame("OK", sunriseSunsetResponse!!.status)
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }
}
