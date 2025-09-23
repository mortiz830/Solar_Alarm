package com.example.solar_alarm.SolarTime

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum
import com.example.solar_alarm.Data.Tables.SolarTime
import com.example.solar_alarm.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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
            solarTimeViewHolder.solarTimeData1.text = "${solarTime.Id} - ${solarTime.SolarDate} - Location ID: ${solarTime.LocationId}"
            solarTimeViewHolder.solarTimeData2.text = "Sunrise - ${FormatDateString(solarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise))}"
            solarTimeViewHolder.solarTimeData3.text = "SolarNoon - ${FormatDateString(solarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon))}"
            solarTimeViewHolder.solarTimeData4.text = "Sunset - ${FormatDateString(solarTime.GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset))}"
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

    private fun FormatDateString(zonedDateTime : ZonedDateTime) : String
    {
        val dateTimeFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu hh:mm a")
        return zonedDateTime.format(dateTimeFormatter)
    }
}
