package com.example.solar_alarm.sunrise_sunset_http;

import java.util.Calendar;

public class SunriseSunsetRequest
{
    public float Latitude;
    public float Longitude;
    public Calendar Date;
    public Boolean Format;

    public SunriseSunsetRequest(float latitude, float longitude, Calendar date)
    {
        this.Latitude  = latitude;
        this.Longitude = longitude;
        this.Date      = date;
        this.Format    = false;   // hard coded because this will give us date and times in UTC
    }
}
