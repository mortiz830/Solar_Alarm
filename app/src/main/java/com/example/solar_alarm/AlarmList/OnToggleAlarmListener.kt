package com.example.solar_alarm.alarmList

import com.example.solar_alarm.data.tables.SolarAlarmWithDetails

interface OnToggleAlarmListener {
    fun onToggle(solarAlarmWithDetails: SolarAlarmWithDetails, isChecked: Boolean)
}
