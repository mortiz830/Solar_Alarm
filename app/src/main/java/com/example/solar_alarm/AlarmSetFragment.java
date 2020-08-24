package com.example.solar_alarm;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AlarmSetFragment extends Fragment {
    Button save, cancel;
    EditText almName;
    TimePicker tp1;
    int currentHr, currentMin;
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
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, currentHr);
                c.set(Calendar.MINUTE, currentMin);
                c.set(Calendar.SECOND, 0);

                String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

                AlarmListActivity al1 = (AlarmListActivity) getActivity();
                al1.addNewAlarm(timeText,alarmName);

                System.out.println(timeText);
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
}