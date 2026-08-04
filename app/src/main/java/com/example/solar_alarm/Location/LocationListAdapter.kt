package com.example.solar_alarm.location

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.data.tables.Location
import com.example.solar_alarm.databinding.LocationListItemBinding

@RequiresApi(Build.VERSION_CODES.O)
class LocationListAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder
    {
        val binding = LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int)
    {
        val location = locations[position]
        holder.binding.locationData1.text = "${location.Id} - ${location.Name}"
        holder.binding.locationData2.text = "${location.Latitude}, ${location.Longitude}"
    }

    override fun getItemCount(): Int = locations.size

    fun UpdateLocations(newLocations: List<Location>)
    {
        locations = newLocations
        notifyDataSetChanged()
    }
}
