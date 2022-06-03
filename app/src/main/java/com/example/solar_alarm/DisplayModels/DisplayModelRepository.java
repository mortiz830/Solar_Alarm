package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Tables.SolarAlarm;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DisplayModelRepository
{
    private Application _Application;

    public DisplayModelRepository(Application application)
    {
        _Application = application;
    }

    public List<SolarAlarmDisplayModel> GetSolarAlarmDisplayModels()
    {
        List<SolarAlarmDisplayModel> solarAlarmDisplayModels = new ArrayList<>();
        LiveData<List<SolarAlarm>>   solarAlarms             = new SolarAlarmRepository(_Application).getAll();

        for (SolarAlarm solarAlarm : solarAlarms.getValue())
        {
            solarAlarmDisplayModels.add(new SolarAlarmDisplayModel(_Application, solarAlarm));
        }

        return solarAlarmDisplayModels;
    }
}
