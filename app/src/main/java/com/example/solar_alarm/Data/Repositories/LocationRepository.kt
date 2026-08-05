package com.example.solar_alarm.data.repositories

// Fixed broken line
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.flow.Flow

@RequiresApi(api = Build.VERSION_CODES.O)
class LocationRepository(private val locationDao: LocationDao)
{
    val all: Flow<List<Location>> = locationDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(location: Location)
    {
        locationDao.insert(location)
    }

    @WorkerThread
    suspend fun getById(id: Int): Location
    {
        return locationDao.getById(id)
    }

    @WorkerThread
    suspend fun getByName(name: String): Location
    {
        return locationDao.getByName(name)
    }

    @WorkerThread
    suspend fun update(location: Location)
    {
        locationDao.update(location)
    }

    @WorkerThread
    suspend fun delete(location: Location)
    {
        locationDao.delete(location)
    }

    @WorkerThread
    suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean
    {
        return locationDao.doesLocationLatLongExists(latitude, longitude)
    }

    @WorkerThread
    suspend fun doesLocationNameExists(name: String?): Boolean
    {
        return locationDao.doesLocationNameExists(name)
    }

    @WorkerThread
    suspend fun maxId(): Int?
    {
        return locationDao.maxId()
    }
}
