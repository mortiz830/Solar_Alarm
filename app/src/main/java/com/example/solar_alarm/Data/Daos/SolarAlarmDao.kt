package com.example.solar_alarm.Data.Daos

import com.example.solar_alarm.Data.Tables.SolarAlarm
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SolarAlarmDao {
    @Insert
    fun Insert(solarAlarm: SolarAlarm?)

    @get:Query("SELECT * FROM SolarAlarm ORDER BY Name")
    val all: LiveData<List<SolarAlarm?>?>?

    @Query("SELECT EXISTS(SELECT * FROM SolarAlarm WHERE Name = :name AND LocationId = :locationId)")
    fun isSolarAlarmNameLocationIDPairExists(name: String?, locationId: Int): Boolean

    @Update
    fun Update(solarAlarm: SolarAlarm?)

    @Delete
    fun delete(solarAlarm: SolarAlarm?)
}