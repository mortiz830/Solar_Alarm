package com.example.solar_alarm.data.daos

import com.example.solar_alarm.data.enums.OffsetTypeEnum
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import androidx.room.*

@Dao
interface StaticDataDao {
    @Insert
    suspend fun insert(offsetType: OffsetTypeEnum)

    @Query("SELECT EXISTS(SELECT * FROM OffsetTypeEnum)")
    suspend fun isOffsetTypesExists(): Boolean

    @Insert
    suspend fun insert(solarTimeType: SolarTimeTypeEnum)

    @Query("SELECT EXISTS(SELECT * FROM SolarTimeTypeEnum)")
    suspend fun isSolarTimeTypesExists(): Boolean
}