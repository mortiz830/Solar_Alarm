package com.example.solar_alarm.service

// Repair: Fixed broken package/import lines
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.solar_alarm.activities.RingActivity
import com.example.solar_alarm.broadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.broadcastReceiver.MusicControl
import com.example.solar_alarm.createAlarm.AlarmScheduler
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service()
{
    private var solarAlarm: SolarAlarm? = null
    private var vibrator: Vibrator? = null

    @Inject
    lateinit var rescheduleHelper: RescheduleHelper

    companion object {
        const val ACTION_DISMISS = "com.example.solar_alarm.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.example.solar_alarm.ACTION_SNOOZE"
        const val ACTION_CLOSE_RING_ACTIVITY = "com.example.solar_alarm.ACTION_CLOSE_RING_ACTIVITY"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY

        val action = intent.action
        if (action == ACTION_DISMISS) {
            val alarm = AlarmBroadcastReceiver.getSolarAlarmFromIntent(intent) ?: solarAlarm
            if (alarm != null) {
                AlarmScheduler.cancelSnooze(applicationContext, alarm.Id)
                CoroutineScope(Dispatchers.IO).launch {
                    rescheduleHelper.rescheduleNext(applicationContext, alarm)
                }
            }
            sendBroadcast(Intent(ACTION_CLOSE_RING_ACTIVITY).setPackage(packageName))
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        if (action == ACTION_SNOOZE) {
            val alarm = AlarmBroadcastReceiver.getSolarAlarmFromIntent(intent) ?: solarAlarm
            if (alarm != null) {
                AlarmScheduler.snooze(applicationContext, alarm)
            }
            sendBroadcast(Intent(ACTION_CLOSE_RING_ACTIVITY).setPackage(packageName))
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        try {
            val alarm = AlarmBroadcastReceiver.getSolarAlarmFromIntent(intent) as SolarAlarm
            solarAlarm = alarm
            val isSnooze = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_SNOOZE, false)
            
            // 1. Create the Intent for RingActivity with proper flags
            val ringIntent = Intent(this, RingActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                             Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                             Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("SolarAlarm", alarm)
                putExtra(AlarmScheduler.EXTRA_IS_SNOOZE, isSnooze)
            }
            
            // 2. Create the PendingIntent
            val pendingIntent = PendingIntent.getActivity(
                this, 
                alarm.Id, 
                ringIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Inline action: Dismiss
            val dismissIntent = Intent(this, AlarmService::class.java).apply {
                this.action = ACTION_DISMISS
                putExtra("SolarAlarm", alarm)
            }
            val dismissPendingIntent = PendingIntent.getService(
                this,
                alarm.Id + 200000,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Inline action: Snooze
            val snoozeIntent = Intent(this, AlarmService::class.java).apply {
                this.action = ACTION_SNOOZE
                putExtra("SolarAlarm", alarm)
            }
            val snoozePendingIntent = PendingIntent.getService(
                this,
                alarm.Id + 300000,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = if (isSnooze) "${alarm.Name} (Snoozed)" else alarm.Name

            // 3. Build the high-priority notification with Full Screen Intent and Actions
            val notification: Notification = NotificationCompat.Builder(this, SolarAlarmApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(title)
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_alarm_black_24dp, "Dismiss", dismissPendingIntent)
                .addAction(R.drawable.ic_alarm_black_24dp, "Snooze", snoozePendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build()

            // 4. Start Foreground Service
            startForeground(alarm.Id + 1000, notification)

            // 5. Play Sound and Vibrate
            @Suppress("DEPRECATION")
            vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                val pattern = longArrayOf(0, 500, 500)
                @Suppress("DEPRECATION")
                it.vibrate(pattern, 0)
            }

            MusicControl.getInstance(this).playMusic(this)

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
