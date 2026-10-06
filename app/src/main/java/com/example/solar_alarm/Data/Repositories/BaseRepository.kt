package com.example.solar_alarm.data.repositories

import com.example.solar_alarm.data.daos.BaseDao
import kotlinx.coroutines.flow.Flow


open class BaseRepository<T>(private val dao: BaseDao<T>)
{
    //val allEntities: Flow<List<T>> = dao.getAll()

    suspend fun insert(entity: T) {
        dao.insert(entity)
    }

    suspend fun delete(entity: T) {
        dao.delete(entity)
    }
}