package com.example.solar_alarm.Data

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlin.jvm.Volatile
import androidx.room.Room
import android.content.Context
import com.example.solar_alarm.Data.Tables.Location
import com.example.solar_alarm.Data.Tables.OffsetType
import com.example.solar_alarm.Data.Tables.SolarAlarm
import java.util.concurrent.Executors

@RequiresApi(api = Build.VERSION_CODES.O)
@Database(entities = [Location::class, OffsetType::class, SolarAlarm::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao?

    companion object {
        @Volatile
        private lateinit var INSTANCE: AlarmDatabase
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getDatabase(context: Context): AlarmDatabase
        {
            if (INSTANCE == null)
            {
                synchronized(AlarmDatabase::class.java)
                {
                    if (INSTANCE == null)
                    {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AlarmDatabase::class.java,
                                "alarm_database")
                                .build()
                    }
                }
            }

            return INSTANCE
        }
    }
}