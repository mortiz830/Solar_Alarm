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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
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
    @BindView(R.id.fragment_createalarm_sunrise_data)
    TextView sunriseData;
    @BindView(R.id.fragment_createalarm_solarnoon_data)
    TextView solarNoonData;
    @BindView(R.id.fragment_createalarm_sunset_data)
    TextView sunsetData;
    SpinnerAdapter spinnerAdapter;
    TimePicker timePicker;
    Location locationItem;
    SolarTime solarTimeItem;
    SolarAlarm solarAlarmItem = new SolarAlarm();
    TimeZoneConverter timeZoneConverter;

    boolean isLocationIdExists;
    boolean isDateExists;
    boolean isSolarAlarmLocationIdExists;
    boolean isSolarAlarmNameExists;

    private LocationRepository locationRepository;
    private List<Location> Locations;
    private SolarTimeRepository solarTimeRepository;
    private SolarAlarmRepository solarAlarmRepository;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Locations = new ArrayList<Location>();
        solarTimeRepository = new SolarTimeRepository(getActivity().getApplication());
        solarAlarmRepository = new SolarAlarmRepository(getActivity().getApplication());
        locationRepository = new LocationRepository(getActivity().getApplication());
        locationRepository.getAll().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                Locations = locations;
                spinnerAdapter = new SpinnerAdapter(getActivity(), Locations);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        timeZoneConverter = new TimeZoneConverter(solarTimeItem);
                        solarTimeItem = timeZoneConverter.convertSolarTime();
                        sunriseData.setText(solarTimeItem.Sunrise.toString());
                        solarNoonData.setText(solarTimeItem.SolarNoon.toString());
                        sunsetData.setText(solarTimeItem.Sunset.toString());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

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
        solarAlarmItem.Name = title.getText().toString();
        solarAlarmItem.LocationId = locationItem.Id;
        solarAlarmItem.Recurring = recurring.isChecked();
        solarAlarmItem.Monday = mon.isChecked();
        solarAlarmItem.Tuesday = tue.isChecked();
        solarAlarmItem.Wednesday = wed.isChecked();
        solarAlarmItem.Thursday = thu.isChecked();
        solarAlarmItem.Friday = fri.isChecked();
        solarAlarmItem.Saturday = sat.isChecked();
        solarAlarmItem.Sunday = sun.isChecked();
        solarAlarmItem.Sunrise = sunrise.isChecked();
        solarAlarmItem.Sunset = sunset.isChecked();
        solarAlarmItem.Before = before.isChecked();
        solarAlarmItem.At = at.isChecked();
        solarAlarmItem.After = after.isChecked();
        solarAlarmItem.SolarNoon = solarnoon.isChecked();
        solarAlarmItem.CivilTwilightBegin = false;
        solarAlarmItem.CivilTwilightEnd = false;
        solarAlarmItem.NauticalTwilightBegin = false;
        solarAlarmItem.NauticalTwilightEnd = false;
        solarAlarmItem.AstronomicalTwilightBegin = false;
        solarAlarmItem.AstronomicalTwilightEnd = false;

        try {
            isSolarAlarmLocationIdExists = new SolarAlarmLocationIdExistsTask().execute().get();
            isSolarAlarmNameExists = new SolarAlarmNameExistsTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!isSolarAlarmLocationIdExists && !isSolarAlarmNameExists)
            solarAlarmRepository.Insert(solarAlarmItem);
        else
            Toast.makeText(getContext(), "Alarm already exists!", Toast.LENGTH_LONG).show();
        //alarm.schedule(getContext());
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
        SolarTime solarTime = new SolarTime();
        String pattern = "h:mm a";

        solarTime.LocationId = locationItem.Id;
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

    private class SolarAlarmNameExistsTask extends AsyncTask<LocalDate, Void, Boolean>{
        @Override
        protected Boolean doInBackground(LocalDate... localDates) {
            Boolean result = false;
            try{
                result = solarAlarmRepository.isSolarAlarmNameExists(solarAlarmItem.Name);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }

    private class SolarAlarmLocationIdExistsTask extends AsyncTask<Integer, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Integer... integers) {
            Boolean result = false;
            try{
                result = solarAlarmRepository.isLocationIDExists(solarAlarmItem.LocationId);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }
}
