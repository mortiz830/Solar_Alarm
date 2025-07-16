package com.example.solar_alarm.Data.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.solar_alarm.Data.Tables.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao// : BaseDao<Location>
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM Location ORDER BY Name")
    fun GetAll(): Flow<List<Location>>

    @Query("SELECT * FROM Location WHERE Id = :id")
    fun GetById(id: Int): Location

    @Query("SELECT * FROM Location WHERE Name = :name")
    fun GetByName(name: String): Location

    @Update
    suspend fun update(location: Location): Int

    @Delete
    suspend fun Delete(location: Location)

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    suspend fun DoesLocationNameExists(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Latitude = :latitude AND Longitude = :longitude)")
    suspend fun DoesLocationLatLongExists(latitude: Double, longitude: Double): Boolean

    @Query("SELECT MAX(Id) FROM Location")
    suspend fun MaxId() : Int?
}