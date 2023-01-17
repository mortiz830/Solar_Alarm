package com.example.solar_alarm.Data.Daos

import com.example.solar_alarm.Data.Tables.SolarAlarm
import androidx.room.*
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SolarAlarmDao : BaseDao<SolarAlarm>
{
    @Query("SELECT * FROM SolarAlarm ORDER BY Name")
    abstract fun GetAll(): Flow<List<SolarAlarm>>

    @Query("SELECT * FROM SolarAlarm WHERE Id = :id")
    abstract fun GetById(id: Int): SolarTime?

    @Query("SELECT EXISTS(SELECT * FROM SolarAlarm WHERE Name = :name AND LocationId = :locationId)")
    abstract fun isSolarAlarmNameLocationIDPairExists(name: String?, locationId: Int): Boolean
}