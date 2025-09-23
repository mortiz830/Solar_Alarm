package com.example.solar_alarm.SolarTime

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class SolarTimeViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val solarTimeData1 : TextView = itemView.findViewById(R.id.solarTimeData1)
    val solarTimeData2 : TextView = itemView.findViewById(R.id.solarTimeData2)
}
