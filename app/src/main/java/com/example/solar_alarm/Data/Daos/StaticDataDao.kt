package com.example.solar_alarm.data.daos

import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import androidx.room.*

@Dao
interface StaticDataDao {
    @Insert
    fun insert(offsetType: OffsetTypeEnum)

    @get:Query("SELECT EXISTS(SELECT * FROM OffsetTypeEnum)")
    val isOffsetTypesExists: Boolean

    @Insert
    fun insert(solarTimeType: SolarTimeTypeEnum)

    @get:Query("SELECT EXISTS(SELECT * FROM SolarTimeTypeEnum)")
    val isSolarTimeTypesExists: Boolean
}