package com.example.solar_alarm.data.di

import com.example.solar_alarm.data.repositories.LocationRepository
import com.example.solar_alarm.data.repositories.LocationRepositoryImpl
import com.example.solar_alarm.data.repositories.SolarAlarmRepository
import com.example.solar_alarm.data.repositories.SolarAlarmRepositoryImpl
import com.example.solar_alarm.data.repositories.SolarTimeRepository
import com.example.solar_alarm.data.repositories.SolarTimeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindSolarAlarmRepository(
        solarAlarmRepositoryImpl: SolarAlarmRepositoryImpl
    ): SolarAlarmRepository

    @Binds
    @Singleton
    abstract fun bindSolarTimeRepository(
        solarTimeRepositoryImpl: SolarTimeRepositoryImpl
    ): SolarTimeRepository
}
