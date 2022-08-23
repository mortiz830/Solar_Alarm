package com.example.solar_alarm.DisplayModels;

import com.example.solar_alarm.Data.Tables.Timezone;

public class TimezoneDisplayModel
{
    private Timezone _Timezone;

    public TimezoneDisplayModel(Timezone timezone)
    {
        _Timezone = timezone;
    }

    public String GetCountryCode() { return _Timezone.CountryCode; }

    public String GetCountryName() { return _Timezone.CountryName; }

    public String GetZoneName() { return _Timezone.ZoneName; }

    public String GetAbbreviation() { return _Timezone.Abbreviation; }

    public int GetGmtOffset() { return _Timezone.GmtOffset; }

    public Boolean IsDayLightSaving() { return _Timezone.Dst; }

    public int GetZoneStart() { return _Timezone.ZoneStart; }

    public int GetZoneEnd() { return _Timezone.ZoneEnd; }

    public String GetNextAbbreviation() { return _Timezone.NextAbbreviation; }

    public int GetTimestamp() { return _Timezone.Timestamp; }
}
