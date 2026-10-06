package com.example.solar_alarm.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.createAlarm.AlarmScheduler
import com.example.solar_alarm.data.repositories.LocationRepository
import com.example.solar_alarm.data.repositories.SolarAlarmRepository
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.tables.SolarAlarm
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class RescheduleHelper @Inject constructor(
    private val solarAlarmRepository: SolarAlarmRepository,
    private val locationRepository: LocationRepository,
    private val solarTimeRepository: SolarTimeRepository
) {
    suspend fun rescheduleNext(context: Context, alarm: SolarAlarm) {
        if (!alarm.Recurring) return

        val nextDate = findNextDate(alarm)
        val location = locationRepository.getById(alarm.LocationId)
        val nextSolarTime = solarTimeRepository.getSolarTime(location, nextDate)

        if (nextSolarTime != null) {
            val updatedAlarm = alarm.copy().apply {
                SolarTimeId = nextSolarTime.Id
            }
            // We need to keep the ID of the original alarm record
            updatedAlarm.Id = alarm.Id
            
            solarAlarmRepository.update(updatedAlarm)
            
            AlarmScheduler(updatedAlarm, nextSolarTime, updatedAlarm.OffsetHours, updatedAlarm.OffsetMinutes).schedule(context)
        }
    }

    private fun findNextDate(alarm: SolarAlarm): LocalDate {
        var date = LocalDate.now().plusDays(1)
        
        // Loop up to 7 days to find the next active day
        for (i in 0..7) {
            val dayOfWeek = date.dayOfWeek.value // 1 (Mon) to 7 (Sun)
            val isActive = when (dayOfWeek) {
                1 -> alarm.Monday
                2 -> alarm.Tuesday
                3 -> alarm.Wednesday
                4 -> alarm.Thursday
                5 -> alarm.Friday
                6 -> alarm.Saturday
                7 -> alarm.Sunday
                else -> false
            }
            
            if (isActive) return date
            date = date.plusDays(1)
        }
        
        return LocalDate.now().plusDays(1) // Fallback to tomorrow
    }
}
