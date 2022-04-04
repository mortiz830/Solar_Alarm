package com.example.solar_alarm.Data.Daos;

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

    @Query("SELECT EXISTS(SELECT * FROM SolarAlarm WHERE Name = :name AND LocationId = :locationId)")
    boolean isSolarAlarmNameLocationIDPairExists(String name, int locationId);

    @Update
    void Update(SolarAlarm solarAlarm);

    @Delete
    void delete(SolarAlarm solarAlarm);
}
