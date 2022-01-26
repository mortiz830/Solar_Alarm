package com.example.solar_alarm.Data;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.solar_alarm.Data.Daos.LocationDao;
import com.example.solar_alarm.Data.Daos.SolarAlarmDao;
import com.example.solar_alarm.Data.Daos.SolarTimeDao;
import com.example.solar_alarm.Data.Daos.TimeUnitTypeDao;
import com.example.solar_alarm.Data.Daos.TimezoneDao;
import com.example.solar_alarm.Data.Enums.TimeUnitTypeEnum;
import com.example.solar_alarm.Data.Repositories.TimeUnitTypeRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.Data.Tables.TimeUnitType;
import com.example.solar_alarm.Data.Tables.Timezone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database
(
    entities =
    {
            Location.class,
            SolarAlarm.class,
            SolarTime.class,
            Timezone.class,
            TimeUnitType.class
    },
    version = 1,
    exportSchema = false
)

@TypeConverters({Converters.class})
public abstract class SolarAlarmDatabase extends RoomDatabase
{
    public abstract LocationDao locationDao();
    public abstract SolarAlarmDao solarAlarmDao();
    public abstract SolarTimeDao solarTimeDao();
    public abstract TimezoneDao timezoneDao();
    public abstract TimeUnitTypeDao timeUnitTypeDao();

    private static volatile SolarAlarmDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static SolarAlarmDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (SolarAlarmDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SolarAlarmDatabase.class, "SolarAlarmDatabase").build();

                    TimeUnitTypeRepository timeUnitTypeRepository = new TimeUnitTypeRepository((Application) context);

                    for (TimeUnitTypeEnum timeUnitTypeEnum : TimeUnitTypeEnum.values())
                    {
                        TimeUnitType d = new TimeUnitType();
                        d.Name = timeUnitTypeEnum.Name;
                        timeUnitTypeRepository.Insert(d);
                    }
                }
            }
        }

        return INSTANCE;
    }
}
