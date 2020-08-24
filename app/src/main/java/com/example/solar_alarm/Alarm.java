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
    private String AlarmTime;
    private String    AlarmName;

    public Alarm(String alarmTime, String alarmName)
    {
        AlarmTime = alarmTime;
        AlarmName = alarmName;
    }

    public String GetAlarmTime()
    {
        return AlarmTime;
    }

    public String GetAlarmName()
    {
        return AlarmName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Alarm> createAlarmList() {


        String time = "";
        contacts.add(new Alarm(time, "New"));

        return contacts;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Alarm> updateAlarmList(String l1, String s1){

        //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        //LocalTime time = LocalTime.parse(l1, formatter);
        contacts.add(new Alarm(l1, s1));

        return contacts;

    }
}
