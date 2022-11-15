package com.example.solar_alarm.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    // Create
    @Insert
    fun insert(alarm: Alarm?)

    // Read all
    @get:Query("SELECT * FROM alarm_table ORDER BY created ASC")
    val alarms: LiveData<List<Alarm?>?>?

    // Update
    @Update
    fun update(alarm: Alarm?)

    //@Query("DELETE FROM alarm_table WHERE alarmId = :alarmId")
    @Delete
    fun delete(alarm: Alarm?)

    @Query("DELETE FROM alarm_table")
    fun deleteAll()
}