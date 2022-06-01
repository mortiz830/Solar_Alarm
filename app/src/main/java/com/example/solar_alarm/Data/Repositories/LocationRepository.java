package com.example.solar_alarm.Data.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.solar_alarm.Data.Daos.LocationDao;
import com.example.solar_alarm.Data.Daos.StaticDataDao;
import com.example.solar_alarm.Data.Enums.OffsetTypeEnum;
import com.example.solar_alarm.Data.Enums.SolarTimeTypeEnum;
import com.example.solar_alarm.Data.Enums.TimeUnitTypeEnum;
import com.example.solar_alarm.Data.SolarAlarmDatabase;
import com.example.solar_alarm.Data.Tables.OffsetType;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarTimeType;
import com.example.solar_alarm.Data.Tables.TimeUnitType;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationRepository
{
    private final LocationDao locationDao;
    private final StaticDataDao staticDataDao;
    private final LiveData<List<Location>> locationsLiveData;

    public LocationRepository(Application application)
    {
        SolarAlarmDatabase db = SolarAlarmDatabase.getDatabase(application);
        locationDao = db.locationDao();
        staticDataDao = db.staticDataDao();
        locationsLiveData = locationDao.getAll();

        AddStaticData();
    }

    public void Insert(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.Insert(location));
    }

    public void Update(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.Update(location));
    }

    public void delete(Location location)
    {
        SolarAlarmDatabase.databaseWriteExecutor.execute(() -> locationDao.delete(location));
    }

    public LiveData<List<Location>> getAll() { return locationsLiveData; }

    public boolean isLocationNameExists(String name)
    {
        return locationDao.isLocationNameExists(name);
    }

    public boolean isLocationLatitudeExists(double latitude)
    {
        return locationDao.isLocationLatitudeExists(latitude);
    }

    public boolean isLocationLongitudeExists(double longitude)
    {
        return locationDao.isLocationLongitudeExists(longitude);
    }

    private void AddStaticData()
    {
        try {
            new IsTimeUnitTypesExistsTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class IsTimeUnitTypesExistsTask extends AsyncTask<Double, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Double... doubles)
        {
            try
            {
                if (!staticDataDao.isTimeUnitTypesExists())
                {
                    for (TimeUnitTypeEnum enumType : TimeUnitTypeEnum.values())
                    {
                        TimeUnitType x = new TimeUnitType();
                        x.Id   = enumType.Id;
                        x.Name = enumType.Name;

                        staticDataDao.Insert(x);
                    }
                }

                //--------------------------

                if (!staticDataDao.isOffsetTypesExists())
                {
                    for (OffsetTypeEnum enumType : OffsetTypeEnum.values())
                    {
                        OffsetType x = new OffsetType();
                        x.Id   = enumType.Id;
                        x.Name = enumType.Name;

                        staticDataDao.Insert(x);
                    }
                }

                //--------------------------

                if (!staticDataDao.isSolarTimeTypesExists())
                {
                    for (SolarTimeTypeEnum enumType : SolarTimeTypeEnum.values())
                    {
                        SolarTimeType x = new SolarTimeType();
                        x.Id   = enumType.Id;
                        x.Name = enumType.Name;

                        staticDataDao.Insert(x);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return true;
        }
    }
}
