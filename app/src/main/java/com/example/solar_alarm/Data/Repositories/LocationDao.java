package com.example.solar_alarm.Data.Repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.solar_alarm.Data.Tables.Location;

import java.util.List;

@Dao
public interface LocationDao
{
    @Insert
    void Insert(Location location);

    @Query("SELECT * FROM Location ORDER BY Name")
    LiveData<List<Location>> getAll();

    @Update
    void Update(Location location);

    @Delete
    void delete(Location location);
}
