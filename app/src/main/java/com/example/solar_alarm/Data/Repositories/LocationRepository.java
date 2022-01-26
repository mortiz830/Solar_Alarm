package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Daos.LocationDao;
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

    public boolean isLocationNameExists(String name)
    {
        return locationDao.isLocationNameExists(name);
    }

    public boolean isLocationLatitudeExists(double latitude)
    {
        return locationDao.isLocationLatitudeExists(latitude);
    }

    public boolean isLocationLongitudeExists(double longitude)
    {
        return locationDao.isLocationLongitudeExists(longitude);
    }
}
