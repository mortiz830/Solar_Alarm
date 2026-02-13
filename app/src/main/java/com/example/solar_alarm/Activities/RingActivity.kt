package com.example.solar_alarm.Activities

import android.app.KeyguardManager // Fixed the undefined error
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.AlarmBroadcastReceiver.MusicControl
import com.example.solar_alarm.R
import com.example.solar_alarm.Service.AlarmService

class RingActivity : AppCompatActivity() {

    @kotlin.jvm.JvmField
    @BindView(R.id.activity_ring_dismiss)
    var dismiss: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.activity_ring_snooze)
    var snooze: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup Lockscreen Visibility BEFORE setContentView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        setContentView(R.layout.activity_ring)

        // 2. Initialize ButterKnife so buttons aren't null
        ButterKnife.bind(this)

        // 3. Set up Click Listeners
        dismiss?.setOnClickListener {
            dismissAlarm()
        }

        snooze?.setOnClickListener {
            // Add your snooze logic here if needed
            dismissAlarm()
        }
    }

    private fun dismissAlarm() {
        // Stop the music!
        MusicControl.getInstance(this).stopMusic()

        // Stop the background service
        val intentService = Intent(applicationContext, AlarmService::class.java)
        stopService(intentService)

        // Close the activity
        finish()
    }
}