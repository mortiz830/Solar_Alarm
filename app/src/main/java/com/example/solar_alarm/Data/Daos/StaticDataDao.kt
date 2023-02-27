package com.example.solar_alarm.Data.Daos

import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Tables.SolarTimeType
import androidx.room.*

@Dao
interface StaticDataDao {
    @Insert
    fun Insert(offsetType: OffsetType?)

    @get:Query("SELECT EXISTS(SELECT * FROM OffsetType)")
    val isOffsetTypesExists: Boolean

    @Insert
    fun Insert(solarTimeType: SolarTimeType?)

    @get:Query("SELECT EXISTS(SELECT * FROM SolarTimeType)")
    val isSolarTimeTypesExists: Boolean
}