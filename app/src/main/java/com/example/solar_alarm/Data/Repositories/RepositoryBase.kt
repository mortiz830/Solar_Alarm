package com.example.solar_alarm.Data.Repositories

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.SolarAlarmDatabase
import com.example.solar_alarm.SolarAlarmApp

@RequiresApi(api = Build.VERSION_CODES.O)
abstract class RepositoryBase {
    protected var _SolarAlarmApp = SolarAlarmApp()
    protected var _SolarAlarmDatabase: SolarAlarmDatabase = SolarAlarmDatabase.Companion.getDatabase(_SolarAlarmApp)
}