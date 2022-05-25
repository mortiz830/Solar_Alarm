package com.example.solar_alarm.CreateAlarm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class TimeZoneConverter {
    private SolarTime solarTime;

    public TimeZoneConverter(SolarTime solarTime)
    {
        this.solarTime = solarTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime convertSolarTime()
    {
        solarTime.Sunrise                   = convertToUTC(solarTime.Sunrise);
        solarTime.SolarNoon                 = convertToUTC(solarTime.SolarNoon);
        solarTime.Sunset                    = convertToUTC(solarTime.Sunset);
        solarTime.CivilTwilightBegin        = convertToUTC(solarTime.CivilTwilightBegin);
        solarTime.CivilTwilightEnd          = convertToUTC(solarTime.CivilTwilightEnd);
        solarTime.NauticalTwilightBegin     = convertToUTC(solarTime.NauticalTwilightBegin);
        solarTime.NauticalTwilightEnd       = convertToUTC(solarTime.NauticalTwilightEnd);
        solarTime.AstronomicalTwilightBegin = convertToUTC(solarTime.AstronomicalTwilightBegin);
        solarTime.AstronomicalTwilightEnd   = convertToUTC(solarTime.AstronomicalTwilightEnd);

        return solarTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime convertToUTC(LocalDateTime localDateTime)
    {
        long offset = TimeZone.getDefault().getRawOffset();

        localDateTime = localDateTime.plus(offset, ChronoUnit.MILLIS);

        return localDateTime;
    }
}
