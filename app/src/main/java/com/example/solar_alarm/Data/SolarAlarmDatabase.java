package com.example.solar_alarm.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.solar_alarm.Data.Repositories.LocationDao;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Location.class, SolarAlarm.class, SolarTime.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class SolarAlarmDatabase extends RoomDatabase
{
    public abstract LocationDao locationDao();

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
                }
            }
        }

        return INSTANCE;
    }
}
