package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.Location;

import java.util.List;

public class LocationRepository {
    private LocationDao locationDao;
    private LiveData<List<Location>> locationsLiveData;

    public LocationRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        locationDao = db.locationDao();
        locationsLiveData = locationDao.getAll();
    }

    public LocationRepository()
    {
        Location l1 = new Location();
        Location l2 = new Location();
        Location l3 = new Location();
    }
}
