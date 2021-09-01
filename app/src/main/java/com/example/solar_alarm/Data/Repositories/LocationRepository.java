package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.Location;

import java.util.List;

public class LocationRepository
{
    private LocationDao locationDao;
    private LiveData<List<Location>> locationsLiveData;

    public LocationRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        locationDao = db.locationDao();
        locationsLiveData = locationDao.getAll();
    }

    public void Insert(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.Insert(location));
    }

    public void Update(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.Update(location));
    }

    public void delete(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.delete(location));
    }

    public LiveData<List<Location>> getAll() { return locationsLiveData; }

    public boolean isLocationExists(String name)
    {
        if (locationDao.isLocationExists(name))
            return true;
        else
            return false;
    }
}
