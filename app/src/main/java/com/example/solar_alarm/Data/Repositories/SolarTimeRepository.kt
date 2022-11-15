package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import java.time.LocalDate

class SolarTimeRepository @RequiresApi(api = Build.VERSION_CODES.O) constructor() : RepositoryBase() {
    private val solarTimeDao: SolarTimeDao?
    val all: LiveData<List<SolarTime?>?>?

    init {
        solarTimeDao = _SolarAlarmDatabase!!.solarTimeDao()
        all = solarTimeDao?.all
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun Insert(solarTime: SolarTime?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarTimeDao!!.Insert(solarTime) })
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun Update(solarTime: SolarTime?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarTimeDao!!.Update(solarTime) })
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun delete(solarTime: SolarTime?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarTimeDao!!.delete(solarTime) })
    }

    fun isLocationIDDatePairExists(locationId: Int, date: LocalDate?): Boolean {
        return solarTimeDao!!.isLocationIDDatePairExists(locationId, date)
    }

    fun getSolarTime(locationId: Int, date: LocalDate?): SolarTime? {
        return solarTimeDao!!.getSolarTime(locationId, date)
    }

    fun GetById(Id: Int): SolarTime? {
        return solarTimeDao!!.getById(Id)
    }
}