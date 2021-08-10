package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "Location")
public class Location extends TableBase
{
    @NonNull
    public String Name;

    @ForeignKey(entity = Timezone.class, parentColumns = "Id", childColumns = "TimezoneId")
    public int TimezoneId;

    public double Latitude;

    public double Longitude;
}
