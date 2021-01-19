package com.example.solar_alarm.CreateAlarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAlarmFragment extends Fragment {
    @BindView(R.id.TimePicker) TimePicker timePicker;
    @BindView(R.id.alarmName) EditText alarmName;
    @BindView(R.id.save) Button save;
    @BindView(R.id.recurring) CheckBox recurring;
    @BindView(R.id.mon) Button mon;
    @BindView(R.id.tue) Button tue;
    @BindView(R.id.wed) Button wed;
    @BindView(R.id.thur) Button thu;
    @BindView(R.id.fri) Button fri;
    @BindView(R.id.sat) Button sat;
    @BindView(R.id.sun) Button sun;
    //@BindView(R.id.fragment_createalarm_recurring_options) LinearLayout recurringOptions;

    private CreateAlarmViewModel createAlarmViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_set, container, false);

        ButterKnife.bind(this, view);

//        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    recurringOptions.setVisibility(View.VISIBLE);
//                } else {
//                    recurringOptions.setVisibility(View.GONE);
//                }
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlarm();
                Navigation.findNavController(v).navigate(R.id.action_createAlarmFragment_to_alarmsListFragment);
            }
        });

        return view;
    }

    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        Alarm alarm = new Alarm(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                alarmName.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isPressed(),
                tue.isPressed(),
                wed.isPressed(),
                thu.isPressed(),
                fri.isPressed(),
                sat.isPressed(),
                sun.isPressed()
        );

        createAlarmViewModel.insert(alarm);

        alarm.schedule(getContext());
    }
}
