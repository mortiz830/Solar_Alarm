package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Repositories.TimezoneRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.Timezone;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationDisplayModel
{
    private Application          _Application;
    private Location             _Location;
    private TimezoneDisplayModel _TimezoneDisplayModel;

    public LocationDisplayModel(Application application, Location location)
    {
        _Application = application;
        _Location    = location;
    }

    public int GetId() { return _Location.Id; }

    public String GetName() { return _Location.Name; }

    public TimezoneDisplayModel GetTimezone()
    {
        if (_TimezoneDisplayModel == null)
        {
            Timezone timezone = new TimezoneRepository(_Application).GetById(_Location.Id);

            _TimezoneDisplayModel = new TimezoneDisplayModel(timezone);
        }

        return _TimezoneDisplayModel;
    }

    public double GetLatitude() { return _Location.Latitude; }

    public double GetLongitude() { return _Location.Longitude; }
}
