package com.example.solar_alarm.AlarmList

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.Tables.SolarAlarmWithDetails
import com.example.solar_alarm.databinding.SolarAlarmListItemBinding

class AlarmRecycleViewAdapter(private val listener: OnToggleAlarmListener) : RecyclerView.Adapter<AlarmViewHolder>() {
    private var alarms: MutableList<SolarAlarmWithDetails> = ArrayList()
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = SolarAlarmListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return AlarmViewHolder(binding, listener)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun setAlarms(alarms: MutableList<SolarAlarmWithDetails>) {
        this.alarms = alarms
        notifyDataSetChanged()
    }

    fun getAlarm(position: Int): SolarAlarmWithDetails {
        return alarms[position]
    }
}
