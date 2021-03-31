package com.example.solar_alarm.Data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver;
import com.example.solar_alarm.CreateAlarm.DayUtil;

import java.util.Calendar;

import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.FRIDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.MONDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.RECURRING;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.SATURDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.SUNDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.THURSDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.TITLE;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.TUESDAY;
import static com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver.WEDNESDAY;

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey
    @NonNull
    private int alarmId;

    private int hour, minute;
    private boolean started, recurring;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private String title;

    private long created;

    public Alarm(int alarmId, int hour, int minute, String title, long created, boolean started, boolean recurring, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.started = started;

        this.recurring = recurring;

        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;

        this.title = title;

        this.created = created;
    }

    public Alarm(Alarm alarm)
    {
        this.alarmId = alarm.alarmId;
        this.hour = alarm.hour;
        this.minute = alarm.minute;
        this.started = alarm.started;

        this.recurring = alarm.recurring;

        this.monday = alarm.monday;
        this.tuesday = alarm.tuesday;
        this.wednesday = alarm.wednesday;
        this.thursday = alarm.thursday;
        this.friday = alarm.friday;
        this.saturday = alarm.saturday;
        this.sunday = alarm.sunday;

        this.title = alarm.title;

        this.created = alarm.created;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isStarted() {
        return started;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public void setTitle(String title) {this.title = title; }

    public void setHour(int hour) {this.hour = hour; }

    public void setMinute(int minute) {this.minute = minute; }

    public void setRecurring(boolean recurring) {this.recurring = recurring; }

    public void setMonday(boolean monday) {this.monday = monday; }

    public void setTuesday(boolean tuesday) {this.monday = tuesday; }

    public void setWednesday(boolean wednesday) {this.wednesday = wednesday; }

    public void setThursday(boolean thursday) {this.thursday = thursday; }

    public void setFriday(boolean friday) {this.friday = friday; }

    public void setSaturday(boolean saturday) {this.saturday = saturday; }

    public void setSunday(boolean sunday) {this.sunday = sunday; }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);

        intent.putExtra(TITLE, title);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!recurring) {
            String toastText = null;
            try {
                toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", title, DayUtil.toDay(calendar.get(Calendar.DAY_OF_WEEK)), hour, minute, alarmId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        } else {
            String toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", title, getRecurringDaysText(), hour, minute, alarmId);
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

        this.started = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }

    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Mo ";
        }
        if (tuesday) {
            days += "Tu ";
        }
        if (wednesday) {
            days += "We ";
        }
        if (thursday) {
            days += "Th ";
        }
        if (friday) {
            days += "Fr ";
        }
        if (saturday) {
            days += "Sa ";
        }
        if (sunday) {
            days += "Su ";
        }

        return days;
    }

    public String getTitle() {
        return title;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}