package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class SolarTime extends TableBase
{
    @NonNull
    public LocalDate Date;

    public int LocationId;

    // Times
    public LocalTime Sunrise;
    public LocalTime Sunset;
    public LocalTime SolarNoon;
    public LocalTime DayLength;
    public LocalTime CivilTwilightBegin;
    public LocalTime CivilTwilightEnd;
    public LocalTime NauticalTwilightBegin;
    public LocalTime NauticalTwilightEnd;
    public LocalTime AstronomicalTwilightBegin;
    public LocalTime AstronomicalTwilightEnd;
}
