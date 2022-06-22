package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DisplayModelRepository
{
    private Application _Application;
    private List<SolarAlarmDisplayModel> _SolarAlarmDisplayModels;

    public DisplayModelRepository(Application application)
    {
        _Application = application;
    }

    public LiveData<List<SolarAlarmDisplayModel>> GetSolarAlarmDisplayModels()
    {
        if (_SolarAlarmDisplayModels == null)
        {
            Lock lock = new ReentrantLock();
            lock.lock();

            _SolarAlarmDisplayModels = new ArrayList<>();
            LiveData<List<SolarAlarm>> solarAlarms = new SolarAlarmRepository(_Application).getAll();

            for (SolarAlarm solarAlarm : solarAlarms.getValue())
            {
                _SolarAlarmDisplayModels.add(new SolarAlarmDisplayModel(_Application, solarAlarm));
            }

            lock.unlock();
        }

        return (LiveData<List<SolarAlarmDisplayModel>>) _SolarAlarmDisplayModels;
    }

    public void Refresh()
    {
        Lock lock = new ReentrantLock();
        lock.lock();

        _SolarAlarmDisplayModels = null;

        lock.unlock();
    }
}
