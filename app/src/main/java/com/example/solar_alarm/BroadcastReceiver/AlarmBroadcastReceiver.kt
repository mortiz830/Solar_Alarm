package com.example.solar_alarm.BroadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.solar_alarm.Activities.RingActivity
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R
import java.util.Calendar

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private lateinit var solarAlarm: SolarAlarm

    override fun onReceive(context: Context, intent: Intent) {
        // Ensure Notification Channel exists (Crucial for Android 8+)
        createNotificationChannel(context)

        try {
            // 1. Initialize data
            solarAlarm = GetSolarAlarmFromIntent(intent)
            Log.d("ALARM_DEBUG", "Received alarm: ${solarAlarm.Name}")

            // 2. Logic check: If it's recurring, check if today is an active day
            if (solarAlarm.Recurring && !alarmIsToday()) {
                Log.d("ALARM_DEBUG", "Alarm skipped: Not scheduled for today.")
                return
            }

            // 3. Play Music (Using your MusicControl singleton)
            MusicControl.getInstance(context).PlayMusic(context)
            Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show()

            // 4. Build the Full Screen Intent for RingActivity
            val ringIntent = Intent(context, RingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("SolarAlarm", solarAlarm)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                solarAlarm.Id, // Unique ID for this alarm
                ringIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 5. Build and Show the Notification
            val builder = NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Solar Alarm")
                .setContentText(solarAlarm.Name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true) // Pops the screen
                .setAutoCancel(true)
                .setOngoing(true) // Keeps notification visible

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(123, builder.build())

        } catch (e: Exception) {
            Log.e("ALARM_DEBUG", "Error in onReceive: ${e.message}")
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "alarm_channel",
            "Solar Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for Solar Alarm triggers"
            setSound(null, null) // We handle sound manually via MusicControl
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun alarmIsToday(): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar[Calendar.DAY_OF_WEEK]

        return when (today) {
            Calendar.MONDAY    -> solarAlarm.Monday
            Calendar.TUESDAY   -> solarAlarm.Tuesday
            Calendar.WEDNESDAY -> solarAlarm.Wednesday
            Calendar.THURSDAY  -> solarAlarm.Thursday
            Calendar.FRIDAY    -> solarAlarm.Friday
            Calendar.SATURDAY  -> solarAlarm.Saturday
            Calendar.SUNDAY    -> solarAlarm.Sunday
            else               -> false
        }
    }

    companion object {
        fun GetSolarAlarmFromIntent(intent: Intent): SolarAlarm {
            val alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("SolarAlarm", SolarAlarm::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<SolarAlarm>("SolarAlarm")
            }
            return alarm ?: throw NullPointerException("SolarAlarm data was null in Intent")
        }
    }
}

// Keeping your MusicControl and MediaNotificationReceiver as they were (mostly)
class MusicControl private constructor(private var context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        @Volatile
        private var INSTANCE: MusicControl? = null

        fun getInstance(context: Context): MusicControl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicControl(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun PlayMusic(context: Context) {
        stopMusic() // Stop any current playback first
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
