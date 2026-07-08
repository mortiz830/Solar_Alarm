package com.example.solar_alarm.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.R
import com.example.solar_alarm.Service.AlarmService
import java.util.Calendar

class AlarmBroadcastReceiver : BroadcastReceiver() {
    private lateinit var solarAlarm: SolarAlarm

    override fun onReceive(context: Context, intent: Intent) {
        try {
            // 1. Initialize data
            val dd = GetSolarAlarmFromIntent(intent);

            if (dd != null)
            {
                solarAlarm = dd
                Log.d("ALARM_DEBUG", "Received alarm: ${solarAlarm.Name}")

                // 2. Logic check: If it's recurring, check if today is an active day
                if (solarAlarm.Recurring && !alarmIsToday()) {
                    Log.d("ALARM_DEBUG", "Alarm skipped: Not scheduled for today.")
                    return
                }

                // 3. Start the AlarmService to handle notification, music and full screen intent
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("SolarAlarm", solarAlarm)
                }

                context.startForegroundService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("ALARM_DEBUG", "Error in onReceive: ${e.message}")
        }
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
        fun GetSolarAlarmFromIntent(intent: Intent): SolarAlarm?
        {
            val solarAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                intent.getParcelableExtra("SolarAlarm", SolarAlarm::class.java)
            }
            else
            {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<SolarAlarm>("SolarAlarm")
            }
            return solarAlarm //?: throw NullPointerException("SolarAlarm data was null in Intent")
        }
    }
}

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
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer?.setAudioAttributes(audioAttributes)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopMusic() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("MusicControl", "Error stopping music", e)
        } finally {
            mediaPlayer = null
        }
    }
}
