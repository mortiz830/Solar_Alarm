package com.example.solar_alarm.AlarmList


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
class AlarmListAdapter(private var solarAlarms: List<SolarAlarm>) : RecyclerView.Adapter<SolarAlarmViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarAlarmViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return SolarAlarmViewHolder(view)
    }


    override fun onBindViewHolder(solarAlarmViewHolder: SolarAlarmViewHolder, position: Int)
    {
        val solarAlarm : SolarAlarm = solarAlarms[position]

        try
        {
            runBlocking {
                val solarTime = solarAlarm.GetSolarTime()

                if (solarTime != null)
                {
                    solarAlarmViewHolder.alarmTime.text = solarTime.GetLocalZonedDateTime(solarAlarm.SolarTimeTypeId).toString()
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = solarAlarms.size

    fun updateSolarAlarms(newSolarAlarms: List<SolarAlarm>)
    {
        solarAlarms = newSolarAlarms
        notifyDataSetChanged()
    }

}