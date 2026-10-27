package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.di.IoDispatcher
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class SolarAlarmRepositoryImpl @Inject constructor(
    private val database: SolarAlarmDatabase,
    private val solarAlarmDao: SolarAlarmDao,
    private val solarTimeDao: SolarTimeDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SolarAlarmRepository {

    override val all: Flow<List<SolarAlarm>> = solarAlarmDao.getAll()
    override val allWithDetails: Flow<List<SolarAlarmWithDetails>> = solarAlarmDao.getAllWithDetails()

    override suspend fun insert(solarAlarm: SolarAlarm) {
        withContext(ioDispatcher) {
            solarAlarmDao.insert(solarAlarm)
        }
    }

    override suspend fun getById(id: Int): SolarAlarm? = withContext(ioDispatcher) {
        solarAlarmDao.getById(id)
    }

    override suspend fun update(solarAlarm: SolarAlarm) {
        withContext(ioDispatcher) {
            solarAlarmDao.update(solarAlarm)
        }
    }

    override suspend fun delete(solarAlarm: SolarAlarm) {
        withContext(ioDispatcher) {
            database.withTransaction {
                solarAlarmDao.delete(solarAlarm)
                solarTimeDao.deleteSolarTimeIfNotUsed(solarAlarm.SolarTimeId)
            }
        }
    }

    override suspend fun isSolarAlarmNameLocationIdPairExists(solarAlarm: SolarAlarm): Boolean = withContext(ioDispatcher) {
        solarAlarmDao.isSolarAlarmNameLocationIdPairExists(solarAlarm.Name, solarAlarm.LocationId)
    }
}
