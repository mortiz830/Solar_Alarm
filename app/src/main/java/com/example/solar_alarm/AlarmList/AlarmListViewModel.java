package com.example.solar_alarm.AlarmList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.Data.AlarmRepository;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.util.List;

public class AlarmListViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private LiveData<List<Alarm>> alarmsLiveData;

    private SolarTimeRepository solarTimeRepository;
    private SolarAlarmRepository solarAlarmRepository;
    private LocationRepository locationRepository;

    private LiveData<List<SolarAlarm>> solarAlarmLiveData;
    private LiveData<List<SolarTime>> solarTimeLiveData;
    private LiveData<List<Location>> locationLiveData;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
        alarmsLiveData = alarmRepository.getAlarmsLiveData();

        solarAlarmRepository = new SolarAlarmRepository(application);
        solarAlarmLiveData = solarAlarmRepository.getAll();

        solarTimeRepository = new SolarTimeRepository(application);
        solarTimeLiveData = solarTimeRepository.getAll();

        locationRepository = new LocationRepository(application);
        locationLiveData = locationRepository.getAll();
    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }

    public void delete(Alarm alarm) {
        alarmRepository.delete(alarm);
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }

    public LiveData<List<SolarAlarm>> getSolarAlarmLiveData() { return solarAlarmLiveData; }

    public LiveData<List<SolarTime>> getSolarTimeLiveData() { return solarTimeLiveData; }

    public LiveData<List<Location>> getLocationLiveData() { return locationLiveData; }


}
