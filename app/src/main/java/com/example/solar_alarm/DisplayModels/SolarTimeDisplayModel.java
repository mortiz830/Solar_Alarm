package com.example.solar_alarm.DisplayModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.ZonedDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SolarTimeDisplayModel {

    private Application _Application;
    private SolarTime _SolarTime;

    public SolarTimeDisplayModel(Application application, SolarTime solarTime)
    {
        _Application = application;
        _SolarTime  = solarTime;
    }


    public ZonedDateTime getSunrise() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise);}
    public ZonedDateTime getSunset() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset);}
    public ZonedDateTime getSolarNoon() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon);}
    public ZonedDateTime getCivilTwilightBegin() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightBegin);}
    public ZonedDateTime getCivilTwilightEnd() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.CivilTwilightEnd);}
    public ZonedDateTime getNauticalTwilightBegin() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightBegin);}
    public ZonedDateTime getNauticalTwilightEnd() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.NauticalTwilightEnd);}
    public ZonedDateTime getAstronomicalTwilightBegin() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightBegin);}
    public ZonedDateTime getAstronomicalTwilightEnd() throws Exception {return _SolarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.AstronomicalTwilightEnd);}
}
