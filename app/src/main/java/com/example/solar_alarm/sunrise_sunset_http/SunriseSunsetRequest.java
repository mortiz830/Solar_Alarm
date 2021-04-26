package com.example.solar_alarm.sunrise_sunset_http;

public class SunriseSunsetRequest
{
    public float Latitude;
    public float Longitude;
    public String Date;
    public Boolean Format;

    public SunriseSunsetRequest(float latitude, float longitude, String date, Boolean format)
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

    public void setDate(String date)
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

    public String getDate()
    {
        return Date;
    }

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