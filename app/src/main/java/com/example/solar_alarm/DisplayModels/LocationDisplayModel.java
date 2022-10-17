package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Tables.Location;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationDisplayModel
{
    private Application          _Application;
    private Location             _Location;

    public LocationDisplayModel(Application application, Location location)
    {
        _Application = application;
        _Location    = location;
    }

    public int GetId() { return _Location.Id; }

    public String GetName() { return _Location.Name; }

    public double GetLatitude() { return _Location.Latitude; }

    public double GetLongitude() { return _Location.Longitude; }
}
