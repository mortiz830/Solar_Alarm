package com.example.solar_alarm;

import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;

import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

public class BaseTestCase {

    protected Calendar eventDate;

    @Test
    public void Foo()
    {
        SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest((float) 40.67441, (float) -73.43162, Calendar.getInstance(), true);

        try {
            HttpRequests httpRequests = new HttpRequests(sunriseSunsetRequest);
            httpRequests.GetSolarData(sunriseSunsetRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
