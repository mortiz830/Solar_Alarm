package com.example.solar_alarm.SolarTime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.R

class SolarTimeAdapter (private var solarTimes: List<SolarTime>) : RecyclerView.Adapter<SolarTimeViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarTimeViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.solartime_list_item, parent, false)

        return SolarTimeViewHolder(view)
    }

    override fun onBindViewHolder(solarTimeViewHolder: SolarTimeViewHolder, position: Int)
    {
        val solarTime = solarTimes[position]

        try
        {
            solarTimeViewHolder.solarTimeData1.text = "${solarTime.SolarDate} - Location ID: ${solarTime.LocationId}"
            solarTimeViewHolder.solarTimeData2.text = "Sunrise - ${solarTime.SunriseUtc}, Sunset - ${solarTime.SunsetUtc}, SolarNoon - ${solarTime.SolarNoonUtc}"
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = solarTimes.size

    fun UpdateSolarTimes(newSolarTimes: List<SolarTime>)
    {
        solarTimes = newSolarTimes
        notifyDataSetChanged()
    }
}