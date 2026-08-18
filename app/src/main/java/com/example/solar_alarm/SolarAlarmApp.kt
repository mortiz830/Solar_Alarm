package com.example.solar_alarm

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SolarAlarmApp : Application()
{
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Solar Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for critical alarm notifications"
                setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private var context: Context? = null
        const val CHANNEL_ID = "SOLAR_ALARM_CHANNEL_V2"
        fun getAppContext(): Context? {
            return context
        }
    }
}
