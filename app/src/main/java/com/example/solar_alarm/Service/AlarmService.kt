package com.example.solar_alarm.Service

import com.example.solar_alarm.R
import android.content.Intent
import android.app.*
import android.content.Context
import com.example.solar_alarm.Application.App
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import com.example.solar_alarm.Activities.RingActivity
import androidx.core.app.NotificationCompat
import android.os.IBinder
import android.util.Log
import com.example.solar_alarm.Application.App.Companion.CHANNEL_ID

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return try{
        val notificationIntent = Intent(this, RingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val alarmTitle = String.format("%s Alarm", intent.getStringExtra(AlarmBroadcastReceiver.Companion.TITLE))
        val notification: Notification = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID)
                .setContentTitle(alarmTitle)
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