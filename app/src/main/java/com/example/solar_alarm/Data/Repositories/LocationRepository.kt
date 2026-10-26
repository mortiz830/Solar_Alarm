package com.example.solar_alarm.data.repositories

import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val all: Flow<List<Location>>

    suspend fun insert(location: Location)
    suspend fun getById(id: Int): Location
    suspend fun getByName(name: String): Location
    suspend fun update(location: Location)
    suspend fun delete(location: Location)
    suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean
    suspend fun doesLocationNameExists(name: String?): Boolean
    suspend fun maxId(): Int?
}
