package com.example.solar_alarm.Data.Enums;

public enum TimeUnitTypeEnum
{
    Minute(1, "Monitor"),
    Hour(2, "Hour");

    public final int    Id;
    public final String Name;

    TimeUnitTypeEnum(int id, String name)
    {
        Id   = id;
        Name = name;
    }
}
