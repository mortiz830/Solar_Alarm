package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.room.withTransaction
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class SolarAlarmRepositoryImpl @Inject constructor(
    private val database: SolarAlarmDatabase,
    private val solarAlarmDao: SolarAlarmDao,
    private val solarTimeDao: SolarTimeDao
) : SolarAlarmRepository {

    override val all: Flow<List<SolarAlarm>> = solarAlarmDao.getAll()
    override val allWithDetails: Flow<List<SolarAlarmWithDetails>> = solarAlarmDao.getAllWithDetails()

    @WorkerThread
    override suspend fun insert(solarAlarm: SolarAlarm) {
        solarAlarmDao.insert(solarAlarm)
    }

    @WorkerThread
    override suspend fun getById(id: Int): SolarAlarm? {
        return solarAlarmDao.getById(id)
    }

    @WorkerThread
    override suspend fun update(solarAlarm: SolarAlarm) {
        solarAlarmDao.update(solarAlarm)
    }

    @WorkerThread
    override suspend fun delete(solarAlarm: SolarAlarm) {
        database.withTransaction {
            solarAlarmDao.delete(solarAlarm)
            solarTimeDao.deleteSolarTimeIfNotUsed(solarAlarm.SolarTimeId)
        }
    }

    @WorkerThread
    override suspend fun isSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean {
        return solarAlarmDao.isSolarAlarmNameLocationIdPairExists(solarAlarm.Name, solarAlarm.LocationId)
    }
}
