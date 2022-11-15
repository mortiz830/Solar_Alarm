package com.example.solar_alarm.Data.Daos

import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.Tables.SolarTime
import androidx.room.*
import java.time.LocalDate

@Dao
interface SolarTimeDao {
    @Insert
    fun Insert(solarTime: SolarTime?)

    @get:Query("SELECT * FROM SolarTime ORDER BY SolarDate")
    val all: LiveData<List<SolarTime?>?>?

    @Query("SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date")
    fun getSolarTime(locationId: Int, date: LocalDate?): SolarTime?

    @Query("SELECT EXISTS(SELECT * FROM SolarTime WHERE LocationId = :locationId AND SolarDate = :date)")
    fun isLocationIDDatePairExists(locationId: Int, date: LocalDate?): Boolean

    @Query("SELECT * FROM SolarTime WHERE Id = :id")
    fun getById(id: Int): SolarTime?

    @Update
    fun Update(solarTime: SolarTime?)

    @Delete
    fun delete(solarTime: SolarTime?)
}