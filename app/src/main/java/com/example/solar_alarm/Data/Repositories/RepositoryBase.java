package com.example.solar_alarm.Data.Repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.SolarAlarmApp;

@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class RepositoryBase
{
    protected SolarAlarmApp      _SolarAlarmApp      = new SolarAlarmApp();
    protected SolarAlarmDatabase _SolarAlarmDatabase = SolarAlarmDatabase.getDatabase(_SolarAlarmApp);
}
