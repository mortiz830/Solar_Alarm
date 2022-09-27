package com.example.solar_alarm.Data;

import static androidx.test.InstrumentationRegistry.getContext;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository;
import com.example.solar_alarm.Data.Repositories.SolarTimeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetRequest;
import com.example.solar_alarm.sunrise_sunset_http.SunriseSunsetResponse;

import java.time.LocalDate;

public class AsyncDbAccess
{
    public static class TimeResponseTask extends AsyncTask<Object, Void, SolarTime>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Object... objects)
        {
            SolarTime solarTime = null;
            try
            {
                SunriseSunsetRequest  sunriseSunsetRequest  = (SunriseSunsetRequest) objects[0];
                Location              location              = (Location) objects[1];
                HttpRequests          httpRequests          = new HttpRequests(sunriseSunsetRequest);
                SunriseSunsetResponse sunriseSunsetResponse = httpRequests.GetSolarData(sunriseSunsetRequest);

                solarTime = new SolarTime(location, sunriseSunsetResponse);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //Toast.makeText(getContext(), "Unable to get times!", Toast.LENGTH_LONG).show();
            }

            return solarTime;
        }
    }

    public static class LocationIdDatePairExistsTask extends AsyncTask<Object, Void, Boolean>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Object... objects)
        {
            Location  location  = (Location) objects[0];
            LocalDate localDate = (LocalDate) objects[1];
            Boolean   result    = false;

            try
            {
                result = new SolarTimeRepository().isLocationIDDatePairExists(location.Id, localDate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(), "Location / Date Pair exists!", Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }

    public static class GetSolarTimeByLocationDate extends AsyncTask<Object, Void, SolarTime>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Object... objects)
        {
            int       locationId = (Integer)   objects[0];
            LocalDate localDate  = (LocalDate) objects[1];
            SolarTime solarTime  = new SolarTimeRepository().getSolarTime(locationId, localDate);

            return solarTime;
        }
    }

    public static class SolarAlarmNameExistsTask extends AsyncTask<SolarAlarm, Void, Boolean>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(SolarAlarm... solarAlarms)
        {
            Boolean result = false;
            try
            {
                SolarAlarm solarAlarmItem = solarAlarms[0];
                result = new SolarAlarmRepository().isSolarAlarmNameLocationIDExists(solarAlarmItem.Name, solarAlarmItem.LocationId);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(), "Solar Alarm already exists!", Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }

    public static class GetSolarTime extends AsyncTask<Integer, Void, SolarTime>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected SolarTime doInBackground(Integer... id)
        {
            SolarTime solarTime = null;

            try
            {
                int i = 1;//id[0].intValue();
                SolarTimeRepository solarTimeRepository = new SolarTimeRepository();

                //List<SolarTime> allSolarTimes = solarTimeRepository.getAll().getValue();   // DEBUG_STATEMENT

                solarTime = solarTimeRepository.GetById(i);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return solarTime;
        }
    }

    public static class LocationNameExistsTask extends AsyncTask<String, Void, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected Boolean doInBackground(String... strings )
        {
            Boolean result = false;

            try
            {
                String locationName = strings[0];
                result = new LocationRepository().isLocationNameExists(locationName);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }
    }

    public static class LocationPointExistsTask extends AsyncTask<Double, Void, Boolean>
    {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Double... doubles)
        {
            boolean isLocationLatitudeExists  = false;
            boolean isLocationLongitudeExists = false;

            try
            {
                double latitude  = doubles[0];
                double longitude = doubles[1];

                isLocationLatitudeExists  = new LocationRepository().isLocationLatitudeExists(latitude);
                isLocationLongitudeExists = new LocationRepository().isLocationLongitudeExists(longitude);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return isLocationLatitudeExists && isLocationLongitudeExists;
        }
    }
}
