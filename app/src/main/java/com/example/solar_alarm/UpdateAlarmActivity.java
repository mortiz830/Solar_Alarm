package com.example.solar_alarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class UpdateAlarmActivity extends AppCompatActivity {
    Button save, cancel;
    EditText almName;
    TimePicker tp1;
    int currentHr, currentMin, position;
    String alarmPosition;
    AlarmListActivity alarmListActivity;
    AlarmAdapter alarmAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alarm);

        almName = findViewById(R.id.almName);
        tp1 = findViewById(R.id.TimePicker);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alarmName = almName.getText().toString();

                currentHr = tp1.getHour();
                currentMin = tp1.getMinute();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, currentHr);
                c.set(Calendar.MINUTE, currentMin);
                c.set(Calendar.SECOND, 0);

                String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

                Intent intent = new Intent(UpdateAlarmActivity.this, AlarmListActivity.class);
                intent.putExtra("AlarmName", alarmName);
                intent.putExtra("AlarmTime", timeText);
                intent.putExtra("AlarmPosition", Integer.toString(position));
                intent.putExtra("methodName", "UpdateAlarm");
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateAlarmActivity.this, AlarmListActivity.class);
                startActivity(intent);
            }
        });

        getIncomingIntent();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getIncomingIntent()
    {
        if(getIntent().hasExtra("AlarmName"))
        {
            String AlarmName = getIntent().getStringExtra("AlarmName");
            String AlarmTime = getIntent().getStringExtra("AlarmTime");
            String AlarmPosition = getIntent().getStringExtra("AlarmPosition");
            setData(AlarmName, AlarmTime, AlarmPosition);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setData(String AlarmName, String AlarmTime, String AlarmPosition)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date time = null;

        try
        {
            time = sdf.parse(AlarmTime);

        }

        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);

        tp1.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        tp1.setCurrentMinute(c.get(Calendar.MINUTE));
        almName.setText(AlarmName);
        alarmPosition = AlarmPosition;
        position = Integer.parseInt(alarmPosition);
    }
}