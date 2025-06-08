package com.example.solar_alarm.Location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.R

class LocationAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_list_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(locationViewHolder: LocationViewHolder, position: Int)
    {
        val location = locations[position]

        locationViewHolder.locationData1.text = "${location.Id} - ${location.Name}"
        locationViewHolder.locationData2.text = "${location.Latitude}, ${location.Longitude}"
    }

    override fun getItemCount(): Int = locations.size

    fun updateLocations(newLocations: List<Location>)
    {
        locations = newLocations
        notifyDataSetChanged()
    }

}