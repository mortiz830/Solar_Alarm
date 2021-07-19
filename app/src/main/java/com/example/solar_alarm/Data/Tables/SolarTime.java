package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(tableName = "SolarTimes")
public class SolarTime extends TableBase
{
    @NonNull
    public LocalDate Date;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    // UTC Times
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
