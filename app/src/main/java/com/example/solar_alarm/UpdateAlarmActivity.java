package com.example.solar_alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class UpdateAlarmActivity extends AppCompatActivity {
    Button save, cancel;
    EditText almName;
    TimePicker tp1;
    int currentHr, currentMin;

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

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getIncomingIntent();
    }

    private void getIncomingIntent()
    {
        if(getIntent().hasExtra("AlarmName"))
        {
            String AlarmName = getIntent().getStringExtra("AlarmName");
            setData(AlarmName);
        }

    }

    private void setData(String AlarmName)
    {
        almName.setText(AlarmName);
    }
}