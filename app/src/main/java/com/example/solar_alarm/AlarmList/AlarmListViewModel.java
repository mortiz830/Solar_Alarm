package com.example.solar_alarm.AlarmList;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.DisplayModels.DisplayModelRepository;
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmListViewModel extends AndroidViewModel
{
    private DisplayModelRepository _displayModelRepository;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);

        _displayModelRepository = new DisplayModelRepository(application);

        _displayModelRepository.GetSolarAlarmDisplayModels();

    }

    public LiveData<List<SolarAlarmDisplayModel>> getAlarmDisplayLiveData()
    {
        return _displayModelRepository.GetSolarAlarmDisplayModels();
    }
}
