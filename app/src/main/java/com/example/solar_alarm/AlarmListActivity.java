package com.example.solar_alarm;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 * */
public class AlarmListActivity extends AppCompatActivity {
    private RecyclerView               recyclerView;
    private RecyclerView.Adapter       alarmAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.recycleViewer);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // MOCKED DATA
        alarmAdapter = new AlarmAdapter(Alarm.createAlarmList(5));
        recyclerView.setAdapter(alarmAdapter);
    }
}
