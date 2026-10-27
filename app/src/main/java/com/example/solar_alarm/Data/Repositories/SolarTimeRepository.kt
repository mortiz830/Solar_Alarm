package com.example.solar_alarm.data.repositories

import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface SolarTimeRepository {
    val all: Flow<List<SolarTime>>

    suspend fun insert(solarTime: SolarTime)
    suspend fun getById(id: Int): SolarTime
    suspend fun update(solarTime: SolarTime)
    suspend fun delete(solarTime: SolarTime)
    suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean
    suspend fun getSolarTime(location: Location, date: LocalDate): Result<SolarTime>
    suspend fun getWeeklySolarTimes(location: Location, startDate: LocalDate = LocalDate.now()): Result<List<SolarTime>>
}
