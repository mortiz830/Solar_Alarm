package com.example.solar_alarm.Data.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface SolarTimeDao
{
    @Insert
    void Insert(SolarTime solarTime);

    @Query("SELECT * FROM SolarTime ORDER BY SolarDate")
    LiveData<List<SolarTime>> getAll();

    @Query("SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date")
    SolarTime getSolarTime(int locationId, LocalDate date);

    @Query("SELECT EXISTS(SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date)")
    boolean isLocationIDDatePairExists(int locationId, LocalDate date);

    @Query("SELECT * FROM SolarTime WHERE Id = :id")
    SolarTime getById(int id);

    @Update
    void Update(SolarTime solarTime);

    @Delete
    void delete(SolarTime solarTime);
}
