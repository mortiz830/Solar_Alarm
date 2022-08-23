package com.example.solar_alarm.sunrise_sunset_http;

import java.time.LocalDate;

public class SunriseSunsetRequest
{
    public float     Latitude;
    public float     Longitude;
    public LocalDate RequestDate;

    public SunriseSunsetRequest(float latitude, float longitude, LocalDate requestDate)
    {
        this.Latitude    = latitude;
        this.Longitude   = longitude;
        this.RequestDate = requestDate;
    }
}
