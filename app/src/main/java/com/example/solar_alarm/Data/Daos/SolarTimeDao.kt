package com.example.solar_alarm.Data.Daos

import com.example.solar_alarm.Data.Tables.SolarTime
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class SolarTimeDao : BaseDao<SolarTime>
{
    @Query("SELECT * FROM SolarTime ORDER BY SolarDate")
    abstract fun GetAll(): Flow<List<SolarTime>>

    @Query("SELECT * FROM SolarTime WHERE Id = :id")
    abstract fun GetById(id: Int): SolarTime?

    // Returns one item or null
    @Query("SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date")
    abstract fun getSolarTime(locationId: Int, date: LocalDate): SolarTime?

    @Query("SELECT EXISTS(SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date)")
    abstract fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean
}