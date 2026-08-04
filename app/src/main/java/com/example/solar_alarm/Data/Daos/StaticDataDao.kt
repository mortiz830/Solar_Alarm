//package com.example.solar_alarm.data.daos
//
//import com.example.solar_alarm.data.tables.OffsetType
//import com.example.solar_alarm.data.tables.SolarTimeType
//import androidx.room.*
//
//@Dao
//interface StaticDataDao {
//    @Insert
//    fun insert(offsetType: OffsetType)
//
//    @get:Query("SELECT EXISTS(SELECT * FROM OffsetType)")
//    val isOffsetTypesExists: Boolean
//
//    @Insert
//    fun insert(solarTimeType: SolarTimeType)
//
//    @get:Query("SELECT EXISTS(SELECT * FROM SolarTimeType)")
//    val isSolarTimeTypesExists: Boolean
//}