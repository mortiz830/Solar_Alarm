package com.example.solar_alarm.AlarmList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Converters
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.databinding.SolarAlarmListItemBinding

class AlarmViewHolder(private val binding: SolarAlarmListItemBinding, private val listener: OnToggleAlarmListener) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun bind(solarAlarm: SolarAlarm) {
        val zonedDateTime = solarAlarm.SolarTimeTypeId?.let { solarAlarm.solarTime.GetLocalZonedDateTime(it) }
        val alarmText = zonedDateTime?.let { Converters.toTimeString(it) }
        
        if (alarmText != null) {
            binding.alarmDateTime.text = alarmText[0] // Note: In original code, both date and time were set to alarmDateTime.
            // If you have a separate view for time, use it here.
        }
        
        binding.alarmName.text = String.format("%s | %d", solarAlarm.Name, solarAlarm.Id)

        binding.parentLayout.setOnClickListener {
            // Add click logic if needed
        }
    }
}
