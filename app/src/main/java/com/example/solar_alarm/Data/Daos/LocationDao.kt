package com.example.solar_alarm.data.daos

// Repair: Fixed broken package/import lines
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM Location ORDER BY Name")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM Location WHERE Id = :id")
    suspend fun getById(id: Int): Location

    @Query("SELECT * FROM Location WHERE Name = :name")
    suspend fun getByName(name: String): Location

    @Update
    suspend fun update(location: Location): Int

    @Delete
    suspend fun delete(location: Location)

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    suspend fun doesLocationNameExists(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Latitude = :latitude AND Longitude = :longitude)")
    suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean

    @Query("SELECT MAX(Id) FROM Location")
    suspend fun maxId() : Int?
}