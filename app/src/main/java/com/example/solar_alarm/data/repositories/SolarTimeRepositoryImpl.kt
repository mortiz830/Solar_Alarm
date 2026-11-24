package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.di.IoDispatcher
import com.example.solar_alarm.data.solarCalculator.NoaaSolarCalculator
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class SolarTimeRepositoryImpl @Inject constructor(
    private val solarTimeDao: SolarTimeDao,
    private val solarCalculator: NoaaSolarCalculator = NoaaSolarCalculator(),
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SolarTimeRepository {

    override val all: Flow<List<SolarTime>> = solarTimeDao.getAll()

    override suspend fun insert(solarTime: SolarTime) {
        withContext(ioDispatcher) {
            solarTimeDao.insert(solarTime)
        }
    }

    override suspend fun getById(id: Int): SolarTime = withContext(ioDispatcher) {
        solarTimeDao.getById(id)
    }

    override suspend fun update(solarTime: SolarTime) {
        withContext(ioDispatcher) {
            solarTimeDao.update(solarTime)
        }
    }

    override suspend fun delete(solarTime: SolarTime) {
        withContext(ioDispatcher) {
            solarTimeDao.delete(solarTime)
        }
    }

    override suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean = withContext(ioDispatcher) {
        solarTimeDao.doesLocationIdDatePairExists(locationId, date)
    }

    override suspend fun getSolarTime(location: Location, date: LocalDate): Result<SolarTime> = withContext(ioDispatcher) {
        runCatching {
            var solarTime = solarTimeDao.getSolarTime(location.Id, date)

            if (solarTime == null) {
                // Calculate directly and offline via NOAA astronomical calculation engine
                val calculated = solarCalculator.calculateSolarTime(
                    locationId = location.Id,
                    latitude = location.Latitude,
                    longitude = location.Longitude,
                    date = date
                )

                solarTimeDao.insert(calculated)

                solarTime = solarTimeDao.getSolarTime(location.Id, date) ?: calculated
            }

            solarTime
        }
    }

    override suspend fun getWeeklySolarTimes(location: Location, startDate: LocalDate): Result<List<SolarTime>> = withContext(ioDispatcher) {
        runCatching {
            val solarTimes = mutableListOf<SolarTime>()
            var date = startDate

            for (i in 1..7) {
                val dailySolarTime = getSolarTime(location, date).getOrThrow()
                solarTimes.add(dailySolarTime)
                date = date.plusDays(1)
            }

            solarTimes
        }
    }

    override suspend fun calculateSolarTimeOffline(location: Location, date: LocalDate): SolarTime = withContext(ioDispatcher) {
        var solarTime = solarTimeDao.getSolarTime(location.Id, date)
        if (solarTime == null) {
            solarTime = solarCalculator.calculateSolarTime(location.Id, location.Latitude, location.Longitude, date)
            solarTimeDao.insert(solarTime)
            solarTime = solarTimeDao.getSolarTime(location.Id, date) ?: solarTime
        }
        solarTime
    }
}
