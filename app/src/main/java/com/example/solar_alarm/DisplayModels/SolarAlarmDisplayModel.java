package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Enums.TimeUnitTypeEnum;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.Data.Tables.Timezone;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SolarAlarmDisplayModel
{
    private Application             _Application;
    private SolarAlarm              _SolarAlarm;
    private LocationDisplayModel    _LocationDisplayModel;
    private SolarTimeDisplayModel   _SolarTimeDisplayModel;
    private Timezone                _Timezone;

    private HashMap<DayOfWeek, Boolean> _RecurrenceDays;

    public SolarAlarmDisplayModel(Application application, SolarAlarm solarAlarm)
    {
        _Application = application;
        _SolarAlarm  = solarAlarm;
    }

    public LocationDisplayModel GetLocation()
    {
        if (_LocationDisplayModel == null)
        {
            Location location = new LocationRepository(_Application).GetById(_SolarAlarm.LocationId);

            _LocationDisplayModel = new LocationDisplayModel(_Application, location) ;
        }

        return _LocationDisplayModel;
    }

    public SolarTimeDisplayModel GetSolarTime()
    {
        if (_SolarTimeDisplayModel == null)
        {
            SolarTime solarTime = new SolarTimeRepository(_Application).GetById(_SolarAlarm.LocationId);

            _SolarTimeDisplayModel = new SolarTimeDisplayModel(_Application, solarTime);
        }

        return _SolarTimeDisplayModel;
    }

    public String GetName() { return _SolarAlarm.Name; }

    public Map<DayOfWeek, Boolean> GetRecurrenceDays()
    {
        if (_RecurrenceDays == null)
        {
            _RecurrenceDays = new HashMap<DayOfWeek, Boolean>();

            _RecurrenceDays.put(DayOfWeek.MONDAY,    _SolarAlarm.Monday);
            _RecurrenceDays.put(DayOfWeek.TUESDAY,   _SolarAlarm.Tuesday);
            _RecurrenceDays.put(DayOfWeek.WEDNESDAY, _SolarAlarm.Wednesday);
            _RecurrenceDays.put(DayOfWeek.THURSDAY,  _SolarAlarm.Thursday);
            _RecurrenceDays.put(DayOfWeek.FRIDAY,    _SolarAlarm.Friday);
            _RecurrenceDays.put(DayOfWeek.SATURDAY,  _SolarAlarm.Saturday);
            _RecurrenceDays.put(DayOfWeek.SUNDAY,    _SolarAlarm.Sunday);
        }

        return _RecurrenceDays;
    }

    public boolean IsRecurring() { return _SolarAlarm.Recurring; }

    public OffsetTypeEnum GetOffsetType() { return OffsetTypeEnum.values()[_SolarAlarm.OffsetTypeId]; }

    public SolarTimeTypeEnum GetSolarTimeType() { return SolarTimeTypeEnum.values()[_SolarAlarm.SolarTimeTypeId]; }

    public TimeUnitTypeEnum GetTimeUnitType() { return TimeUnitTypeEnum.values()[_SolarAlarm.TimeUnitTypeId]; }

    public ZonedDateTime GetSetAlarmTime()
    {
        try {
            switch (GetSolarTimeType()) {
                case Sunrise:
                    return _SolarTimeDisplayModel.getSunrise();
                case Sunset:
                    return _SolarTimeDisplayModel.getSunset();
                case SolarNoon:
                    return _SolarTimeDisplayModel.getSolarNoon();
                case CivilTwilightBegin:
                    return _SolarTimeDisplayModel.getCivilTwilightBegin();
                case CivilTwilightEnd:
                    return _SolarTimeDisplayModel.getCivilTwilightEnd();
                case NauticalTwilightBegin:
                    return _SolarTimeDisplayModel.getNauticalTwilightBegin();
                case NauticalTwilightEnd:
                    return _SolarTimeDisplayModel.getNauticalTwilightEnd();
                case AstronomicalTwilightBegin:
                    return _SolarTimeDisplayModel.getAstronomicalTwilightBegin();
                case AstronomicalTwilightEnd:
                    return _SolarTimeDisplayModel.getAstronomicalTwilightEnd();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
