package com.example.solar_alarm.CreateAlarm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.R;
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAlarmFragment extends Fragment{

    @BindView(R.id.fragment_createalarm_title)
    EditText title;
    @BindView(R.id.fragment_createalarm_scheduleAlarm)
    Button scheduleAlarm;
    @BindView(R.id.fragment_createalarm_recurring)
    CheckBox recurring;
    @BindView(R.id.fragment_createalarm_checkMon)
    CheckBox mon;
    @BindView(R.id.fragment_createalarm_checkTue)
    CheckBox tue;
    @BindView(R.id.fragment_createalarm_checkWed)
    CheckBox wed;
    @BindView(R.id.fragment_createalarm_checkThu)
    CheckBox thu;
    @BindView(R.id.fragment_createalarm_checkFri)
    CheckBox fri;
    @BindView(R.id.fragment_createalarm_checkSat)
    CheckBox sat;
    @BindView(R.id.fragment_createalarm_checkSun)
    CheckBox sun;
    @BindView(R.id.fragment_createalarm_recurring_options)
    LinearLayout recurringOptions;
    @BindView(R.id.fragment_createalarm_location_spinner)
    Spinner spinner;
    @BindView(R.id.fragment_createalarm_sunrise_radio_button)
    RadioButton sunrise;
    @BindView(R.id.fragment_createalarm_solarnoon_radio_button)
    RadioButton solarnoon;
    @BindView(R.id.fragment_createalarm_sunset_radio_button)
    RadioButton sunset;
    @BindView(R.id.fragment_createalarm_radio_button_at)
    RadioButton at;
    @BindView(R.id.fragment_createalarm_radio_button_before)
    RadioButton before;
    @BindView(R.id.fragment_createalarm_radio_button_after)
    RadioButton after;
    @BindView(R.id.fragment_createalarm_alarmtime_radiogroup)
    RadioGroup alarmTime;
    @BindView(R.id.fragment_createalarm_alarmtype_radiogroup)
    RadioGroup alarmType;
    SpinnerAdapter spinnerAdapter;
    TimePicker timePicker;

    private LocationRepository locationRepository;
    private List<Location> Locations;

    private CreateAlarmViewModel createAlarmViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Locations = new ArrayList<Location>();
        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);
        locationRepository = new LocationRepository(getActivity().getApplication());
        locationRepository.getAll().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                Locations = locations;
                spinnerAdapter = new SpinnerAdapter(getActivity(), Locations);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        Location locationItem = (Location) adapterView.getItemAtPosition(position);
                        SunriseSunsetRequest sunriseSunsetRequest =
                                new SunriseSunsetRequest((float) locationItem.Latitude, (float) locationItem.Longitude, Calendar.getInstance(), true);
                        new TimeResponseTask().execute(sunriseSunsetRequest);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                alarmType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createalarm, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_add_location_map);

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
                title.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked()
        );

        createAlarmViewModel.insert(alarm);

        alarm.schedule(getContext());
    }

    private class TimeResponseTask extends AsyncTask<SunriseSunsetRequest, Void,Void> {
        HttpRequests httpRequests;
        SunriseSunsetResponse response;
        @Override
        protected Void doInBackground(SunriseSunsetRequest... sunriseSunsetRequests) {

            try {
                httpRequests = new HttpRequests(sunriseSunsetRequests[0]);
                response = httpRequests.GetSolarData(sunriseSunsetRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

//    public void onRadioButtonClicked(View view)
//    {
//        boolean checked = ((RadioButton) view).isChecked();
//        switch(view.getId()) {
//            case R.id.fragment_createalarm_sunrise_radio_button:
//                if (checked)
//                {
//                    solarnoon.setChecked(false);
//                    sunset.setChecked(false);
//                }
//                    break;
//            case R.id.fragment_createalarm_solarnoon_radio_button:
//                if (checked)
//                {
//                    sunrise.setChecked(false);
//                    sunset.setChecked(false);
//                }
//                    break;
//            case R.id.fragment_createalarm_sunset_radio_button:
//                if (checked)
//                {
//                    sunrise.setChecked(false);
//                    solarnoon.setChecked(false);
//                }
//                    break;
//            case R.id.fragment_createalarm_radio_button_at:
//                if (checked)
//                {
//                    before.setChecked(false);
//                    after.setChecked(false);
//                }
//                    break;
//            case R.id.fragment_createalarm_radio_button_before:
//                if (checked)
//                {
//                    at.setChecked(false);
//                    after.setChecked(false);
//                }
//                    break;
//            case R.id.fragment_createalarm_radio_button_after:
//                if (checked)
//                {
//                    at.setChecked(false);
//                    before.setChecked(false);
//                }
//
//                    break;
//        }
//    }
}
