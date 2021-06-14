package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "SolarAlarms")
public class SolarAlarm extends TableBase
{
    @NonNull
    public String Name;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    // Recurrence Flags
    public boolean Monday;
    public boolean Tuesday;
    public boolean Wednesday;
    public boolean Thursday;
    public boolean Friday;
    public boolean Saturday;
    public boolean Sunday;

    // Time Type Flags
    public boolean Sunrise;
    public boolean Sunset;
    public boolean SolarNoon;
    public boolean CivilTwilightBegin;
    public boolean CivilTwilightEnd;
    public boolean NauticalTwilightBegin;
    public boolean NauticalTwilightEnd;
    public boolean AstronomicalTwilightBegin;
    public boolean AstronomicalTwilightEnd;

    // public ??? DayLength;
}
