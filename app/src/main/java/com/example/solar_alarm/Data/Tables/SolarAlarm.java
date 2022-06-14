package com.example.solar_alarm.Data.Tables;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity
(
    tableName = "SolarAlarm",
    indices = {@Index(value = {"Name", "LocationId"}, unique = true)}
)
public class SolarAlarm extends TableBase
{
    @NonNull
    public String Name;

    @ForeignKey(entity = Location.class, parentColumns = "Id", childColumns = "LocationId")
    public int LocationId;

    @ForeignKey(entity = SolarTime.class, parentColumns = "Id", childColumns = "SolarTimeId")
    public int SolarTimeId;

    // Recurrence Flags
    public boolean Recurring;
    public boolean Monday;
    public boolean Tuesday;
    public boolean Wednesday;
    public boolean Thursday;
    public boolean Friday;
    public boolean Saturday;
    public boolean Sunday;

    @ForeignKey(entity = OffsetType.class, parentColumns = "Id", childColumns = "OffsetTypeId")
    public int OffsetTypeId;

    @ForeignKey(entity = SolarTimeType.class, parentColumns = "Id", childColumns = "SolarTimeTypeId")
    public int SolarTimeTypeId;

    @ForeignKey(entity = TimeUnitType.class, parentColumns = "Id", childColumns = "TimeUnitTypeId")
    public int TimeUnitTypeId;
}
