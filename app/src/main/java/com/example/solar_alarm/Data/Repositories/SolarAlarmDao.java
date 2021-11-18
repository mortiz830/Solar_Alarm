package com.example.solar_alarm.Data.Repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.List;

@Dao
public interface SolarAlarmDao
{
    @Insert
    void Insert(SolarAlarm solarAlarm);

    @Query("SELECT * FROM SolarAlarm ORDER BY Name")
    LiveData<List<SolarAlarm>> getAll();

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    boolean isSolarAlarmNameExists(String name);

    @Query("SELECT EXISTS(SELECT * FROM SolarTime WHERE LocationId = :locationId)")
    boolean isLocationIDExists(int locationId);

    @Update
    void Update(SolarAlarm solarAlarm);

    @Delete
    void delete(SolarAlarm solarAlarm);
}
