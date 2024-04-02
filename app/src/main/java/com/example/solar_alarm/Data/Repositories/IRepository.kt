package com.example.solar_alarm.Data.Repositories

interface IRepository <T>
{
    suspend fun getAll(): List<T>
    suspend fun getById(id: Int): T?
    suspend fun add(entity: T): Int
    suspend fun update(entity: T): Int
    suspend fun delete(entity: T): Int
}
