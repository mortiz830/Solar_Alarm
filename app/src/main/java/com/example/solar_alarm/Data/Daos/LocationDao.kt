package com.example.solar_alarm.Data.Daos

import androidx.room.*
import com.example.solar_alarm.Data.Tables.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocationDao : BaseDao<Location>
{
    @Query("SELECT * FROM Location ORDER BY Name")
    abstract fun GetAll(): Flow<List<Location>>

    @Query("SELECT * FROM Location WHERE Id = :id")
    abstract fun GetById(id: Int): Location?

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    abstract fun DoesLocationNameExists(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Latitude = :latitude AND Longitude = :longitude)")
    abstract fun DoesLocationLatLongExists(latitude: Double, longitude: Double): Boolean
}