package com.example.solar_alarm.data.di

import android.content.Context
import com.example.solar_alarm.data.SolarAlarmDatabase
import com.example.solar_alarm.data.daos.LocationDao
import com.example.solar_alarm.data.daos.SolarAlarmDao
import com.example.solar_alarm.data.daos.SolarTimeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): SolarAlarmDatabase {
        return SolarAlarmDatabase.getDatabase(context, scope)
    }

    @Provides
    fun provideLocationDao(database: SolarAlarmDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    fun provideSolarAlarmDao(database: SolarAlarmDatabase): SolarAlarmDao {
        return database.solarAlarmDao()
    }

    @Provides
    fun provideSolarTimeDao(database: SolarAlarmDatabase): SolarTimeDao {
        return database.solarTimeDao()
    }
}
