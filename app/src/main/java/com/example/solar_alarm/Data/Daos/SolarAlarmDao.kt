package com.example.solar_alarm.data.daos

// Repair: Fixed broken package/import lines
import com.example.solar_alarm.data.tables.SolarAlarm
import androidx.room.*
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import com.example.solar_alarm.data.tables.SolarTime
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SolarAlarmDao : BaseDao<SolarAlarm>
{
    @Query("SELECT * FROM SolarAlarm ORDER BY Name")
    abstract fun getAll(): Flow<List<SolarAlarm>>

    @Transaction
    @Query("SELECT * FROM SolarAlarm ORDER BY Name")
    abstract fun getAllWithDetails(): Flow<List<SolarAlarmWithDetails>>

    @Query("SELECT * FROM SolarAlarm WHERE Id = :id")
    abstract suspend fun getById(id: Int): SolarAlarm?

    @Query("SELECT EXISTS(SELECT * FROM SolarAlarm WHERE Name = :name AND LocationId = :locationId)")
    abstract suspend fun isSolarAlarmNameLocationIdPairExists(name: String?, locationId: Int): Boolean
}