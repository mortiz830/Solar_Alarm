package com.example.solar_alarm.CreateAlarm;

import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.FRIDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.MONDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.RECURRING;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.SATURDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.SUNDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.THURSDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.TITLE;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.TUESDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.WEDNESDAY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver;
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.time.ZonedDateTime;
import java.util.Calendar;

public class AlarmScheduler
{
    private SolarAlarm solarAlarm;
    private SolarTime solarTime;
    private int hours;
    private int mins;
    private boolean started;

    public AlarmScheduler(SolarAlarm solarAlarm, SolarTime solarTime, int hours, int mins)
    {
        this.solarAlarm = solarAlarm;
        this.solarTime = solarTime;
        this.hours = hours;
        this.mins = mins;
        this.started = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void schedule(Context context) throws Exception
    {


        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(RECURRING, solarAlarm.Recurring);
        intent.putExtra(MONDAY, solarAlarm.Monday);
        intent.putExtra(TUESDAY, solarAlarm.Tuesday);
        intent.putExtra(WEDNESDAY, solarAlarm.Wednesday);
        intent.putExtra(THURSDAY, solarAlarm.Thursday);
        intent.putExtra(FRIDAY, solarAlarm.Friday);
        intent.putExtra(SATURDAY, solarAlarm.Saturday);
        intent.putExtra(SUNDAY, solarAlarm.Sunday);
        intent.putExtra(TITLE, solarAlarm.Name);

        ZonedDateTime localZonedDateTime;

        if(true) localZonedDateTime = ZonedDateTime.now().plusMinutes(mins); else // DEBUG_STATEMENT makes alarm ring immediately
            localZonedDateTime = solarTime.GetLocalZonedDateTime(solarAlarm.SolarTimeTypeId);

        if(solarAlarm.OffsetTypeId == OffsetTypeEnum.Before)
        {
            localZonedDateTime.minusHours(hours);
            localZonedDateTime.minusMinutes(mins);
        }
        else if (solarAlarm.OffsetTypeId == OffsetTypeEnum.After)
        {
            localZonedDateTime.plusHours(hours);
            localZonedDateTime.plusMinutes(mins);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, localZonedDateTime.getHour());
        calendar.set(Calendar.MINUTE, localZonedDateTime.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
        {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        AlarmManager  alarmManager       = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, solarAlarm.Id, intent, 0);

        if (solarAlarm.Recurring)
        {
            String toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, getRecurringDaysText(), localZonedDateTime.getHour(), localZonedDateTime.getMinute(), solarAlarm.Id);
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;

            try
            {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), RUN_DAILY, alarmPendingIntent);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        else
        {
            String toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", solarAlarm.Name, DayUtil.toDay(calendar.get(Calendar.DAY_OF_WEEK)), localZonedDateTime.getHour(), localZonedDateTime.getMinute(), solarAlarm.Id);;
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            try
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

        this.started = true;
    }

    public String getRecurringDaysText()
    {
        if (!solarAlarm.Recurring)
        {
            return null;
        }

        String days = "";
        if (solarAlarm.Monday) {
            days += "Mo ";
        }
        if (solarAlarm.Tuesday) {
            days += "Tu ";
        }
        if (solarAlarm.Wednesday) {
            days += "We ";
        }
        if (solarAlarm.Thursday) {
            days += "Th ";
        }
        if (solarAlarm.Friday) {
            days += "Fr ";
        }
        if (solarAlarm.Saturday) {
            days += "Sa ";
        }
        if (solarAlarm.Sunday) {
            days += "Su ";
        }

        return days;
    }
}
