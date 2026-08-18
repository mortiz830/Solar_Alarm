package com.example.solar_alarm.activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.solar_alarm.broadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.broadcastReceiver.MusicControl
import com.example.solar_alarm.data.tables.SolarAlarm
import com.example.solar_alarm.service.AlarmService
import com.example.solar_alarm.service.RescheduleHelper
import com.example.solar_alarm.databinding.ActivityRingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding
    private lateinit var solarAlarm: SolarAlarm

    @Inject
    lateinit var rescheduleHelper: RescheduleHelper

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
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 2. Initialize View Binding
        binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Get SolarAlarm from Intent
        solarAlarm = AlarmBroadcastReceiver.getSolarAlarmFromIntent(intent) as SolarAlarm

        // 4. Set up Click Listeners using binding
        binding.activityRingDismiss.setOnClickListener {
            dismissAlarm()
        }

        binding.activityRingSnooze.setOnClickListener {
            dismissAlarm()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun dismissAlarm()
    {
        MusicControl.getInstance(this).stopMusic()
        val intentService = Intent(applicationContext, AlarmService::class.java)

        lifecycleScope.launch {
            rescheduleHelper.rescheduleNext(applicationContext, solarAlarm)
            stopService(intentService)
            finish()
        }
    }
}
