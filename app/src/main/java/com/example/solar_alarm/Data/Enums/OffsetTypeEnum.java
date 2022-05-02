package com.example.solar_alarm.Data.Enums;

public enum OffsetTypeEnum
{
    Before(1, "Before"),
    At(2, "At"),
    After(3, "After");

    public final int    Id;
    public final String Name;

    OffsetTypeEnum(int id, String name)
    {
        Id   = id;
        Name = name;
    }
}
