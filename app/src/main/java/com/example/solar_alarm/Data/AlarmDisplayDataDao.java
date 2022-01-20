package com.example.solar_alarm.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmDisplayDataDao {
    @Query("SELECT DISTINCT Location.Id AS LocationID, " +
            "SolarAlarm.Id AS SolarAlarmID, " +
            "SolarTime.Id AS SolarTimeID FROM Location" +
            " JOIN SolarAlarm ON (Location.Id = SolarAlarm.LocationId)" +
            " JOIN SolarTime ON (Location.Id = SolarTime.LocationId)" +
            " ORDER BY 1,2,3")
    public LiveData<List<AlarmDisplayData>> loadAlarmData();
}
