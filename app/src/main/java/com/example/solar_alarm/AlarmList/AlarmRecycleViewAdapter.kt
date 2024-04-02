package com.example.solar_alarm.AlarmList

import com.example.solar_alarm.R
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Tables.SolarAlarm
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.ExecutionException

class AlarmRecycleViewAdapter(listener: OnToggleAlarmListener) : RecyclerView.Adapter<AlarmViewHolder>() {
    private var alarms: MutableList<SolarAlarm>
    private val listener: OnToggleAlarmListener
    var context: Context? = null

    init {
        alarms = ArrayList()
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        context = parent.context
        return AlarmViewHolder(itemView, listener)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        try {
            holder.bind(alarm)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun setAlarms(alarms: MutableList<SolarAlarm>) {
        this.alarms = alarms
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: AlarmViewHolder) {
        super.onViewRecycled(holder)
        holder.alarmStarted.setOnCheckedChangeListener(null)
    }

    fun removeItem(position: Int): SolarAlarm {
        val alarm = alarms[position]
        alarms.remove(alarm)
        notifyItemRemoved(position)
        return alarm
    }

    fun getAlarm(position: Int): SolarAlarm {
        return alarms[position]
    }
}