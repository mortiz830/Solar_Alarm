package com.example.solar_alarm.Data.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity
(
    tableName = "Location",
    indices =
    {
        @Index(value = {"Name"},                  unique = true),
        @Index(value = {"Latitude", "Longitude"}, unique = true)
    }
)
public class Location extends TableBase
{
    @NonNull
    public String Name;

    @ForeignKey(entity = Timezone.class, parentColumns = "Id", childColumns = "TimezoneId")
    public int TimezoneId;

    public double Latitude;

    public double Longitude;
}
