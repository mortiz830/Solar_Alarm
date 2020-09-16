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
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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

    private FloatingActionButton floatingActionButton;

    private final String SaveFile = "SolarAlarmData.json";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        String json = ReadFromFile();

        if (json != null)
        {
            ParseJSON(json);
        }

        recyclerView = findViewById(R.id.recycleViewer);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // MOCKED DATA
        if (mAlarm == null)
        {
            mAlarm = new ArrayList<>();
        }
        alarmAdapter = new AlarmAdapter(mAlarm);
        recyclerView.setAdapter(alarmAdapter);

        // FRAGMENTS
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final AlarmSetFragment alarmSetFragment = new AlarmSetFragment();


        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.flFragment, alarmSetFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                floatingActionButton.hide();
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
        floatingActionButton.show();
        SaveToFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SaveToFile()
    {
        // create json string by serializing
        String json = new Gson().toJson(mAlarm);
        FileOutputStream fos = null;

        // create/overwrite file to disk
        try
        {
            fos = this.openFileOutput(SaveFile, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(fos != null)
            {
                try {
                    fos.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String ReadFromFile()
    {
        String json = null;

        try
        {
            File file = new File("/data/data/com.example.solar_alarm/files/" + SaveFile);

            if (file.exists())
            {
                FileInputStream fileInputStream = openFileInput(SaveFile);

                try
                {
                    int size = fileInputStream.available();

                    if (size > 0)
                    {
                        byte[] buffer = new byte[size];
                        fileInputStream.read(buffer);
                        fileInputStream.close();
                        json = new String(buffer, StandardCharsets.UTF_8);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                file.createNewFile();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return json;
    }

    private void ParseJSON(String json)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Alarm>>(){}.getType();
        ArrayList<Alarm> mList = gson.fromJson(json, type);
        mAlarm = new ArrayList<>(mList);
    }
}

