package com.example.solar_alarm.data.repositories

import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import kotlinx.coroutines.flow.Flow

interface SolarAlarmRepository {
    val all: Flow<List<SolarAlarm>>
    val allWithDetails: Flow<List<SolarAlarmWithDetails>>

    suspend fun insert(solarAlarm: SolarAlarm)
    suspend fun getById(id: Int): SolarAlarm?
    suspend fun update(solarAlarm: SolarAlarm)
    suspend fun delete(solarAlarm: SolarAlarm)
    suspend fun isSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean
}
