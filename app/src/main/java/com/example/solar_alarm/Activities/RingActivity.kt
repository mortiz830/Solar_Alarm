package com.example.solar_alarm.Activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.solar_alarm.BroadcastReceiver.AlarmBroadcastReceiver
import com.example.solar_alarm.BroadcastReceiver.MusicControl
import com.example.solar_alarm.Data.Tables.SolarAlarm
import com.example.solar_alarm.Service.AlarmService
import com.example.solar_alarm.databinding.ActivityRingBinding

class RingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding
    private lateinit var solarAlarm: SolarAlarm

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
        solarAlarm = AlarmBroadcastReceiver.GetSolarAlarmFromIntent(intent) as SolarAlarm

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

        var tt = intentService.getParcelableExtra<SolarAlarm>("SolarAlarm", SolarAlarm::class.java)

//        // 3. Start the AlarmService to handle notification, music and full screen intent
//        val serviceIntent = Intent(context, AlarmService::class.java).apply {
//            putExtra("SolarAlarm", solarAlarm)
//        }

        val dd = intentService.getParcelableExtra<SolarAlarm>("SolarAlarm");
        
        // Example: Pass the object back to the service or another component
        // intentService.putExtra("SolarAlarm", solarAlarm)

        stopService(intentService)
        finish()
    }
}
