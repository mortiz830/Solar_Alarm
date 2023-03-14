package com.example.solar_alarm.Data.Repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Tables.SolarTime
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarTimeRepository(private val solarTimeDao: SolarTimeDao)
{
    val all: Flow<List<SolarTime>> = solarTimeDao.GetAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun Insert(solarTime: SolarTime)
    {
        solarTimeDao.Insert(solarTime)
    }

    @WorkerThread
    suspend fun GetById(id: Int)
    {
        solarTimeDao.GetById(id)
    }

    @WorkerThread
    suspend fun Update(solarTime: SolarTime)
    {
        solarTimeDao.Update(solarTime)
    }

    @WorkerThread
    suspend fun Delete(solarTime: SolarTime)
    {
        solarTimeDao.Delete(solarTime)
    }

    @WorkerThread
    suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean
    {
        return solarTimeDao.doesLocationIdDatePairExists(locationId, date)
    }

    fun getSolarTime(locationId: Int, date: LocalDate): SolarTime?
    {
        var solarTime = solarTimeDao.getSolarTime(locationId, date)

        if (solarTime == null)
        {
            // Make HTTP Request to API
            // save response as a new SolarTime
            solarTime = solarTimeDao.getSolarTime(locationId, date)
        }


        return solarTime
    }
}