package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import com.example.solar_alarm.Data.Daos.TimeUnitTypeDao;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.TimeUnitType;

public class TimeUnitTypeRepository
{
    private TimeUnitTypeDao timeUnitTypeDao;

    public TimeUnitTypeRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        timeUnitTypeDao = db.timeUnitTypeDao();
    }

    public void Insert(TimeUnitType timeUnitType)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> timeUnitTypeDao.Insert(timeUnitType));
    }
}
