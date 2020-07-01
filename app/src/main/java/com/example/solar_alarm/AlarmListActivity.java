package com.example.solar_alarm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 * */
public class AlarmListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter alarmAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycleViewer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmAdapter = new AlarmAdapter();   // add dataset to this new instance
        recyclerView.setAdapter(alarmAdapter);
    }
}
