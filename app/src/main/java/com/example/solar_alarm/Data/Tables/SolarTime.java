package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
(
    tableName = "SolarTime",
    indices = {@Index(value = {"Date", "LocationId"}, unique = true)}
)
public class SolarTime extends TableBase
{
    @NonNull
    public LocalDateTime Date;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    // UTC Times
    public LocalDateTime Sunrise;
    public LocalDateTime Sunset;
    public LocalDateTime SolarNoon;
    public LocalDateTime DayLength;
    public LocalDateTime CivilTwilightBegin;
    public LocalDateTime CivilTwilightEnd;
    public LocalDateTime NauticalTwilightBegin;
    public LocalDateTime NauticalTwilightEnd;
    public LocalDateTime AstronomicalTwilightBegin;
    public LocalDateTime AstronomicalTwilightEnd;
}
