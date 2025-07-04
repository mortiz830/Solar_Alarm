package com.example.solar_alarm.Location

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.R

@RequiresApi(Build.VERSION_CODES.O)
class LocationListAdapter(private var locations: List<Location>) : RecyclerView.Adapter<LocationViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_list_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(locationViewHolder: LocationViewHolder, position: Int)
    {
        val location = locations[position]

        try
        {
            locationViewHolder.locationData1.text = "${location.Id} - ${location.Name}"
            locationViewHolder.locationData2.text = "${location.Latitude}, ${location.Longitude}"
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = locations.size

    fun UpdateLocations(newLocations: List<Location>)
    {
        locations = newLocations
        notifyDataSetChanged()
    }
}