package com.example.solar_alarm.Data.Tables;

import androidx.room.Entity;

@Entity(tableName = "Timezones")
public class Timezone extends TableBase
{
    public String CountryCode;

    public String CountryName;

    public String ZoneName;

    public String Abbreviation;

    public int GmtOffset;

    public Boolean Dst;

    public int ZoneStart;

    public int ZoneEnd;

    public String NextAbbreviation;

    public int Timestamp;
}
