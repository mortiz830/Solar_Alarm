package com.example.solar_alarm.Data.Repositories

import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Tables.SolarTimeType
import kotlinx.coroutines.flow.Flow

@RequiresApi(api = Build.VERSION_CODES.O)
class LocationRepository(private val locationDao: LocationDao)
{
    //private val staticDataDao: StaticDataDao = _SolarAlarmDatabase.staticDataDao()

    val allLocations: Flow<List<Location>> = locationDao.GetAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun Insert(location: Location)
    {
        locationDao.Insert(location)
    }

    @WorkerThread
    suspend fun GetById(id: Int)
    {
        locationDao.GetById(id)
    }

    @WorkerThread
    suspend fun Update(location: Location)
    {
        locationDao.Update(location)
    }

    @WorkerThread
    suspend fun Delete(location: Location)
    {
        locationDao.Delete(location)
    }

    @WorkerThread
    suspend fun DoesLocationLatLongExists(latitude: Double, longitude: Double)
    {
        locationDao.DoesLocationLatLongExists(latitude, longitude)
    }

    @WorkerThread
    suspend fun DoesLocationNameExists(name: String?)
    {
        locationDao.DoesLocationNameExists(name)
    }
/*
    init {
        all = locationDao.all
        AddStaticData()
    }

    fun Insert(location: Location) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao.Insert(location) })
    }

    fun Update(location: Location) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao.Update(location) })
    }

    fun delete(location: Location) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao.delete(location) })
    }

    fun GetById(id: Int): Location? {
        return locationDao.GetById(id)
    }

    fun isLocationNameExists(name: String?): Boolean {
        return locationDao.isLocationNameExists(name)
    }

    fun isLocationLatitudeExists(latitude: Double): Boolean {
        return locationDao.isLocationLatitudeExists(latitude)
    }

    fun isLocationLongitudeExists(longitude: Double): Boolean {
        return locationDao.isLocationLongitudeExists(longitude)
    }

    private fun AddStaticData() {
        try {
            IsTimeUnitTypesExistsTask().execute().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/
/*
    inner class IsTimeUnitTypesExistsTask : AsyncTask<Double?, Void?, Boolean>() {
        protected override fun doInBackground(vararg p0: Double?): Boolean? {
            try {
                if (!staticDataDao.isOffsetTypesExists) {
                    for (enumType in OffsetTypeEnum.values()) {
                        val x = OffsetType(enumType.Id, enumType.Name)
                        //x.Id = enumType.Id
                        //x.Name = enumType.Name
                        staticDataDao.Insert(x)
                    }
                }

                //--------------------------
                if (!staticDataDao.isSolarTimeTypesExists) {
                    for (enumType in SolarTimeTypeEnum.values()) {
                        val x = SolarTimeType(enumType.Id, enumType.Name)
                        //x.Id = enumType.Id
                        //x.Name = enumType.Name
                        staticDataDao.Insert(x)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }

 */
}