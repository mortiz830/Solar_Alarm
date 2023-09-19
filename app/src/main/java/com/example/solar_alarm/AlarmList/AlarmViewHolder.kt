package com.example.solar_alarm.AlarmList

import com.example.solar_alarm.R
import android.annotation.TargetApi
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository
import com.example.solar_alarm.Data.Tables.SolarTime
import android.os.AsyncTask
import kotlin.Throws
import com.example.solar_alarm.Data.Converters
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import java.lang.Exception

class AlarmViewHolder(itemView: View, listener: OnToggleAlarmListener) : RecyclerView.ViewHolder(itemView) {
    private val alarmTime: TextView
    private val alarmDate: TextView
    private val alarmRecurring: ImageView
    private val alarmRecurringDays: TextView
    private val alarmTitle: TextView
    var parent_layout: LinearLayout
    var alarmStarted: SwitchCompat
    private val listener: OnToggleAlarmListener

    init {
        alarmTime = itemView.findViewById(R.id.item_alarm_time)
        alarmDate = itemView.findViewById(R.id.item_alarm_date)
        alarmStarted = itemView.findViewById(R.id.item_alarm_started)
        alarmRecurring = itemView.findViewById(R.id.item_alarm_recurring)
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays)
        alarmTitle = itemView.findViewById(R.id.item_alarm_title)
        parent_layout = itemView.findViewById(R.id.parent_layout)
        this.listener = listener
    }

    inner class GetSolarTime : AsyncTask<Int?, Void?, SolarTime?>() {
        @TargetApi(Build.VERSION_CODES.O) //@Override
        protected override fun doInBackground(vararg p0: Int?): SolarTime? {
            var solarTime: SolarTime? = null
            try {
                //solarTime = SolarTimeRepository().GetById(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null// solarTime
        }
    }

    /*
    * SolarTime*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun bind(solarAlarm: SolarAlarm) {
        val solarTime = GetSolarTime().execute(solarAlarm.SolarTimeId).get()!!
        val zonedDateTime = solarAlarm.SolarTimeTypeId?.let { solarTime.GetLocalZonedDateTime(it) }
        zonedDateTime?.hour
        val localTime = zonedDateTime?.toLocalTime()
        val alarmText = zonedDateTime?.let { Converters.toTimeString(it) }
        alarmDate.text = alarmText!![0]
        alarmTime.text = alarmText[1]
        alarmStarted.isChecked = solarAlarm.Active
        if (solarAlarm.Recurring) {
            alarmRecurring.setImageResource(R.drawable.ic_repeat_black_24dp)
            alarmRecurringDays.text = solarAlarm.recurringDaysText
        } else {
            alarmRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp)
            alarmRecurringDays.text = "Once Off"
        }
        alarmTitle.text = String.format("%s | %d", solarAlarm.Name, solarAlarm.Id)

//        alarmStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//                listener.onToggle(solarAlarm);
//            }
//        });
    }
}