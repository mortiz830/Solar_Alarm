package com.example.solar_alarm.Data.Daos;

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

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    boolean isLocationNameExists(String name);

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Latitude = :latitude)")
    boolean isLocationLatitudeExists(double latitude);

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Longitude = :longitude)")
    boolean isLocationLongitudeExists(double longitude);

    @Update
    void Update(Location location);

    @Delete
    void delete(Location location);
}
