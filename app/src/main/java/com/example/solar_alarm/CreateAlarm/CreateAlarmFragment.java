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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    TimeZoneConverter timeZoneConverter;

    private List<Location> Locations;
    private SolarTimeRepository solarTimeRepository;
    private SolarAlarmRepository solarAlarmRepository;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locations = new ArrayList<>();
        solarTimeRepository = new SolarTimeRepository(getActivity().getApplication());
        solarAlarmRepository = new SolarAlarmRepository(getActivity().getApplication());
        LocationRepository locationRepository = new LocationRepository(getActivity().getApplication());
        locationRepository.getAll().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                Locations = locations;
                spinnerAdapter = new SpinnerAdapter(getActivity(), Locations);
                spinner.setAdapter(spinnerAdapter);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createalarm, container, false);
        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_add_location_map);

        List<SolarTime> solarTimes = new ArrayList<SolarTime>();
        ButterKnife.bind(this, view);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Location locationItem = (Location) adapterView.getItemAtPosition(position);
                Calendar date = Calendar.getInstance();

                for (int i = 0; i < 14; i++)
                {
                    try {
                        solarTimes.add(getSolarTime(locationItem, date));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Solar Time exists!", Toast.LENGTH_LONG).show();
                    }

                    date.add(Calendar.DAY_OF_YEAR, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                for(int i = 0; i < solarTimes.size(); i++)
                {
                    try {
                        scheduleAlarm(solarTimes.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Navigation.findNavController(v).navigate(R.id.action_createAlarmFragment_to_alarmsListFragment);
            }
        });



        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime getSolarTime(Location locationItem, Calendar date) throws Exception {
        boolean isLocationIdDatePairExists = getLocationIdDatePareExists(locationItem, date);
        SolarTime solarTimeItem;

        if(!isLocationIdDatePairExists) {
            SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest((float) locationItem.Latitude, (float) locationItem.Longitude, date, true);
            try {
                solarTimeItem = new TimeResponseTask().execute(sunriseSunsetRequest, locationItem).get();
                timeZoneConverter = new TimeZoneConverter(solarTimeItem);
                solarTimeItem = timeZoneConverter.convertSolarTime();
                sunriseData.setText(solarTimeItem.Sunrise.toString());
                solarNoonData.setText(solarTimeItem.SolarNoon.toString());
                sunsetData.setText(solarTimeItem.Sunset.toString());
                solarTimeRepository.Insert(solarTimeItem);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            solarTimeItem = new GetSolarTimeTask().execute(locationItem.Id, localDate).get();
            sunriseData.setText(solarTimeItem.Sunrise.toString());
            solarNoonData.setText(solarTimeItem.SolarNoon.toString());
            sunsetData.setText(solarTimeItem.Sunset.toString());
        }
        return solarTimeItem;
    }

    public boolean getLocationIdDatePareExists(Location locationItem, Calendar date) throws Exception {
        try {
            return new LocationIdDatePairExistsTask().execute(locationItem, date).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean getSolarAlarmNameLocationIdPairExists(SolarAlarm solarAlarmItem) throws Exception
    {
        boolean result;
        try {
            result = new SolarAlarmNameExistsTask().execute(solarAlarmItem).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void scheduleAlarm(SolarTime solarTimeItem) throws Exception {
        SolarAlarm solarAlarmItem = new SolarAlarm();
        boolean isSolarAlarmNameLocationIdPairExists;



        solarAlarmItem.Name = title.getText().toString();
        solarAlarmItem.LocationId = solarTimeItem.LocationId;
        solarAlarmItem.Recurring = recurring.isChecked();
        solarAlarmItem.Monday = mon.isChecked();
        solarAlarmItem.Tuesday = tue.isChecked();
        solarAlarmItem.Wednesday = wed.isChecked();
        solarAlarmItem.Thursday = thu.isChecked();
        solarAlarmItem.Friday = fri.isChecked();
        solarAlarmItem.Saturday = sat.isChecked();
        solarAlarmItem.Sunday = sun.isChecked();
        //solarAlarmItem.Sunrise = sunrise.isChecked();
        //solarAlarmItem.Sunset = sunset.isChecked();
        //solarAlarmItem.Before = before.isChecked();
        //solarAlarmItem.At = at.isChecked();
        //solarAlarmItem.After = after.isChecked();
        //solarAlarmItem.SolarNoon = solarnoon.isChecked();
        //solarAlarmItem.CivilTwilightBegin = false;
        //solarAlarmItem.CivilTwilightEnd = false;
        //solarAlarmItem.NauticalTwilightBegin = false;
        //solarAlarmItem.NauticalTwilightEnd = false;
        //solarAlarmItem.AstronomicalTwilightBegin = false;
        //solarAlarmItem.AstronomicalTwilightEnd = false;

        try {
            isSolarAlarmNameLocationIdPairExists = getSolarAlarmNameLocationIdPairExists(solarAlarmItem);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if(!isSolarAlarmNameLocationIdPairExists)
            solarAlarmRepository.Insert(solarAlarmItem);
        else
            Toast.makeText(getContext(), "Alarm already exists!", Toast.LENGTH_LONG).show();

        AlarmScheduler alarmScheduler = new AlarmScheduler(solarAlarmItem, solarTimeItem);

        alarmScheduler.schedule(getContext());
    }

    private class TimeResponseTask extends AsyncTask<Object, Void, SolarTime>  {
        HttpRequests httpRequests;
        SunriseSunsetRequest sunriseSunsetRequest;
        SunriseSunsetResponse response;
        Location location;
        SolarTime solarTime;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Object... objects) {
            sunriseSunsetRequest = (SunriseSunsetRequest) objects[0];
            location = (Location) objects[1];
            try {
                httpRequests = new HttpRequests(sunriseSunsetRequest);
                response = httpRequests.GetSolarData(sunriseSunsetRequest);
                solarTime = newSolarTime(response, location);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to get times!", Toast.LENGTH_LONG).show();
            }

            return solarTime;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SolarTime newSolarTime(SunriseSunsetResponse sunriseSunsetResponse, Location locationItem)
    {
        SolarTime solarTime = new SolarTime();
        String pattern = "h:mm a";
        LocalDate date = LocalDateTime.ofInstant(sunriseSunsetResponse.request.Date.toInstant(), ZoneId.systemDefault()).toLocalDate();

        solarTime.LocationId = locationItem.Id;
        solarTime.Date = date;
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

    private class LocationIdDatePairExistsTask extends AsyncTask<Object, Void, Boolean>{
        Location location;
        Calendar date;
        LocalDate localDate;
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Object... objects) {
            location = (Location) objects[0];
            date = (Calendar) objects[1];
            localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            Boolean result = false;
            try{
                result = solarTimeRepository.isLocationIDDatePairExists(location.Id, localDate);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Location / Date Pair exists!", Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }

    private class GetSolarTimeTask extends AsyncTask<Object, Void, SolarTime>{

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Object... objects) {
            int locationId = (Integer) objects[0];
            LocalDate localDate = (LocalDate) objects[1];
            return solarTimeRepository.getSolarTime(locationId, localDate);
        }
    }

    private class SolarAlarmNameExistsTask extends AsyncTask<SolarAlarm, Void, Boolean>{
        SolarAlarm solarAlarmItem;

        @Override
        protected Boolean doInBackground(SolarAlarm... solarAlarms) {
            solarAlarmItem = solarAlarms[0];
            Boolean result = false;
            try{
                result = solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarmItem.Name, solarAlarmItem.LocationId);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Solar Alarm already exists!", Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }
}
