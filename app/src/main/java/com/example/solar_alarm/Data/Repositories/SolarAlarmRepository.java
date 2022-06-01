package com.example.solar_alarm.Data.Repositories;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Daos.SolarAlarmDao;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.List;

public class SolarAlarmRepository
{
    private final SolarAlarmDao solarAlarmDao;
    private final LiveData<List<SolarAlarm>> solarAlarmLiveData;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarAlarmRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        solarAlarmDao = db.solarAlarmDao();
        solarAlarmLiveData = solarAlarmDao.getAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Insert(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.Insert(solarAlarm));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Update(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.Update(solarAlarm));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(SolarAlarm solarAlarm)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> solarAlarmDao.delete(solarAlarm));
    }

    public boolean isSolarAlarmNameLocationIDExists(String name, int locationId)
    {
        return solarAlarmDao.isSolarAlarmNameLocationIDPairExists(name, locationId);
    }
    public LiveData<List<SolarAlarm>> getAll() {return solarAlarmLiveData;}
}
