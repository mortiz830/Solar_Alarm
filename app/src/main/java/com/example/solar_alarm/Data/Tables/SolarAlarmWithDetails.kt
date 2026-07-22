package com.example.solar_alarm.Data.Tables

import androidx.room.Embedded
import androidx.room.Relation

data class SolarAlarmWithDetails(
    @Embedded val solarAlarm: SolarAlarm,
    
    @Relation(
        parentColumn = "LocationId",
        entityColumn = "Id"
    )
    val location: Location,
    
    @Relation(
        parentColumn = "SolarTimeId",
        entityColumn = "Id"
    )
    val solarTime: SolarTime
)
