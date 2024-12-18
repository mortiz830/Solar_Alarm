package com.example.solar_alarm.Location

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.AlarmList.AlarmViewHolder
import com.example.solar_alarm.AlarmList.OnToggleAlarmListener
import com.example.solar_alarm.Data.ViewModels.LocationViewModel

class LocationRecycleViewAdapter(private val onToggleAlarmListener : OnToggleAlarmListener,
                                 private val locationViewModel     : LocationViewModel)
    : RecyclerView.Adapter<AlarmViewHolder>()
{

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder
    {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int
    {
        locationViewModel.All.value.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int)
    {
        TODO("Not yet implemented")
    }
}