package com.example.solar_alarm.data.daos

// Repair: Fixed broken package/import lines
import androidx.room.*

interface BaseDao<T>
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: T)

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T)
}