package com.example.solar_alarm.sunrise_sunset_http;

public class SunriseSunsetResponse
{
    public Results results;
    public String status;
    public SunriseSunsetRequest request;

    public String getSunrise() {return results.getSunrise();}
    public String getSunset() {return results.getSunset();}
    public String getSolarNoon() {return results.getSolar_noon();}
    public String getCivilTwilightBegin() {return results.getCivil_twilight_begin();}
    public String getCivilTwilightEnd() {return results.getCivil_twilight_end();}
    public String getNauticalTwilightBegin() {return results.getNautical_twilight_begin();}
    public String getNauticalTwilightEnd() {return results.getNautical_twilight_end();}
    public String getAstronomicalTwilightBegin() {return results.getAstronomical_twilight_begin();}
    public String getAstronomicalTwilightEnd() {return results.getAstronomical_twilight_end();}
}
