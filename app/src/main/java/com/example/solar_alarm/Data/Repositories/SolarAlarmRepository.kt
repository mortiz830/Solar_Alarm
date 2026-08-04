package com.example.solar_alarm.data.repositories

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.tables.SolarAlarm
import androidx.lifecycle.LiveData
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import com.example.solar_alarm.data.tables.SolarTime
import kotlinx.coroutines.flow.Flow

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarAlarmRepository(private val solarAlarmDao: SolarAlarmDao)
{
    val all: Flow<List<SolarAlarm>> = solarAlarmDao.getAll()
    val allWithDetails: Flow<List<SolarAlarmWithDetails>> = solarAlarmDao.getAllWithDetails()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.insert(solarAlarm)
    }

    @WorkerThread
    suspend fun getById(id: Int): SolarAlarm?
    {
        return solarAlarmDao.getById(id)
    }

    @WorkerThread
    suspend fun update(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.update(solarAlarm)
    }

    @WorkerThread
    suspend fun delete(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.delete(solarAlarm)
    }

    @WorkerThread
    suspend fun isSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean
    {
        return solarAlarmDao.isSolarAlarmNameLocationIdPairExists(solarAlarm.Name, solarAlarm.LocationId)
    }
}