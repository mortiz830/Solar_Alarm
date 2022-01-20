package com.example.solar_alarm.Data.Enums;

public enum TimeTypeEnum
{
    Sunrise(1, "Sunrise"),
    Sunset(2, "Sunset"),
    SolarNoon(3, "SolarNoon");//,
    //CivilTwilightBegin(4),
    //CivilTwilightEnd(5),
    //NauticalTwilightBegin(6),
    //NauticalTwilightEnd(7),
    //AstronomicalTwilightBegin(8),
    //AstronomicalTwilightEnd(9)

    public final int    Id;
    public final String Name;

    TimeTypeEnum(int id, String name)
    {
        Id   = id;
        Name = name;
    }
}
