package com.example.solar_alarm.Data.Tables;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity
(
    tableName = "SolarTime",
    indices = {@Index(value = {"Date", "LocationId"}, unique = true)}
)

public class SolarTime extends TableBase
{
    @NonNull
    public LocalDate Date;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    // UTC Times
    public LocalDateTime Sunrise;
    public LocalDateTime Sunset;
    public LocalDateTime SolarNoon;
    public int DayLength;
    public LocalDateTime CivilTwilightBegin;
    public LocalDateTime CivilTwilightEnd;
    public LocalDateTime NauticalTwilightBegin;
    public LocalDateTime NauticalTwilightEnd;
    public LocalDateTime AstronomicalTwilightBegin;
    public LocalDateTime AstronomicalTwilightEnd;

    public SolarTime (){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime (Location location, SunriseSunsetResponse sunriseSunsetResponse)
    {
        DateTimeFormatter _DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocationId                = location.Id;
        Date                      = LocalDateTime.ofInstant(sunriseSunsetResponse.request.Date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        DayLength                 = sunriseSunsetResponse.getDayLength();
        Sunrise                   = LocalDateTime.parse(sunriseSunsetResponse.getSunrise(),                   _DateTimeFormatter);
        Sunset                    = LocalDateTime.parse(sunriseSunsetResponse.getSunset(),                    _DateTimeFormatter);
        SolarNoon                 = LocalDateTime.parse(sunriseSunsetResponse.getSolarNoon(),                 _DateTimeFormatter);
        CivilTwilightBegin        = LocalDateTime.parse(sunriseSunsetResponse.getCivilTwilightBegin(),        _DateTimeFormatter);
        CivilTwilightEnd          = LocalDateTime.parse(sunriseSunsetResponse.getCivilTwilightEnd(),          _DateTimeFormatter);
        NauticalTwilightBegin     = LocalDateTime.parse(sunriseSunsetResponse.getNauticalTwilightBegin(),     _DateTimeFormatter);
        NauticalTwilightEnd       = LocalDateTime.parse(sunriseSunsetResponse.getNauticalTwilightEnd(),       _DateTimeFormatter);
        AstronomicalTwilightBegin = LocalDateTime.parse(sunriseSunsetResponse.getAstronomicalTwilightBegin(), _DateTimeFormatter);
        AstronomicalTwilightEnd   = LocalDateTime.parse(sunriseSunsetResponse.getAstronomicalTwilightEnd(),   _DateTimeFormatter);
    }
}
