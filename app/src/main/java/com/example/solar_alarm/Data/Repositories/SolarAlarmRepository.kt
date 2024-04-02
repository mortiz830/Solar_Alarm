package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.annotation.WorkerThread
import com.example.solar_alarm.Data.Tables.SolarAlarm
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlinx.coroutines.flow.Flow

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarAlarmRepository(private val solarAlarmDao: SolarAlarmDao)
{
    val all: Flow<List<SolarAlarm>> = solarAlarmDao.GetAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun Insert(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.insert(solarAlarm)
    }

    @WorkerThread
    suspend fun GetById(id: Int)
    {
        solarAlarmDao.GetById(id)
    }

    @WorkerThread
    suspend fun Update(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.update(solarAlarm)
    }

    @WorkerThread
    suspend fun Delete(solarAlarm: SolarAlarm)
    {
        solarAlarmDao.delete(solarAlarm)
    }

    @WorkerThread
    fun isSolarAlarmNameLocationIDExists(solarAlarm: SolarAlarm): Boolean
    {
        return solarAlarmDao.isSolarAlarmNameLocationIDPairExists(solarAlarm.Name, solarAlarm.LocationId)
    }
}