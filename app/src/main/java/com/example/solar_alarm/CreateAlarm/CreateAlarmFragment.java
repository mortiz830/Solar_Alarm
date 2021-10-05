package com.example.solar_alarm.CreateAlarm;

import android.os.AsyncTask;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.R;
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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
    Location locationItem;
    SolarTime solarTimeItem;

    boolean isLocationIdExists;
    boolean isDateExists;

    private LocationRepository locationRepository;
    private List<Location> Locations;
    private SolarTimeRepository solarTimeRepository;

    private CreateAlarmViewModel createAlarmViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Locations = new ArrayList<Location>();
        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);
        solarTimeRepository = new SolarTimeRepository(getActivity().getApplication());
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
                        locationItem = (Location) adapterView.getItemAtPosition(position);

                        SunriseSunsetRequest sunriseSunsetRequest =
                                new SunriseSunsetRequest((float) locationItem.Latitude, (float) locationItem.Longitude, Calendar.getInstance(), true);
                        try {
                            solarTimeItem = new TimeResponseTask().execute(sunriseSunsetRequest).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            isLocationIdExists = new LocationIdExistsTask().execute().get();
                            isDateExists = new DateExistsTask().execute().get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(!isLocationIdExists && !isDateExists)
                        {
                            solarTimeRepository.Insert(solarTimeItem);
                        }

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

    private class TimeResponseTask extends AsyncTask<SunriseSunsetRequest, Void, SolarTime> {
        HttpRequests httpRequests;
        SunriseSunsetResponse response;
        SolarTime solarTime;
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(SunriseSunsetRequest... sunriseSunsetRequests) {

            try {
                httpRequests = new HttpRequests(sunriseSunsetRequests[0]);
                response = httpRequests.GetSolarData(sunriseSunsetRequests[0]);
                solarTime = newSolarTime(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return solarTime;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime newSolarTime(SunriseSunsetResponse sunriseSunsetResponse)
    {
        int solarTimeID = new Random().nextInt(Integer.MAX_VALUE);
        SolarTime solarTime = new SolarTime();
        String pattern = "h:mm a";
        String day_length_pattern = "hh:mm";

        solarTime.LocationId = locationItem.Id;
        solarTime.Id = solarTimeID;
        solarTime.Date = LocalDate.now();
        solarTime.Sunrise = LocalTime.parse(sunriseSunsetResponse.getSunrise(), DateTimeFormatter.ofPattern(pattern));
        solarTime.Sunset = LocalTime.parse(sunriseSunsetResponse.getSunset(), DateTimeFormatter.ofPattern(pattern));
        solarTime.SolarNoon = LocalTime.parse(sunriseSunsetResponse.getSolarNoon(), DateTimeFormatter.ofPattern(pattern));
        solarTime.DayLength = LocalTime.parse(sunriseSunsetResponse.getDayLength());
        solarTime.CivilTwilightBegin = LocalTime.parse(sunriseSunsetResponse.getCivilTwilightBegin(), DateTimeFormatter.ofPattern(pattern));
        solarTime.CivilTwilightEnd = LocalTime.parse(sunriseSunsetResponse.getCivilTwilightEnd(), DateTimeFormatter.ofPattern(pattern));
        solarTime.NauticalTwilightBegin = LocalTime.parse(sunriseSunsetResponse.getNauticalTwilightBegin(), DateTimeFormatter.ofPattern(pattern));
        solarTime.NauticalTwilightEnd = LocalTime.parse(sunriseSunsetResponse.getNauticalTwilightEnd(), DateTimeFormatter.ofPattern(pattern));
        solarTime.AstronomicalTwilightBegin = LocalTime.parse(sunriseSunsetResponse.getAstronomicalTwilightBegin(), DateTimeFormatter.ofPattern(pattern));
        solarTime.AstronomicalTwilightEnd = LocalTime.parse(sunriseSunsetResponse.getAstronomicalTwilightEnd(), DateTimeFormatter.ofPattern(pattern));

        return solarTime;
    }

    private class LocationIdExistsTask extends AsyncTask<Integer, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Integer... integers) {
            Boolean result = false;
            try{
                result = solarTimeRepository.isLocationIDExists(locationItem.Id);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }

    private class DateExistsTask extends AsyncTask<LocalDate, Void, Boolean>{
        @Override
        protected Boolean doInBackground(LocalDate... localDates) {
            Boolean result = false;
            try{
                result = solarTimeRepository.isDateExists(solarTimeItem.Date);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
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
