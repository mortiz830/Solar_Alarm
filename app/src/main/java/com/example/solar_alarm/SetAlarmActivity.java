package com.example.solar_alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;


public class SetAlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private int hour;
    private int min;
    private EditText alarmName;
    private Button setAlarm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

//        timePicker = findViewById(R.id.TimePicker);
//        alarmName = findViewById(R.id.alarmName);
//        setAlarm = findViewById(R.id.setAlarm);
//        setAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hour = timePicker.getCurrentHour();
//                min = timePicker.getCurrentMinute();
//                onTimeSet(timePicker, hour, min);
//            }
//        });
    }

    public void onTimeSet(TimePicker timePicker, int hour, int min){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        setAlarm(c);
    }

    private void updateTimeText(Calendar c)
    {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        alarmName.setText(timeText);
    }

    public void setAlarm(Calendar c) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),alarmIntent);
    }
}