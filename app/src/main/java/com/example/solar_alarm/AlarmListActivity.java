package com.example.solar_alarm;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


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
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // MOCKED DATA
        LocalTime now = LocalTime.now();

        List<Alarm> alarmMocks = new ArrayList<Alarm>();
        alarmMocks.add(new Alarm(now));
        alarmMocks.add(new Alarm(now.plusHours(24)));
        alarmMocks.add(new Alarm(now.plusHours(48)));

        alarmAdapter = new AlarmAdapter(alarmMocks);   // add dataset to this new instance
        recyclerView.setAdapter(alarmAdapter);
    }
}
