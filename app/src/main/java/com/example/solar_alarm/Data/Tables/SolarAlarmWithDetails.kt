package com.example.solar_alarm.data.tables

// Repair: Fixed broken package/import lines
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
