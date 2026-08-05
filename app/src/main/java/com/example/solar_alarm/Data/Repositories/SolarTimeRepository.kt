package com.example.solar_alarm.data.repositories

// Fixed broken line
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.daos.SolarTimeDao
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarTimeRepository(private val solarTimeDao: SolarTimeDao)
{
    val all: Flow<List<SolarTime>> = solarTimeDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(solarTime: SolarTime)
    {
        solarTimeDao.insert(solarTime)
    }

    @WorkerThread
    suspend fun getById(id: Int) : SolarTime
    {
        return solarTimeDao.getById(id)
    }

    @WorkerThread
    suspend fun update(solarTime: SolarTime)
    {
        solarTimeDao.update(solarTime)
    }

    @WorkerThread
    suspend fun delete(solarTime: SolarTime)
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
            val sunriseSunsetResponse = HttpRequests().getSolarData(sunriseSunsetRequest)

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

            insert(solarTime)   // save response as a new SolarTime

            solarTime = solarTimeDao.getSolarTime(location.Id, date)   // reload from DB to get ID number
        }

        return solarTime
    }
}
