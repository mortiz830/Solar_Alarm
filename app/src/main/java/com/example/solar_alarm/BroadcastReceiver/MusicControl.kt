package com.example.solar_alarm.broadcastReceiver

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.solar_alarm.R

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

    fun playMusic(context: Context) {
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
