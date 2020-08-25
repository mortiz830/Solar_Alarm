package com.example.solar_alarm;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 * */
public class AlarmListActivity extends AppCompatActivity {
    private ArrayList<Alarm>           mAlarm;
    private RecyclerView               recyclerView;
    private RecyclerView.Adapter       alarmAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private final String SaveFile = "SolarAlarmData.json";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.recycleViewer);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // MOCKED DATA
        mAlarm = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(mAlarm);
        recyclerView.setAdapter(alarmAdapter);

        // FRAGMENTS
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final AlarmSetFragment alarmSetFragment = new AlarmSetFragment();


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.flFragment, alarmSetFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewAlarm(String localTimeString, String alarmName){
        int currPosition = mAlarm.size();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime time = LocalTime.parse(localTimeString, dtf);
        Alarm a = new Alarm(time, alarmName);
        mAlarm.add(currPosition, a);  // add to list's tail
        alarmAdapter.notifyItemInserted(currPosition);  // refresh view

        SaveToFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SaveToFile()
    {
        String json = new Gson().toJson(mAlarm);   // create json string by serializing

        // create/overwrite file to disk
        this.getApplicationContext();
        File saveFile = new File(this.getBaseContext().getDataDir(), SaveFile);
    }
}

