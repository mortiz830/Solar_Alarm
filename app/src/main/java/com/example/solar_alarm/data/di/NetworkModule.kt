package com.example.solar_alarm.data.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.solar_alarm.sunrise_sunset_http.HttpRequests
import com.example.solar_alarm.sunrise_sunset_http.SolarRemoteDataSource
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideSolarRemoteDataSource(
        client: OkHttpClient,
        gson: Gson
    ): SolarRemoteDataSource {
        return HttpRequests(client, gson)
    }
}
