package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override val all: Flow<List<Location>> = locationDao.getAll()

    @WorkerThread
    override suspend fun insert(location: Location) {
        locationDao.insert(location)
    }

    @WorkerThread
    override suspend fun getById(id: Int): Location {
        return locationDao.getById(id)
    }

    @WorkerThread
    override suspend fun getByName(name: String): Location {
        return locationDao.getByName(name)
    }

    @WorkerThread
    override suspend fun update(location: Location) {
        locationDao.update(location)
    }

    @WorkerThread
    override suspend fun delete(location: Location) {
        locationDao.delete(location)
    }

    @WorkerThread
    override suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean {
        return locationDao.doesLocationLatLongExists(latitude, longitude)
    }

    @WorkerThread
    override suspend fun doesLocationNameExists(name: String?): Boolean {
        return locationDao.doesLocationNameExists(name)
    }

    @WorkerThread
    override suspend fun maxId(): Int? {
        return locationDao.maxId()
    }
}
