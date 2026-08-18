package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class SolarAlarmRepository @Inject constructor(private val solarAlarmDao: SolarAlarmDao)
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
