package com.example.solar_alarm.Location

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class LocationViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val locationData1 : TextView = itemView.findViewById(R.id.locationData1)
    val locationData2 : TextView = itemView.findViewById(R.id.locationData2)
}