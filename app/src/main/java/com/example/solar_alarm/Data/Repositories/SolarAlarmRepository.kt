package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Tables.SolarAlarm
import androidx.lifecycle.LiveData
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.Data.Daos.SolarAlarmDao

@RequiresApi(api = Build.VERSION_CODES.O)
class SolarAlarmRepository @RequiresApi(api = Build.VERSION_CODES.O) constructor() : RepositoryBase()
{
    private val solarAlarmDao: SolarAlarmDao = _SolarAlarmDatabase.solarAlarmDao()

    val all: LiveData<List<SolarAlarm?>?>? = solarAlarmDao.all

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun Insert(solarAlarm: SolarAlarm?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarAlarmDao!!.Insert(solarAlarm) })
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun Update(solarAlarm: SolarAlarm?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarAlarmDao!!.Update(solarAlarm) })
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun Delete(solarAlarm: SolarAlarm?) {
        SolarAlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { solarAlarmDao!!.delete(solarAlarm) })
    }

    suspend fun isSolarAlarmNameLocationIDExists(name: String, locationId: Int): Boolean
    {
        return solarAlarmDao.isSolarAlarmNameLocationIDPairExists(name, locationId)
    }
}