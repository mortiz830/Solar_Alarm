package com.example.solar_alarm.Data.Repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
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
        solarTimeDao.insert(solarTime)
    }

    @WorkerThread
    suspend fun GetById(id: Int)
    {
        solarTimeDao.GetById(id)
    }

    @WorkerThread
    suspend fun Update(solarTime: SolarTime)
    {
        solarTimeDao.update(solarTime)
    }

    @WorkerThread
    suspend fun Delete(solarTime: SolarTime)
    {
        solarTimeDao.delete(solarTime)
    }

    @WorkerThread
    suspend fun doesLocationIdDatePairExists(locationId: Int, date: LocalDate): Boolean
    {
        return solarTimeDao.doesLocationIdDatePairExists(locationId, date)
    }

    @WorkerThread
    suspend fun getSolarTime(location: Location, date: LocalDate): SolarTime?
    {
        var solarTime = solarTimeDao.getSolarTime(location.Id, date)

        if (solarTime == null)
        {
            // Make HTTP Request to API
            val sunriseSunsetRequest  = SunriseSunsetRequest(location.Latitude.toFloat(), location.Longitude.toFloat(), date)
            val sunriseSunsetResponse = HttpRequests().GetSolarData(sunriseSunsetRequest)

            solarTime = SolarTime(date,
                                  location.Id,
                                  sunriseSunsetResponse?.results?.day_length!!,
                                  sunriseSunsetResponse.results?.sunrise,
                                  sunriseSunsetResponse.results?.sunset,
                                  sunriseSunsetResponse.results?.solar_noon,
                                  sunriseSunsetResponse.results?.civil_twilight_begin,
                                  sunriseSunsetResponse.results?.civil_twilight_end,
                                  sunriseSunsetResponse.results?.nautical_twilight_begin,
                                  sunriseSunsetResponse.results?.nautical_twilight_end,
                                  sunriseSunsetResponse.results?.astronomical_twilight_begin,
                                  sunriseSunsetResponse.results?.astronomical_twilight_end)

            Insert(solarTime)   // save response as a new SolarTime
        }

        return solarTime
    }
}