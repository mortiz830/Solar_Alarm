package com.example.solar_alarm.sunrise_sunset_http;

import java.util.Calendar;

public class SunriseSunsetRequest
{
    public float Latitude;
    public float Longitude;
    public Calendar Date;
    public Boolean Format;

    private SunriseSunsetRequest()
    {
        Latitude = (float) 40.67441;
        Longitude = (float) -73.43162;
        Date = Calendar.getInstance();
        Format = true;
    }

    public SunriseSunsetRequest(float latitude, float longitude, Calendar date, Boolean format)
    {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Date = date;
        this.Format = format;
    }

    public void setLatitude(float latitude)
    {
        this.Latitude = latitude;
    }

    public void setLongitude(float longitude)
    {
        this.Longitude = longitude;
    }

    public void setDate(Calendar date)
    {
        this.Date = date;
    }

    public void setFormat(Boolean format)
    {
        this.Format = format;
    }

    public float getLatitude()
    {
        return Latitude;
    }

    public float getLongitude()
    {
        return Longitude;
    }

    public Calendar getDate() { return Date; }

    public Boolean getFormat()
    {
        return Format;
    }
}


/*
*
*
* SunriseSunsetRequest foo = new SunriseSunsetRequest()
*
* SunriseSunsetRequest.Date = "fhfhf";
*
* String x = SunriseSunsetRequest.Date
*
* */