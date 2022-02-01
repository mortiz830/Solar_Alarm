package com.example.solar_alarm.Data.Enums;

public enum SolarTimeTypeEnum
{
    Sunrise(1, "Sunrise"),
    Sunset(2, "Sunset"),
    SolarNoon(3, "SolarNoon"),
    CivilTwilightBegin(4, "Civil Twilight Begin"),
    CivilTwilightEnd(5, "Civil Twilight End"),
    NauticalTwilightBegin(6, "Nautical Twilight Begin"),
    NauticalTwilightEnd(7, "Nautical Twilight End"),
    AstronomicalTwilightBegin(8, "Astronomical Twilight Begin"),
    AstronomicalTwilightEnd(9, "Astronomical Twilight End");

    public final int    Id;
    public final String Name;

    SolarTimeTypeEnum(int id, String name)
    {
        Id   = id;
        Name = name;
    }
}
