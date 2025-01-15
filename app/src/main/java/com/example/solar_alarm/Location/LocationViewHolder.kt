package com.example.solar_alarm.Location

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.R

class LocationViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val locationID : TextView = itemView.findViewById(R.id.item_location_location_ID)
    val locationName : TextView = itemView.findViewById(R.id.item_location_location_name)
    val locationLatitude : TextView = itemView.findViewById(R.id.item_location_latitude_text)
    val locationLongitude : TextView = itemView.findViewById(R.id.item_location_longitude_text)
}