package com.example.solar_alarm;

import android.app.Application;
import android.content.Context;

public class SolarAlarmApp extends Application
{
    private static Context context;

    public void onCreate()
    {
        super.onCreate();
        SolarAlarmApp.context = getApplicationContext();
    }

    public static Context GetAppContext()
    {
        return SolarAlarmApp.context;
    }
}
