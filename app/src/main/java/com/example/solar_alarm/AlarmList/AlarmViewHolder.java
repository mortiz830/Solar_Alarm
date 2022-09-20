package com.example.solar_alarm.AlarmList;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solar_alarm.Data.AsyncDbAccess;
import com.example.solar_alarm.Data.Converters;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.R;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public class AlarmViewHolder extends RecyclerView.ViewHolder
{
    private TextView alarmTime;
    private TextView alarmDate;
    private ImageView alarmRecurring;
    private TextView alarmRecurringDays;
    private TextView alarmTitle;
    LinearLayout parent_layout;

    Switch alarmStarted;

    private OnToggleAlarmListener listener;

    public AlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener)
    {
        super(itemView);

        alarmTime = itemView.findViewById(R.id.item_alarm_time);
        alarmDate = itemView.findViewById(R.id.item_alarm_date);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);
        alarmRecurring = itemView.findViewById(R.id.item_alarm_recurring);
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays);
        alarmTitle = itemView.findViewById(R.id.item_alarm_title);
        parent_layout = itemView.findViewById(R.id.parent_layout);

        this.listener = listener;
    }

    /*
    * SolarTime*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void bind(SolarAlarm solarAlarm) throws Exception
    {
        SolarTime solarTime = new AsyncDbAccess.GetSolarTime().execute(solarAlarm.SolarTimeId).get();

        ZonedDateTime zonedDateTime = solarTime.GetLocalZonedDateTime(solarAlarm.SolarTimeTypeId);

        zonedDateTime.getHour();
        LocalTime localTime = zonedDateTime.toLocalTime();

        String[] alarmText = Converters.toTimeString(zonedDateTime);

        alarmDate.setText(alarmText[0]);
        alarmTime.setText(alarmText[1]);
        alarmStarted.setChecked(solarAlarm.Active);

        if (solarAlarm.Recurring)
        {
            alarmRecurring.setImageResource(R.drawable.ic_repeat_black_24dp);
            alarmRecurringDays.setText(solarAlarm.getRecurringDaysText());
        }
        else
        {
            alarmRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp);
            alarmRecurringDays.setText("Once Off");
        }

        alarmTitle.setText(String.format("%s | %d", solarAlarm.Name, solarAlarm.Id));

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
