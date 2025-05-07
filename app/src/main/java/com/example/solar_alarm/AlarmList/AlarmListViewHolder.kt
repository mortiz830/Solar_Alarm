package com.example.solar_alarm.AlarmList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class SolarAlarmViewHolder  (itemView: View) : RecyclerView.ViewHolder(itemView)
{

    val alarmTime : TextView = itemView.findViewById(R.id.item_alarm_time)
    /*
    *
    val locationName : TextView = itemView.findViewById(R.id.item_location_location_name)
    val locationLatitude : TextView = itemView.findViewById(R.id.item_location_latitude)
    val locationLongitude : TextView = itemView.findViewById(R.id.item_location_longitude)
    * */
}