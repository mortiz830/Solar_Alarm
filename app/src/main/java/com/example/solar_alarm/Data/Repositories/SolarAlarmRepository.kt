package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.annotation.WorkerThread
import com.example.solar_alarm.Data.Tables.SolarAlarm
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Tables.SolarAlarmWithDetails
import com.example.solar_alarm.Data.Tables.SolarTime
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
    suspend fun isSolarAlarmNameLocationIDExists(solarAlarm: SolarAlarm): Boolean
    {
        return solarAlarmDao.isSolarAlarmNameLocationIdPairExists(solarAlarm.Name, solarAlarm.LocationId)
    }
}