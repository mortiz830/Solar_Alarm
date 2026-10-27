package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.sunrise_sunset_http.SolarRemoteDataSource
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class SolarTimeRepositoryImpl @Inject constructor(
    private val solarTimeDao: SolarTimeDao,
    private val solarRemoteDataSource: SolarRemoteDataSource
) : SolarTimeRepository {

    override val all: Flow<List<SolarTime>> = solarTimeDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun insert(solarTime: SolarTime) {
        solarTimeDao.insert(solarTime)
    }

    @WorkerThread
    override suspend fun getById(id: Int): SolarTime {
        return solarTimeDao.getById(id)
    }

    @WorkerThread
    override suspend fun update(solarTime: SolarTime) {
        solarTimeDao.update(solarTime)
    }

    @WorkerThread
    override suspend fun delete(solarTime: SolarTime) {
        solarTimeDao.delete(solarTime)
    }

    @WorkerThread
    override suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean {
        return solarTimeDao.doesLocationIdDatePairExists(locationId, date)
    }

    @WorkerThread
    override suspend fun getSolarTime(location: Location, date: LocalDate): Result<SolarTime> = runCatching {
        var solarTime = solarTimeDao.getSolarTime(location.Id, date)

        if (solarTime == null) {
            // Make HTTP Request to API via injected remote data source
            val sunriseSunsetRequest = SunriseSunsetRequest(location.Latitude.toFloat(), location.Longitude.toFloat(), date)
            val sunriseSunsetResponse = solarRemoteDataSource.getSolarData(sunriseSunsetRequest)

            if (sunriseSunsetResponse.status != "OK" && sunriseSunsetResponse.results == null) {
                throw IOException("Sunrise-Sunset API returned error status: ${sunriseSunsetResponse.status ?: "UNKNOWN"}")
            }

            val results = sunriseSunsetResponse.results
                ?: throw IOException("No solar data results received from Sunrise-Sunset API")

            solarTime = SolarTime(
                date,
                location.Id,
                results.day_length,
                results.sunrise,
                results.sunset,
                results.solar_noon,
                results.civil_twilight_begin,
                results.civil_twilight_end,
                results.nautical_twilight_begin,
                results.nautical_twilight_end,
                results.astronomical_twilight_begin,
                results.astronomical_twilight_end
            )

            insert(solarTime) // save response as a new SolarTime

            solarTime = solarTimeDao.getSolarTime(location.Id, date) // reload from DB to get ID number
                ?: throw IOException("Failed to retrieve cached solar time after insertion")
        }

        solarTime
    }

    @WorkerThread
    override suspend fun getWeeklySolarTimes(location: Location, startDate: LocalDate): Result<List<SolarTime>> = runCatching {
        val solarTimes = mutableListOf<SolarTime>()
        var date = startDate

        for (i in 1..7) {
            val dailyResult = getSolarTime(location, date)
            if (dailyResult.isFailure) {
                throw dailyResult.exceptionOrNull() ?: IOException("Failed to fetch solar time for $date")
            }
            solarTimes.add(dailyResult.getOrThrow())
            date = date.plusDays(1)
        }

        solarTimes
    }
}
