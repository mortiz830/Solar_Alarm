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
import java.time.ZonedDateTime;
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
    public int DayLength;

    // UTC Time Stings in ISO_OFFSET_DATE_TIME format
    public String SunriseUtc;
    public String SunsetUtc;
    public String SolarNoonUtc;
    public String CivilTwilightBeginUtc;
    public String CivilTwilightEndUtc;
    public String NauticalTwilightBeginUtc;
    public String NauticalTwilightEndUtc;
    public String AstronomicalTwilightBeginUtc;
    public String AstronomicalTwilightEndUtc;

    // LocalDateTime Objects - these are created in the device's local timezone
    public LocalDateTime SunriseLocal;
    public LocalDateTime SunsetLocal;
    public LocalDateTime SolarNoonLocal;
    public LocalDateTime CivilTwilightBeginLocal;
    public LocalDateTime CivilTwilightEndLocal;
    public LocalDateTime NauticalTwilightBeginLocal;
    public LocalDateTime NauticalTwilightEndLocal;
    public LocalDateTime AstronomicalTwilightBeginLocal;
    public LocalDateTime AstronomicalTwilightEndLocal;

    public SolarTime (){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime (Location location, SunriseSunsetResponse sunriseSunsetResponse)
    {
        final DateTimeFormatter _DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        Date                           = LocalDateTime.ofInstant(sunriseSunsetResponse.request.Date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocationId                     = location.Id;
        DayLength                      = sunriseSunsetResponse.getDayLength();
        SunriseUtc                     = sunriseSunsetResponse.getSunrise();
        SunsetUtc                      = sunriseSunsetResponse.getSunset();
        SolarNoonUtc                   = sunriseSunsetResponse.getSolarNoon();
        CivilTwilightBeginUtc          = sunriseSunsetResponse.getCivilTwilightBegin();
        CivilTwilightEndUtc            = sunriseSunsetResponse.getCivilTwilightEnd();
        NauticalTwilightBeginUtc       = sunriseSunsetResponse.getNauticalTwilightBegin();
        NauticalTwilightEndUtc         = sunriseSunsetResponse.getNauticalTwilightEnd();
        AstronomicalTwilightBeginUtc   = sunriseSunsetResponse.getAstronomicalTwilightBegin();
        AstronomicalTwilightEndUtc     = sunriseSunsetResponse.getAstronomicalTwilightEnd();
        SunriseLocal                   = LocalDateTime.parse(SunriseUtc,                   _DateTimeFormatter);
        SunsetLocal                    = LocalDateTime.parse(SunsetUtc,                    _DateTimeFormatter);
        SolarNoonLocal                 = LocalDateTime.parse(SolarNoonUtc,                 _DateTimeFormatter);
        CivilTwilightBeginLocal        = LocalDateTime.parse(CivilTwilightBeginUtc,        _DateTimeFormatter);
        CivilTwilightEndLocal          = LocalDateTime.parse(CivilTwilightEndUtc,          _DateTimeFormatter);
        NauticalTwilightBeginLocal     = LocalDateTime.parse(NauticalTwilightBeginUtc,     _DateTimeFormatter);
        NauticalTwilightEndLocal       = LocalDateTime.parse(NauticalTwilightEndUtc,       _DateTimeFormatter);
        AstronomicalTwilightBeginLocal = LocalDateTime.parse(AstronomicalTwilightBeginUtc, _DateTimeFormatter);
        AstronomicalTwilightEndLocal   = LocalDateTime.parse(AstronomicalTwilightEndUtc,   _DateTimeFormatter);
    }
}
