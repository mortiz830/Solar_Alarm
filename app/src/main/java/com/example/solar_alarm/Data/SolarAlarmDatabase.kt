package com.example.solar_alarm.Data

import androidx.annotation.RequiresApi
import android.os.Build
import com.example.solar_alarm.Data.Daos.LocationDao
import com.example.solar_alarm.Data.Daos.StaticDataDao
import com.example.solar_alarm.Data.Daos.SolarAlarmDao
import com.example.solar_alarm.Data.Daos.SolarTimeDao
import androidx.room.Database
import androidx.room.RoomDatabase
//import com.example.solar_alarm.Data.Migrations.StaticDataMigration
import androidx.room.TypeConverters
import kotlin.jvm.Volatile
import androidx.room.Room
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solar_alarm.Data.Tables.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

        fun getDatabase(context: Context, scope: CoroutineScope): SolarAlarmDatabase
        {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SolarAlarmDatabase::class.java,
                    "word_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    //.addMigrations(StaticDataMigration.Companion.MIGRATION_1_2)
                    //.addCallback(SolarAlarmDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class SolarAlarmDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback()
    {
        override fun onCreate(db: SupportSQLiteDatabase)
        {
            super.onCreate(db)
            INSTANCE?.let { database -> scope.launch { populateDatabase(database.locationDao()) } }
        }

        suspend fun populateDatabase(locationDao: LocationDao) {
            // Delete all content here.
            //locationDao.deleteAll()

            // Add sample words.
            //var word = Word("Hello")
            //wordDao.insert(word)
            //word = Word("World!")
            //wordDao.insert(word)

            // TODO: Add your own words!
        }
    }
}
