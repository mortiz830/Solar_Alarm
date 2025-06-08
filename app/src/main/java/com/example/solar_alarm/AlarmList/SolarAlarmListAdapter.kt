package com.example.solar_alarm.AlarmList

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmListAdapter(private var solarAlarms: List<SolarAlarm>) : RecyclerView.Adapter<SolarAlarmViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarAlarmViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.solar_alarm_list_item, parent, false)
        return SolarAlarmViewHolder(view)
    }

    override fun onBindViewHolder(solarAlarmViewHolder: SolarAlarmViewHolder, position: Int)
    {
        val solarAlarm : SolarAlarm = solarAlarms[position]

        try
        {
            val zonedDateTime = solarAlarm.solarTime.GetLocalZonedDateTime(solarAlarm.SolarTimeTypeId)
            val hour          = if (zonedDateTime.hour > 12)  zonedDateTime.hour - 12 else zonedDateTime.hour
            val ampm          = if (zonedDateTime.hour > 12)  "PM" else "AM"

            solarAlarmViewHolder.alarmName.text     = "${solarAlarm.Id} - ${solarAlarm.Name} - ${solarAlarm.OffsetTypeId.Name} ${solarAlarm.SolarTimeTypeId.Name} - ${solarAlarm.location.Name}"
            solarAlarmViewHolder.alarmDateTime.text = "${zonedDateTime.dayOfWeek} ${zonedDateTime.dayOfMonth} ${zonedDateTime.month} ${zonedDateTime.year} ${hour}:${zonedDateTime.minute} $ampm"
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = solarAlarms.size

    fun UpdateSolarAlarms(newSolarAlarms: List<SolarAlarm>)
    {
        solarAlarms = newSolarAlarms
        notifyDataSetChanged()
    }
}