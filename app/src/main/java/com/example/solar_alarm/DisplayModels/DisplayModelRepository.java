package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DisplayModelRepository
{
    private Application                            _Application;
    private List<SolarAlarmDisplayModel>           _SolarAlarmDisplayModels;
    private LiveData<List<SolarAlarmDisplayModel>> _LiveData;

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

            LiveData<List<SolarAlarm>> solarAlarms = new SolarAlarmRepository(_Application).getAll();

            _SolarAlarmDisplayModels = new ArrayList<>();

            List<SolarAlarm> values = solarAlarms.getValue();

            if (values == null)
            {
                values = new ArrayList<>();
            }

            for (SolarAlarm solarAlarm : values)
            {
                _SolarAlarmDisplayModels.add(new SolarAlarmDisplayModel(_Application, solarAlarm));
            }

            _LiveData = new LiveData<List<SolarAlarmDisplayModel>>() {
                @Override
                public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super List<SolarAlarmDisplayModel>> _SolarAlarmDisplayModels) {
                    super.observe(owner, _SolarAlarmDisplayModels);
                }
            };

            lock.unlock();
        }

        return _LiveData;
    }

    public void Refresh()
    {
        Lock lock = new ReentrantLock();
        lock.lock();

        _SolarAlarmDisplayModels = null;

        lock.unlock();
    }
}
