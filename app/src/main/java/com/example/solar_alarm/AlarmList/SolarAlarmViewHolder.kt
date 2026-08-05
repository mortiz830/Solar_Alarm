package com.example.solar_alarm.alarmList

// Repair: Fixed broken package/import lines
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class SolarAlarmViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
{

    val alarmName     : TextView = itemView.findViewById(R.id.alarmName)
    val alarmDateTime : TextView = itemView.findViewById(R.id.alarmDateTime)
    val locationName  : TextView = itemView.findViewById(R.id.alarmName)
}