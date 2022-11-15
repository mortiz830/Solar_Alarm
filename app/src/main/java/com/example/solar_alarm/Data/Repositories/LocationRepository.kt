package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import android.os.AsyncTask
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Tables.*
import java.lang.Exception

@RequiresApi(api = Build.VERSION_CODES.O)
class LocationRepository : RepositoryBase() {
    private val locationDao: LocationDao?
    private val staticDataDao: StaticDataDao?
    val all: LiveData<List<Location?>?>?

    init {
        locationDao = _SolarAlarmDatabase!!.locationDao()
        staticDataDao = _SolarAlarmDatabase!!.staticDataDao()
        all = locationDao?.all
        AddStaticData()
    }

    fun Insert(location: Location?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao!!.Insert(location) })
    }

    fun Update(location: Location?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao!!.Update(location) })
    }

    fun delete(location: Location?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { locationDao!!.delete(location) })
    }

    fun GetById(id: Int): Location? {
        return locationDao!!.GetById(id)
    }

    fun isLocationNameExists(name: String?): Boolean {
        return locationDao!!.isLocationNameExists(name)
    }

    fun isLocationLatitudeExists(latitude: Double): Boolean {
        return locationDao!!.isLocationLatitudeExists(latitude)
    }

    fun isLocationLongitudeExists(longitude: Double): Boolean {
        return locationDao!!.isLocationLongitudeExists(longitude)
    }

    private fun AddStaticData() {
        try {
            IsTimeUnitTypesExistsTask().execute().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class IsTimeUnitTypesExistsTask : AsyncTask<Double?, Void?, Boolean>() {
        protected override fun doInBackground(vararg p0: Double?): Boolean? {
            try {
                if (!staticDataDao!!.isOffsetTypesExists) {
                    for (enumType in OffsetTypeEnum.values()) {
                        val x = OffsetType()
                        x.Id = enumType.Id
                        x.Name = enumType.Name
                        staticDataDao.Insert(x)
                    }
                }

                //--------------------------
                if (!staticDataDao.isSolarTimeTypesExists) {
                    for (enumType in SolarTimeTypeEnum.values()) {
                        val x = SolarTimeType()
                        x.Id = enumType.Id
                        x.Name = enumType.Name
                        staticDataDao.Insert(x)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
    }
}