package com.example.solar_alarm.alarmList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class SolarAlarmViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
{

    val alarmName     : TextView = itemView.findViewById(R.id.alarmName)
    val alarmDateTime : TextView = itemView.findViewById(R.id.alarmDateTime)
    val locationName  : TextView = itemView.findViewById(R.id.alarmName)
    /*
    *
    val locationName : TextView = itemView.findViewById(R.id.item_location_location_name)
    val locationLatitude : TextView = itemView.findViewById(R.id.item_location_latitude)
    val locationLongitude : TextView = itemView.findViewById(R.id.item_location_longitude)
    * */
}