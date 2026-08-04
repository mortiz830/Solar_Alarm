package com.example.solar_alarm.alarmList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.data.Converters
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import com.example.solar_alarm.databinding.SolarAlarmListItemBinding

class AlarmViewHolder(private val binding: SolarAlarmListItemBinding, private val listener: OnToggleAlarmListener) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun bind(solarAlarmWithDetails: SolarAlarmWithDetails) {
        val solarAlarm = solarAlarmWithDetails.solarAlarm
        val solarTime = solarAlarmWithDetails.solarTime
        
        val zonedDateTime = solarTime.getLocalZonedDateTime(solarAlarm.SolarTimeTypeId)
        val alarmText = Converters.toTimeString(zonedDateTime)
        
        binding.alarmDateTime.text = "${alarmText[0]} ${alarmText[1]}"
        binding.alarmName.text = String.format("%s | %d", solarAlarm.Name, solarAlarm.Id)

        binding.parentLayout.setOnClickListener {
            // Add click logic if needed
        }
    }
}
