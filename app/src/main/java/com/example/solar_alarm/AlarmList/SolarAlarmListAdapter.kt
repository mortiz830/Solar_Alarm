package com.example.solar_alarm.alarmList

// Repair: Fixed broken package/import lines
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.data.tables.SolarAlarmWithDetails
import com.example.solar_alarm.R
import java.time.DayOfWeek
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
class SolarAlarmListAdapter(private var solarAlarms: List<SolarAlarmWithDetails>) : RecyclerView.Adapter<SolarAlarmViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarAlarmViewHolder
    {
        val view = LayoutInflater.from(parent.context)
                                 .inflate(R.layout.solar_alarm_list_item, parent, false)

        return SolarAlarmViewHolder(view)
    }

    override fun onBindViewHolder(solarAlarmViewHolder: SolarAlarmViewHolder, position: Int)
    {
        val solarAlarmWithDetails = solarAlarms[position]
        val solarAlarm = solarAlarmWithDetails.solarAlarm
        val solarTime = solarAlarmWithDetails.solarTime
        val location = solarAlarmWithDetails.location

        try
        {
            val zonedDateTime = solarTime.getLocalZonedDateTime(solarAlarm.SolarTimeTypeId)
            val hour          = if (zonedDateTime.hour > 12)  zonedDateTime.hour - 12 else zonedDateTime.hour
            val ampm          = if (zonedDateTime.hour > 12)  "PM" else "AM"
            val shorDay       = getShortDay(zonedDateTime.dayOfWeek)
            val shortMonth    = getShortMonth(zonedDateTime.month)

            "${solarAlarm.Id} - ${solarAlarm.Name} - ${solarAlarm.OffsetTypeId.Name} ${solarAlarm.SolarTimeTypeId.Name} - ${location.Name}".also { solarAlarmViewHolder.alarmName.text = it }
            "${shorDay} ${zonedDateTime.dayOfMonth}-$shortMonth-${zonedDateTime.year} ${hour}:${zonedDateTime.minute} $ampm".also { solarAlarmViewHolder.alarmDateTime.text = it }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun getShortMonth(month: Month): String
    {
        return when (month)
        {
            Month.JANUARY   -> "Jan"
            Month.FEBRUARY  -> "Feb"
            Month.MARCH     -> "Mar"
            Month.APRIL     -> "Apr"
            Month.MAY       -> "May"
            Month.JUNE      -> "Jun"
            Month.JULY      -> "Jul"
            Month.AUGUST    -> "Aug"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER   -> "Oct"
            Month.NOVEMBER  -> "Nov"
            Month.DECEMBER  -> "Dec"
        }
    }

    private fun getShortDay(dayOfWeek: DayOfWeek): String
    {
        return when (dayOfWeek)
        {
            DayOfWeek.MONDAY    -> "Man"
            DayOfWeek.TUESDAY   -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY  -> "Thu"
            DayOfWeek.FRIDAY    -> "Fri"
            DayOfWeek.SATURDAY  -> "Sat"
            DayOfWeek.SUNDAY    -> "Sun"
        }
    }

    override fun getItemCount(): Int = solarAlarms.size

    fun UpdateSolarAlarms(newSolarAlarms: List<SolarAlarmWithDetails>)
    {
        solarAlarms = newSolarAlarms
        notifyDataSetChanged()
    }
}
