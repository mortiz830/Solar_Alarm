package com.example.solar_alarm.Data.Daos

import androidx.room.*
import androidx.room.RoomMasterTable.TABLE_NAME
import java.util.concurrent.Flow

interface BaseDao<T>
{
    //@Query("SELECT * FROM ${T}")
    //fun getAll(): Flow<List<T>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T)
}