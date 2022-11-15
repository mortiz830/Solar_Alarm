package com.example.solar_alarm.AlarmList

import com.example.solar_alarm.Data.Alarm

interface OnToggleAlarmListener {
    fun onToggle(alarm: Alarm)
}