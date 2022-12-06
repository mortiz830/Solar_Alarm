package com.example.solar_alarm;

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
        SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest((float) 40.67441, (float) -73.43162, Calendar.getInstance());

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
