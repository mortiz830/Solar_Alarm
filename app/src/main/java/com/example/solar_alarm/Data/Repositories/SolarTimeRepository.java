package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.LocalDate;
import java.util.List;

public class SolarTimeRepository
{
    private SolarTimeDao solarTimeDao;
    private LiveData<List<SolarTime>> solarTimeLiveData;

    public SolarTimeRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        solarTimeDao = db.solarTimeDao();
        solarTimeLiveData = solarTimeDao.getAll();
    }

    public void Insert(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.Insert(solarTime));
    }

    public void Update(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.Update(solarTime));
    }

    public void delete(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.delete(solarTime));
    }

    public boolean isLocationIDDatePairExists(int locationId, LocalDate date)
    {
        return solarTimeDao.isLocationIDDatePairExists(locationId, date);
    }

    
    public LiveData<List<SolarTime>> getAll() {return solarTimeLiveData;}
}
