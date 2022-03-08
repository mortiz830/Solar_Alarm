package com.example.solar_alarm.Data.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.solar_alarm.Data.Tables.AlarmType;
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
    void Insert(AlarmType alarmType);

    @Query("SELECT EXISTS(SELECT * FROM AlarmTypes)")
    boolean isAlarmTypesExists();

    @Insert
    void Insert(SolarTimeType solarTimeType);

    @Query("SELECT EXISTS(SELECT * FROM SolarTimeTypes)")
    boolean isSolarTimeTypesExists();
}
