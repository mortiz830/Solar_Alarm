package com.example.solar_alarm.data.daos

// Repair: Fixed broken package/import lines
import com.example.solar_alarm.data.tables.SolarTime
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class SolarTimeDao : BaseDao<SolarTime>
{
    @Query("SELECT * FROM SolarTime ORDER BY SolarDate")
    abstract fun getAll(): Flow<List<SolarTime>>

    @Query("SELECT * FROM SolarTime WHERE Id = :id")
    abstract suspend fun getById(id: Int): SolarTime

    @Query("SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date")
    abstract suspend fun getSolarTime(locationId: Int, date: LocalDate): SolarTime?

    @Query("SELECT EXISTS(SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date)")
    abstract suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean
}