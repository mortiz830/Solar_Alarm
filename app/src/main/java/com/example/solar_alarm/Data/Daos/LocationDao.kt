package com.example.solar_alarm.Data.Daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.solar_alarm.Data.Tables.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao// : BaseDao<Location>
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM Location")
    fun GetAll(): LiveData<List<Location>>

    @Query("SELECT * FROM Location WHERE Id = :id")
    suspend fun GetById(id: Int): Location?

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