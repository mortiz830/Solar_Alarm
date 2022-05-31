package com.example.solar_alarm.CreateAlarm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;

public final class ZonedDateTimeUtil
{
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static final String toDay(ZonedDateTime zonedDateTime) throws Exception
    {
        //String time = zonedDateTime.getHour() + ":" + zonedDateTime.getMinute() + " " + zonedDateTime.getZone().getDisplayName(TextStyle.FULL, );
        return null;
    }
}
