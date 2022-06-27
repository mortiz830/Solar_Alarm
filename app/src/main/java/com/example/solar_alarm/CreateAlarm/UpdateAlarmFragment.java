package com.example.solar_alarm.CreateAlarm;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.solar_alarm.AlarmList.AlarmListFragment;
import com.example.solar_alarm.AlarmList.AlarmListViewModel;
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel;
import com.example.solar_alarm.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateAlarmFragment extends Fragment {
    @BindView(R.id.fragment_updatealarm_timePicker) TimePicker timePicker;
    @BindView(R.id.fragment_updatealarm_title) EditText title;
    @BindView(R.id.fragment_updatealarm_scheduleAlarm) Button scheduleAlarm;
    @BindView(R.id.fragment_updatealarm_recurring) CheckBox recurring;
    @BindView(R.id.fragment_updatealarm_checkMon) CheckBox mon;
    @BindView(R.id.fragment_updatealarm_checkTue) CheckBox tue;
    @BindView(R.id.fragment_updatealarm_checkWed) CheckBox wed;
    @BindView(R.id.fragment_updatealarm_checkThu) CheckBox thu;
    @BindView(R.id.fragment_updatealarm_checkFri) CheckBox fri;
    @BindView(R.id.fragment_updatealarm_checkSat) CheckBox sat;
    @BindView(R.id.fragment_updatealarm_checkSun) CheckBox sun;
    @BindView(R.id.fragment_updatealarm_recurring_options) LinearLayout recurringOptions;

    private AlarmListViewModel updateAlarmViewModel;
    private SolarAlarmDisplayModel updatedAlarm;
    int location;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        location = bundle.getInt("position");
        updateAlarmViewModel = new ViewModelProvider(requireParentFragment()).get(AlarmListViewModel.class);;
        updateAlarmViewModel.getAlarmDisplayLiveData().observe(this, new Observer<List<SolarAlarmDisplayModel>>() {
            @Override
            public void onChanged(List<SolarAlarmDisplayModel> alarms) {
                if(alarms != null)
                {
//                   updatedAlarm = new SolarAlarmDisplayModel(alarms.get(location));
//                   timePicker.setHour(updatedAlarm.getHour());
//                   timePicker.setMinute(updatedAlarm.getMinute());
//                   title.setText(updatedAlarm.getTitle());
//                   recurring.setChecked(updatedAlarm.isRecurring());
//                   mon.setChecked(updatedAlarm.isMonday());
//                   tue.setChecked(updatedAlarm.isTuesday());
//                   wed.setChecked(updatedAlarm.isWednesday());
//                   thu.setChecked(updatedAlarm.isThursday());
//                   fri.setChecked(updatedAlarm.isFriday());
//                   sat.setChecked(updatedAlarm.isSaturday());
//                   sun.setChecked(updatedAlarm.isSunday());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updatealarm, container, false);

        ButterKnife.bind(this, view);

        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurringOptions.setVisibility(View.VISIBLE);
                } else {
                    recurringOptions.setVisibility(View.GONE);
                }
            }
        });

        scheduleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlarm();
                AlarmListFragment alarmListFragment = new AlarmListFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.activity_main_nav_host_fragment, alarmListFragment).commit();
            }
        });

        return view;
    }

    private void updateAlarm() {
//        updatedAlarm.setHour(TimePickerUtil.getTimePickerHour(timePicker));
//        updatedAlarm.setMinute(TimePickerUtil.getTimePickerMinute(timePicker));
//        updatedAlarm.setTitle(title.getText().toString());
//        updatedAlarm.setCreated(System.currentTimeMillis());
//        updatedAlarm.setRecurring(recurring.isChecked());
//        updatedAlarm.setMonday(mon.isChecked());
//        updatedAlarm.setTuesday(tue.isChecked());
//        updatedAlarm.setWednesday(wed.isChecked());
//        updatedAlarm.setThursday(thu.isChecked());
//        updatedAlarm.setFriday(fri.isChecked());
//        updatedAlarm.setSaturday(sat.isChecked());
//        updatedAlarm.setSunday(sun.isChecked());
//
//        updateAlarmViewModel.update(updatedAlarm);
//
//        updatedAlarm.schedule(getContext());
    }
}
