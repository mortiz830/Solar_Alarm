package com.example.solar_alarm

import android.app.Application
import android.content.Context

class SolarAlarmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        private var context: Context? = null
        fun GetAppContext(): Context? {
            return context
        }
    }
}