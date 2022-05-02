package com.example.solar_alarm.Data.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.solar_alarm.Data.Tables.OffsetType;
import com.example.solar_alarm.Data.Tables.SolarTimeType;
import com.example.solar_alarm.Data.Tables.TimeUnitType;

@Dao
public interface StaticDataDao
{
    @Insert
    void Insert(TimeUnitType timeUnitType);

    @Query("SELECT EXISTS(SELECT * FROM TimeUnitTypes)")
    boolean isTimeUnitTypesExists();

    @Insert
    void Insert(OffsetType offsetType);

    @Query("SELECT EXISTS(SELECT * FROM OffsetTypes)")
    boolean isAlarmTypesExists();

    @Insert
    void Insert(SolarTimeType solarTimeType);

    @Query("SELECT EXISTS(SELECT * FROM SolarTimeTypes)")
    boolean isSolarTimeTypesExists();
}
