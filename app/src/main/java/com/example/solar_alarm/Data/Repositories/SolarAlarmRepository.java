package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.List;

public class SolarAlarmRepository
{
    private SolarAlarmDao solarAlarmDao;
    private LiveData<List<SolarAlarm>> solarAlarmLiveData;

    public SolarAlarmRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        solarAlarmDao = db.solarAlarmDao();
        solarAlarmLiveData = solarAlarmDao.getAll();
    }

    public void Insert(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.Insert(solarAlarm));
    }

    public void Update(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.Update(solarAlarm));
    }

    public void delete(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.delete(solarAlarm));
    }

    public LiveData<List<SolarAlarm>> getAll() {return solarAlarmLiveData;}
}
