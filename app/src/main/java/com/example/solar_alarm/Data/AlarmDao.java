package com.example.solar_alarm.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    // Create
    @Insert
    void insert(Alarm alarm);

    // Read
    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    LiveData<List<Alarm>> getAlarms();

    // Update
    @Update
    void update(Alarm alarm);

    //@Query("DELETE FROM alarm_table WHERE alarmId = :alarmId")
    @Delete
    void delete(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();
}
