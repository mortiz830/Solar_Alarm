package com.example.solar_alarm.Data.Tables;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "Timezone", indices = {@Index(value = {"ZoneName"},unique = true)})
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
