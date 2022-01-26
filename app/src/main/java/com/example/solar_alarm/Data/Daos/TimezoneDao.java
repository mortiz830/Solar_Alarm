package com.example.solar_alarm.Data.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.solar_alarm.Data.Tables.Timezone;

import java.util.List;

@Dao
public interface TimezoneDao
{
    @Insert
    void Insert(Timezone timezone);

    @Query("SELECT * FROM Timezone ORDER BY ZoneName")
    LiveData<List<Timezone>> getAll();

    @Update
    void Update(Timezone timezone);

    @Delete
    void delete(Timezone timezone);
}
