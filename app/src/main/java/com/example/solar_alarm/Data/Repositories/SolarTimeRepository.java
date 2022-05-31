package com.example.solar_alarm.Data.Repositories;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Daos.SolarTimeDao;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.LocalDate;
import java.util.List;

public class SolarTimeRepository
{
    private SolarTimeDao solarTimeDao;
    private LiveData<List<SolarTime>> solarTimeLiveData;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTimeRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        solarTimeDao = db.solarTimeDao();
        solarTimeLiveData = solarTimeDao.getAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Insert(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.Insert(solarTime));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Update(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.Update(solarTime));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(SolarTime solarTime)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarTimeDao.delete(solarTime));
    }

    public boolean isLocationIDDatePairExists(int locationId, LocalDate date)
    {
        return solarTimeDao.isLocationIDDatePairExists(locationId, date);
    }

    public SolarTime getSolarTime(int locationId, LocalDate date)
    {
        return solarTimeDao.getSolarTime(locationId, date);
    }
    
    public LiveData<List<SolarTime>> getAll() {return solarTimeLiveData;}
}
