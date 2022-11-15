package com.example.solar_alarm.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDisplayDataDao {
    @Query("SELECT DISTINCT Location.Id AS LocationID, " +
            "SolarAlarm.Id AS SolarAlarmID, " +
            "SolarTime.Id AS SolarTimeID FROM Location" +
            " JOIN SolarAlarm ON (Location.Id = SolarAlarm.LocationId)" +
            " JOIN SolarTime ON (Location.Id = SolarTime.LocationId)" +
            " ORDER BY 1,2,3")
    fun loadAlarmData(): LiveData<List<AlarmDisplayData?>?>?
}