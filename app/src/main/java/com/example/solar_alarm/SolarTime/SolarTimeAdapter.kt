package com.example.solar_alarm.solarTime

// Repair: Fixed broken package/import lines
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.data.enums.SolarTimeTypeEnum
import com.example.solar_alarm.data.tables.SolarTime
import com.example.solar_alarm.databinding.SolartimeListItemBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class SolarTimeAdapter (private var solarTimes: List<SolarTime>) : RecyclerView.Adapter<SolarTimeViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarTimeViewHolder
    {
        val binding = SolartimeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SolarTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SolarTimeViewHolder, position: Int)
    {
        val solarTime = solarTimes[position]

        try
        {
            holder.binding.solarTimeData1.text = "${solarTime.Id} - ${solarTime.SolarDate} - Location ID: ${solarTime.LocationId}"
            holder.binding.solarTimeData2.text = "Sunrise - ${formatDateString(solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.Sunrise))}"
            holder.binding.solarTimeData3.text = "SolarNoon - ${formatDateString(solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon))}"
            holder.binding.solarTimeData4.text = "Sunset - ${formatDateString(solarTime.getLocalZonedDateTime(SolarTimeTypeEnum.Sunset))}"
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = solarTimes.size

    fun updateSolarTimes(newSolarTimes: List<SolarTime>)
    {
        solarTimes = newSolarTimes
        notifyDataSetChanged()
    }

    private fun formatDateString(zonedDateTime : ZonedDateTime) : String
    {
        val dateTimeFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd-MMM-uuuu hh:mm a")
        return zonedDateTime.format(dateTimeFormatter)
    }
}
