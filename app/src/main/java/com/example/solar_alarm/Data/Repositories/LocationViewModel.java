package com.example.solar_alarm.Data.Repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Tables.Location;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private LocationRepository LocationRepository;
    private LiveData<List<Location>> locationsLiveData;

    public LocationViewModel(@NonNull Application application) {
        super(application);

        LocationRepository = new LocationRepository(application);
        locationsLiveData = LocationRepository.getAll();
    }

    public void insert(Location location) {LocationRepository.Insert(location);}

    public void update(Location location) {
        LocationRepository.Update(location);
    }

    public void delete(Location location) {
        LocationRepository.delete(location);
    }

    public LiveData<List<Location>> getLocationsLiveData() {
        return locationsLiveData;
    }
}
