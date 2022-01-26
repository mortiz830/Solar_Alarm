package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity
(
    tableName = "SolarAlarm",
    indices = {@Index(value = {"Name", "LocationId"}, unique = true)}
)
public class SolarAlarm extends TableBase
{
    @NonNull
    public String Name;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    // Recurrence Flags
    public boolean Recurring;
    public boolean Monday;
    public boolean Tuesday;
    public boolean Wednesday;
    public boolean Thursday;
    public boolean Friday;
    public boolean Saturday;
    public boolean Sunday;

    @ForeignKey(entity = AlarmType.class, parentColumns = "Id", childColumns = "AlarmTypeId")
    public int AlarmTypeId;

    @ForeignKey(entity = TimeType.class, parentColumns = "Id", childColumns = "TimeTypeId")
    public int TimeTypeId;

    @ForeignKey(entity = TimeUnitType.class, parentColumns = "Id", childColumns = "TimeUnitTypeId")
    public int TimeUnitTypeId;

    public int TimeOffsetValue;

    // public ??? DayLength;
}
