package com.example.solar_alarm;

import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;

import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

public class BaseTestCase {

    protected Calendar eventDate;
    protected HttpRequests httpRequests;

    public BaseTestCase() throws IOException
    {
        httpRequests = new HttpRequests();
    }


    @Test
    public void Foo()
    {
        SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest();

        try {
            httpRequests.GetSolarData(sunriseSunsetRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
