package com.example.solar_alarm.Activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.solar_alarm.BroadcastReceiver.MusicControl
import com.example.solar_alarm.Service.AlarmService
import com.example.solar_alarm.databinding.ActivityRingBinding

class RingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        // 1. Setup Lockscreen Visibility
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

        // 2. Initialize View Binding
        binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Set up Click Listeners using binding
        binding.activityRingDismiss.setOnClickListener {
            dismissAlarm()
        }

        binding.activityRingSnooze.setOnClickListener {
            dismissAlarm()
        }
    }

    private fun dismissAlarm() {
        MusicControl.getInstance(this).stopMusic()
        val intentService = Intent(applicationContext, AlarmService::class.java)
        // NEED SolarAlarm object or ID IN HERE
        // send to CreateAlarmFragment.UpdateAlarmAfterDismiss() to be updated and reset in broadcast receiver
        stopService(intentService)
        finish()
    }
}
