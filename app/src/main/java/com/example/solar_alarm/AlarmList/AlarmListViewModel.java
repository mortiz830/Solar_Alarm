package com.example.solar_alarm.AlarmList;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.AlarmDisplayData;
import com.example.solar_alarm.Data.AlarmDisplayDataDao;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmListViewModel extends AndroidViewModel
{
    private SolarTimeRepository solarTimeRepository;
    private SolarAlarmRepository solarAlarmRepository;
    private LocationRepository locationRepository;

    private LiveData<List<SolarAlarm>> solarAlarmLiveData;
    private LiveData<List<SolarTime>> solarTimeLiveData;
    private LiveData<List<Location>> locationLiveData;
    private AlarmDisplayDataDao alarmDisplayDataDao;
    private LiveData<List<AlarmDisplayData>> alarmDisplayLiveData;

    //String rawQuery = "SELECT DISTINCT " + Location.Id + ","

    public AlarmListViewModel(@NonNull Application application)
    {
        super(application);

        // to be deleted
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        alarmDisplayLiveData = db.alarmDisplayDataDao().loadAlarmData();

        solarAlarmRepository = new SolarAlarmRepository();

        solarAlarmLiveData = solarAlarmRepository.getAll();

        solarTimeLiveData = new SolarTimeRepository().getAll();

        locationLiveData = new LocationRepository().getAll();
    }

    public void update(SolarAlarm alarm) {
        solarAlarmRepository.Update(alarm);
    }

    public void delete(SolarAlarm alarm) {
        solarAlarmRepository.Delete(alarm);
    }

    public LiveData<List<SolarAlarm>> getAlarmsLiveData() {
        return solarAlarmLiveData;
    }

    public LiveData<List<AlarmDisplayData>> getAlarmDisplayLiveData() { return alarmDisplayLiveData; }

    public LiveData<List<SolarAlarm>> getSolarAlarmLiveData() { return solarAlarmLiveData; }

    public LiveData<List<SolarTime>> getSolarTimeLiveData() { return solarTimeLiveData; }

    public LiveData<List<Location>> getLocationLiveData() { return locationLiveData; }

}
