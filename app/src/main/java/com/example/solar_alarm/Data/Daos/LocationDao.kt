package com.example.solar_alarm.Data.Daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.solar_alarm.Data.Tables.*

@Dao
interface LocationDao {
    @Insert
    fun Insert(location: Location?)

    @get:Query("SELECT * FROM Location ORDER BY Name")
    val all: LiveData<List<Location?>?>?

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Name = :name)")
    fun isLocationNameExists(name: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Latitude = :latitude)")
    fun isLocationLatitudeExists(latitude: Double): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Location WHERE Longitude = :longitude)")
    fun isLocationLongitudeExists(longitude: Double): Boolean

    @Query("SELECT * FROM Location WHERE Id = :id")
    fun GetById(id: Int): Location?

    @Update
    fun Update(location: Location?)

    @Delete
    fun delete(location: Location?)
}