package com.example.solar_alarm.AlarmList;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.solar_alarm.DisplayModels.DisplayModelRepository;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmListViewModel extends AndroidViewModel {
    private DisplayModelRepository _displayModelRepository;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);

        _displayModelRepository = new DisplayModelRepository(application);

    }
}
