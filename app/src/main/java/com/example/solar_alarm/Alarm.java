package com.example.solar_alarm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Alarm implements Serializable
{
    private static ArrayList<Alarm> contacts = new ArrayList<Alarm>(1);
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
    public static ArrayList<Alarm> createAlarmList() {


        LocalTime time = LocalTime.now();
        contacts.add(new Alarm(time, "New"));

        return contacts;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Alarm> updateAlarmList(LocalTime l1, String s1){

        //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        //LocalTime time = LocalTime.parse(l1, formatter);
        contacts.add(new Alarm(l1, s1));

        return contacts;

    }
}
