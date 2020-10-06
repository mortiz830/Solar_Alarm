package com.example.solar_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmSetFragment extends Fragment {
    Button save, cancel;
    EditText almName;
    TimePicker tp1;
    int currentHr, currentMin;
    //Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm_set, container, false);
        almName = v.findViewById(R.id.almName);
        tp1 = v.findViewById(R.id.TimePicker);
        save = v.findViewById(R.id.save);
        cancel = v.findViewById(R.id.cancel);

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String alarmName = almName.getText().toString();

                currentHr = tp1.getHour();
                currentMin = tp1.getMinute();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, currentHr);
                calendar.set(Calendar.MINUTE, currentMin);
                calendar.set(Calendar.SECOND, 0);


                String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

                AlarmListActivity alarmListActivity = (AlarmListActivity) getActivity();
                alarmListActivity.addNewAlarm(timeText,alarmName);
                almName.getText().clear();
                getFragmentManager().beginTransaction().remove(AlarmSetFragment.this).commit();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(AlarmSetFragment.this).commit();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

//    private void setAlarm(Calendar calendar)
//    {
//        AlarmManager alarmManager;
//        PendingIntent alarmIntent;
//
//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
//
//        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
//    }
}