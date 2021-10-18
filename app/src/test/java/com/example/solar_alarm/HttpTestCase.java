package com.example.solar_alarm;

import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

public class HttpTestCase
{
    protected Calendar eventDate;

    @Test
    public void HttpGetTest()
    {
        SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest((float) 40.67441, (float) -73.43162, Calendar.getInstance(), true);

        try
        {
            HttpRequests httpRequests = new HttpRequests(sunriseSunsetRequest);
            SunriseSunsetResponse response = httpRequests.GetSolarData(sunriseSunsetRequest);

            Assert.assertSame("OK", response.status);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
