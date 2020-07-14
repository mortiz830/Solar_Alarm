package com.example.solar_alarm;

import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.util.ArrayList;

public class Alarm
{
    private LocalTime AlarmTime;
    private String    AlarmName;

    public Alarm(LocalTime alarmTime, String alarmName)
    {
        AlarmTime = alarmTime;
        AlarmName = alarmName;
    }

    public LocalTime GetAlarmTime()
    {
        return AlarmTime;
    }

    public String GetAlarmName()
    {
        return AlarmName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Alarm> createContactsList(int numAlarms) {
        ArrayList<Alarm> contacts = new ArrayList<Alarm>();
        LocalTime now = LocalTime.now();

        for (int i = 0; i <= numAlarms - 1; i++) {
            contacts.add(new Alarm(now.plusHours(i*24), "Alarm " + i));
        }

        return contacts;
    }
}
