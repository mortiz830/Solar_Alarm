package com.example.solar_alarm.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.solar_alarm.Activities.RingActivity
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.Application.App
import com.example.solar_alarm.Application.App.Companion.CHANNEL_ID
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R

class AlarmService : Service()
{
    private lateinit var solarAlarm: SolarAlarm
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return try
        {
            val notificationIntent = Intent(this, RingActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_MUTABLE)
            solarAlarm = AlarmBroadcastReceiver.Companion.GetSolarAlarmFromIntent(intent)
            val notification : Notification = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID)
                                                                .setContentTitle(solarAlarm.Name)
                                                                .setContentText("Ring Ring .. Ring Ring")
                                                                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                                                                .setContentIntent(pendingIntent)
                                                                .build()

        startForeground(1, notification)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val pattern = longArrayOf(0, 100, 1000)
                    it.vibrate(pattern, 0)
                }
                else {
                    @Suppress("DEPRECATION")
                    it.vibrate(5000)
                }
            }

            mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
            mediaPlayer?.start()

            START_STICKY
        }catch(e: Exception){
            Log.e("AlarmService", "Error in onStartCommand", e)
            START_STICKY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        vibrator?.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val manager = getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(serviceChannel)
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Error creating notification channel", e)
        }
    }
}