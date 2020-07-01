package com.example.solar_alarm;

import java.time.LocalTime;

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
}
