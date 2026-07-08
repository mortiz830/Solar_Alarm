package com.example.solar_alarm.Service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.solar_alarm.Activities.RingActivity
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.BroadcastReceiver.MusicControl
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp

class AlarmService : Service()
{
    private lateinit var solarAlarm: SolarAlarm
    private var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY

        try {
            solarAlarm = AlarmBroadcastReceiver.GetSolarAlarmFromIntent(intent) as SolarAlarm
            
            // 1. Create the Intent for RingActivity with proper flags
            val ringIntent = Intent(this, RingActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                             Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                             Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("SolarAlarm", solarAlarm)
            }
            
            // 2. Create the PendingIntent
            val pendingIntent = PendingIntent.getActivity(
                this, 
                solarAlarm.Id, 
                ringIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 3. Build the high-priority notification with Full Screen Intent
            val notification: Notification = NotificationCompat.Builder(this, SolarAlarmApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(solarAlarm.Name)
                .setContentText("Solar Alarm is ringing!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build()

            // 4. Start Foreground Service
            startForeground(solarAlarm.Id + 1000, notification)

            // 5. Play Sound and Vibrate
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator?.let {
                val pattern = longArrayOf(0, 500, 500)
                it.vibrate(pattern, 0)
            }

            MusicControl.getInstance(this).PlayMusic(this)

            // 6. Force start the activity
            startActivity(ringIntent)

        } catch (e: Exception) {
            Log.e("AlarmService", "Error in onStartCommand", e)
        }
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicControl.getInstance(this).stopMusic()
        vibrator?.cancel()
    }

    override fun onBind(intent: Intent): IBinder? = null
}
