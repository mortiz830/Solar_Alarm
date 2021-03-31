package com.example.solar_alarm.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.solar_alarm.Service.AlarmService;
import com.example.solar_alarm.Service.RescheduleAlarmService;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String RECURRING = "RECURRING";
    public static final String TITLE = "TITLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }
        else {
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent);
            } {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent);
                }
            }
        }
    }

    private boolean alarmIsToday(Intent intent)
    {
        boolean alarmIsToday = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today)
        {
            case Calendar.MONDAY:
                if (intent.getBooleanExtra(MONDAY, false))
                    alarmIsToday = true;
            case Calendar.TUESDAY:
                if (intent.getBooleanExtra(TUESDAY, false))
                    alarmIsToday = true;
            case Calendar.WEDNESDAY:
                if (intent.getBooleanExtra(WEDNESDAY, false))
                    alarmIsToday = true;
            case Calendar.THURSDAY:
                if (intent.getBooleanExtra(THURSDAY, false))
                    alarmIsToday = true;
            case Calendar.FRIDAY:
                if (intent.getBooleanExtra(FRIDAY, false))
                    alarmIsToday = true;
            case Calendar.SATURDAY:
                if (intent.getBooleanExtra(SATURDAY, false))
                    alarmIsToday = true;
            case Calendar.SUNDAY:
                if (intent.getBooleanExtra(SUNDAY, false))
                    alarmIsToday = true;
        }

        return alarmIsToday;
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
