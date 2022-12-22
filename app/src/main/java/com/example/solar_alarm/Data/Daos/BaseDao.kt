package com.example.solar_alarm.Data.Daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T>
{
    /**
     * Insert an object in the database.
     * @param obj the object to be inserted.
     */
    @Insert
    suspend fun Insert(obj: T)

    /**
     * Insert an array of objects in the database.
     * @param obj the objects to be inserted.
     */
    @Insert
    suspend fun Insert(vararg obj: T)

    /**
     * Update an object from the database.
     * @param obj the object to be updated
     */
    @Update
    suspend fun Update(obj: T)

    /**
     * Delete an object from the database
     * @param obj the object to be deleted
     */
    @Delete
    suspend fun Delete(obj: T)
}