package com.example.solar_alarm.CreateAlarm;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
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
    Spinner locationSpinner;
    @BindView(R.id.fragment_createalarm_sunrise_data)
    TextView sunriseData;
    @BindView(R.id.fragment_createalarm_solarnoon_data)
    TextView solarNoonData;
    @BindView(R.id.fragment_createalarm_sunset_data)
    TextView sunsetData;
    @BindView(R.id.fragment_createalarm_set_hours)
    NumberPicker setHours;
    @BindView(R.id.fragment_createalarm_set_mins)
    NumberPicker setMins;
    SpinnerAdapter locationSpinnerAdapter;
    ArrayAdapter<CharSequence> alarmTimeAdapter;
    ArrayAdapter<CharSequence> setTimeAdapter;

    private List<Location> Locations;
    private SolarTimeRepository solarTimeRepository;
    private SolarAlarmRepository solarAlarmRepository;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locations = new ArrayList<>();
        solarTimeRepository = new SolarTimeRepository();
        solarAlarmRepository = new SolarAlarmRepository();

        LocationRepository locationRepository = new LocationRepository();
        locationRepository.getAll().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                Locations = locations;
                locationSpinnerAdapter = new SpinnerAdapter(getActivity(), Locations);
                locationSpinner.setAdapter(locationSpinnerAdapter);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createalarm, container, false);

        Spinner alarmTimeSpinner = (Spinner) view.findViewById(R.id.fragment_createalarm_alarmtime_spinner);
        alarmTimeSpinner.setAdapter(new ArrayAdapter<OffsetTypeEnum>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, OffsetTypeEnum.values()));

        Spinner setTimeSpinner = (Spinner) view.findViewById(R.id.fragment_createalarm_settime_spinner);
        setTimeSpinner.setAdapter(new ArrayAdapter<SolarTimeTypeEnum>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item,SolarTimeTypeEnum.values()));

        List<SolarTime> solarTimes = new ArrayList<SolarTime>();
        ButterKnife.bind(this, view);
        setPickers();
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int locationPosition, long l) {
                Location locationItem = (Location) adapterView.getItemAtPosition(locationPosition);
                LocalDate date = LocalDate.now();

                for (int i = 0; i < 14; i++)
                {
                    try {
                        if(solarTimes.size() == 14)
                        {
                            solarTimes.set(i, getSolarTime(locationItem, date));
                        }
                        else
                            solarTimes.add(getSolarTime(locationItem, date));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Solar Time exists!", Toast.LENGTH_LONG).show();
                    }

                    date = date.plusDays(1);
                }

                try
                {
                    sunriseData.setText(solarTimes.get(0).GetLocalZonedDateTime(SolarTimeTypeEnum.Sunrise).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
                    solarNoonData.setText(solarTimes.get(0).GetLocalZonedDateTime(SolarTimeTypeEnum.SolarNoon).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
                    sunsetData.setText(solarTimes.get(0).GetLocalZonedDateTime(SolarTimeTypeEnum.Sunset).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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

        alarmTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(adapterView.getItemAtPosition(position).toString().equals("Before")
                        || adapterView.getItemAtPosition(position).toString().equals("After"))
                {
                    setHours.setVisibility(View.VISIBLE);
                    setMins.setVisibility(View.VISIBLE);
                }
                else
                {
                    setHours.setVisibility(View.GONE);
                    setMins.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        scheduleAlarm.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                OffsetTypeEnum alarmTimeItem = (OffsetTypeEnum) alarmTimeSpinner.getSelectedItem();
                SolarTimeTypeEnum solarTimeTypeItem = (SolarTimeTypeEnum) setTimeSpinner.getSelectedItem();
                for(int i = 0; i < solarTimes.size(); i++)
                {
                    try {
                        scheduleAlarm(solarTimes.get(i), alarmTimeItem, solarTimeTypeItem);
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
    public SolarTime getSolarTime(Location locationItem, LocalDate date) throws Exception {
        boolean isLocationIdDatePairExists = getLocationIdDatePareExists(locationItem, date);
        SolarTime solarTime;

        if(!isLocationIdDatePairExists) {
            try {
                SunriseSunsetRequest sunriseSunsetRequest = new SunriseSunsetRequest((float) locationItem.Latitude, (float) locationItem.Longitude, date);
                solarTime = new TimeResponseTask().execute(sunriseSunsetRequest, locationItem).get();
                solarTimeRepository.Insert(solarTime);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            solarTime = new GetSolarTimeTask().execute(locationItem.Id, date).get();
        }

        return solarTime;
    }

    public boolean getLocationIdDatePareExists(Location locationItem, LocalDate date) throws Exception {
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
    private void scheduleAlarm(SolarTime solarTimeItem, OffsetTypeEnum alarmTypeId, SolarTimeTypeEnum solarTimeTypeId) throws Exception {
        SolarAlarm solarAlarmItem = new SolarAlarm();
        boolean isSolarAlarmNameLocationIdPairExists;

        solarAlarmItem.Name = title.getText().toString();
        solarAlarmItem.Active = true;
        solarAlarmItem.LocationId = solarTimeItem.LocationId;
        solarAlarmItem.SolarTimeId = solarTimeItem.Id;
        solarAlarmItem.Recurring = recurring.isChecked();
        solarAlarmItem.Monday = mon.isChecked();
        solarAlarmItem.Tuesday = tue.isChecked();
        solarAlarmItem.Wednesday = wed.isChecked();
        solarAlarmItem.Thursday = thu.isChecked();
        solarAlarmItem.Friday = fri.isChecked();
        solarAlarmItem.Saturday = sat.isChecked();
        solarAlarmItem.Sunday = sun.isChecked();
        solarAlarmItem.OffsetTypeId = alarmTypeId;
        solarAlarmItem.SolarTimeTypeId = solarTimeTypeId;

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

        AlarmScheduler alarmScheduler = new AlarmScheduler(solarAlarmItem, solarTimeItem, setHours.getValue(), setMins.getValue());

        alarmScheduler.schedule(getContext());
    }

    private class TimeResponseTask extends AsyncTask<Object, Void, SolarTime> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Object... objects) {
            SolarTime solarTime = null;
            try {
                SunriseSunsetRequest  sunriseSunsetRequest  = (SunriseSunsetRequest) objects[0];
                Location              location              = (Location) objects[1];
                HttpRequests          httpRequests          = new HttpRequests(sunriseSunsetRequest);
                SunriseSunsetResponse sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest);

                solarTime = new SolarTime(location, sunriseSunsetResponse);
            } catch (Exception e) {
                e.printStackTrace();
              //  Toast.makeText(getContext(), "Unable to get times!", Toast.LENGTH_LONG).show();
            }

            return solarTime;
        }
    }

    private class LocationIdDatePairExistsTask extends AsyncTask<Object, Void, Boolean>{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Object... objects) {
            Location location = (Location) objects[0];
            LocalDate localDate = (LocalDate) objects[1];
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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(SolarAlarm... solarAlarms) {
            Boolean result = false;
            try{
                SolarAlarm solarAlarmItem = solarAlarms[0];
                result = solarAlarmRepository.isSolarAlarmNameLocationIDExists(solarAlarmItem.Name, solarAlarmItem.LocationId);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), "Solar Alarm already exists!", Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }

    public void setPickers()
    {
//        setHours = new NumberPicker(getActivity().getApplicationContext());
//        setMins = new NumberPicker(getActivity().getApplicationContext());
//        setHours.findViewById(R.id.fragment_createalarm_set_hours);
//        setMins.findViewById(R.id.fragment_createalarm_set_mins);
        setHours.setMinValue(0);
        setHours.setMaxValue(23);
        setMins.setMinValue(0);
        setMins.setMaxValue(59);
    }
}
