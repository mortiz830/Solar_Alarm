package com.example.solar_alarm.Service

import com.example.solar_alarm.R
import android.content.Intent
import android.app.*
import com.example.solar_alarm.Application.App
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import android.media.MediaPlayer
import android.os.Vibrator
import com.example.solar_alarm.Activities.RingActivity
import androidx.core.app.NotificationCompat
import android.os.IBinder

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.setLooping(true)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, RingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val alarmTitle = String.format("%s Alarm", intent.getStringExtra(AlarmBroadcastReceiver.Companion.TITLE))
        val notification: Notification = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setContentText("Ring Ring .. Ring Ring")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .build()
        mediaPlayer!!.start()
        val pattern = longArrayOf(0, 100, 1000)
        vibrator!!.vibrate(pattern, 0)
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        vibrator!!.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}