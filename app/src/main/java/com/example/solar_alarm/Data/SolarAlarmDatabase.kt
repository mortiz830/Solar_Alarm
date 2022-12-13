package com.example.solar_alarm.Data

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.solar_alarm.Data.Migrations.StaticDataMigration
import androidx.room.TypeConverters
import kotlin.jvm.Volatile
import androidx.room.Room
import android.content.Context
import com.example.solar_alarm.Data.Tables.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RequiresApi(api = Build.VERSION_CODES.O)
@Database(entities = [OffsetType::class, Location::class, SolarAlarm::class, SolarTime::class, SolarTimeType::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SolarAlarmDatabase : RoomDatabase()
{
    abstract fun locationDao(): LocationDao
    abstract fun solarAlarmDao(): SolarAlarmDao
    abstract fun solarTimeDao(): SolarTimeDao
    abstract fun alarmDisplayDataDao(): AlarmDisplayDataDao
    abstract fun staticDataDao(): StaticDataDao

    companion object
    {
        @Volatile
        private var INSTANCE: SolarAlarmDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDatabase(context: Context): SolarAlarmDatabase?
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room.databaseBuilder(context.applicationContext, SolarAlarmDatabase::class.java, "SolarAlarmDatabase")
                        //.addMigrations(StaticDataMigration.Companion.MIGRATION_1_2)
                        .build()
            }

            return INSTANCE
        }
    }
}
