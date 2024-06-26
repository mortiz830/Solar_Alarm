package com.example.solar_alarm

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.Data.Repositories.LocationRepository
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.SolarAlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmApp : Application()
{
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val solarAlarmDatabase by lazy { SolarAlarmDatabase.getDatabase(this, applicationScope) }

    val locationRepository   by lazy { LocationRepository(solarAlarmDatabase.locationDao())   }
    val solarTimeRepository  by lazy { SolarTimeRepository(solarAlarmDatabase.solarTimeDao()) }
    val solarAlarmRepository by lazy { SolarAlarmRepository(solarAlarmDatabase.solarAlarmDao()) }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        private var context: Context? = null
        fun GetAppContext(): Context? {
            return context
        }
    }
}