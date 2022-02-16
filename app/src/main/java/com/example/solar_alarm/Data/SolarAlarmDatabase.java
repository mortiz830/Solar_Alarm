package com.example.solar_alarm.Data;

import static com.example.solar_alarm.Data.Migrations.StaticDataMigration.MIGRATION_1_2;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.solar_alarm.Data.Daos.LocationDao;
import com.example.solar_alarm.Data.Daos.SolarAlarmDao;
import com.example.solar_alarm.Data.Daos.SolarTimeDao;
import com.example.solar_alarm.Data.Daos.TimezoneDao;
import com.example.solar_alarm.Data.Tables.AlarmType;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.Data.Tables.SolarTime;
import com.example.solar_alarm.Data.Tables.SolarTimeType;
import com.example.solar_alarm.Data.Tables.TimeUnitType;
import com.example.solar_alarm.Data.Tables.Timezone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
@Database
(
    entities =
    {
        AlarmType.class,
        Location.class,
        SolarAlarm.class,
        SolarTime.class,
        SolarTimeType.class,
        TimeUnitType.class,
        Timezone.class
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
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SolarAlarmDatabase.class, "SolarAlarmDatabase")
                            .addMigrations(MIGRATION_1_2).build();
                }
            }
        }

        return INSTANCE;
    }
}
