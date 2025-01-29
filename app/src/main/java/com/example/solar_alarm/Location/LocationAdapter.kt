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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int)
    {
        val location : Location = locations[position]
        holder.locationName.text = location.Name
        holder.locationID.text = location.Id.toString()
        holder.locationLatitude.text = location.Latitude.toString()
        holder.locationLongitude.text = location.Longitude.toString()
    }

    override fun getItemCount(): Int = locations.size

    fun updateLocations(newLocations: List<Location>)
    {
        locations = newLocations
        notifyDataSetChanged()
    }

}