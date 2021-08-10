package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.Timezone;

import java.util.List;

public class TimezoneRepository
{
    private TimezoneDao timezoneDao;
    private LiveData<List<Timezone>> timezonesLiveData;

    public TimezoneRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        timezoneDao = db.timezoneDao();
        timezonesLiveData = timezoneDao.getAll();
    }

    public void Insert(Timezone timezone)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> timezoneDao.Insert(timezone));
    }

    public void Update(Timezone timezone)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> timezoneDao.Update(timezone));
    }

    public void delete(Timezone timezone)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> timezoneDao.delete(timezone));
    }

    public LiveData<List<Timezone>> getAll() { return timezonesLiveData; }
}
