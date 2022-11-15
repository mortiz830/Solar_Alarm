package com.example.solar_alarm.Data

import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import androidx.lifecycle.LiveData

@RequiresApi(Build.VERSION_CODES.O)
class AlarmRepository(application: Application) {
    private val alarmDao: AlarmDao?
    val alarmsLiveData: LiveData<List<Alarm?>?>?

    init {
        val db: AlarmDatabase? = AlarmDatabase.Companion.getDatabase(application)
        alarmDao = db?.alarmDao()
        alarmsLiveData = alarmDao?.alarms
    }

    fun insert(alarm: Alarm?) {
        AlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { alarmDao!!.insert(alarm) })
    }

    fun update(alarm: Alarm?) {
        AlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { alarmDao!!.update(alarm) })
    }

    fun delete(alarm: Alarm?) {
        AlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { alarmDao!!.delete(alarm) })
    }

    fun deleteAll() {
        AlarmDatabase.Companion.databaseWriteExecutor.execute(Runnable { alarmDao!!.deleteAll() })
    }
}