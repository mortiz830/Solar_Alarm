package com.example.solar_alarm.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.di.IoDispatcher
import com.example.solar_alarm.data.tables.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(api = Build.VERSION_CODES.O)
@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationRepository {

    override val all: Flow<List<Location>> = locationDao.getAll()

    override suspend fun insert(location: Location) {
        withContext(ioDispatcher) {
            locationDao.insert(location)
        }
    }

    override suspend fun getById(id: Int): Location = withContext(ioDispatcher) {
        locationDao.getById(id)
    }

    override suspend fun getByName(name: String): Location = withContext(ioDispatcher) {
        locationDao.getByName(name)
    }

    override suspend fun update(location: Location) {
        withContext(ioDispatcher) {
            locationDao.update(location)
        }
    }

    override suspend fun delete(location: Location) {
        withContext(ioDispatcher) {
            locationDao.delete(location)
        }
    }

    override suspend fun doesLocationLatLongExists(latitude: Double, longitude: Double): Boolean = withContext(ioDispatcher) {
        locationDao.doesLocationLatLongExists(latitude, longitude)
    }

    override suspend fun doesLocationNameExists(name: String?): Boolean = withContext(ioDispatcher) {
        locationDao.doesLocationNameExists(name)
    }

    override suspend fun maxId(): Int? = withContext(ioDispatcher) {
        locationDao.maxId()
    }
}
