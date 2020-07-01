package com.example.solar_alarm;

import java.time.LocalTime;

public class Alarm
{
    private LocalTime AlarmTime;

    public Alarm(LocalTime alarmTime)
    {
        AlarmTime = alarmTime;
    }

    public LocalTime GetAlarmTime()
    {
        return AlarmTime;
    }
}
