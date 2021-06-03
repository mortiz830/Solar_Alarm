package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "Locations")
public class Location extends TableBase
{
    @NonNull
    public String Name;

    public float Latitude;

    public float Longitude;
}
