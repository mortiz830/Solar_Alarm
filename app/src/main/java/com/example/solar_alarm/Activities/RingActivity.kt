package com.example.solar_alarm.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.Data.Alarm
import android.content.Intent
import com.example.solar_alarm.Service.AlarmService
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.widget.*
import java.util.*

class RingActivity : AppCompatActivity() {
    @kotlin.jvm.JvmField
    @BindView(R.id.activity_ring_dismiss)
    var dismiss: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.activity_ring_snooze)
    var snooze: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.activity_ring_clock)
    var clock: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)
        ButterKnife.bind(this)
        dismiss!!.setOnClickListener { StopService() }
        snooze!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, 10)
            val alarm = Alarm(
                    Random().nextInt(Int.MAX_VALUE),
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE],
                    "Snooze",
                    System.currentTimeMillis(),
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            )
            alarm.schedule(applicationContext)
            StopService()
        }
        animateClock()
    }

    private fun StopService() {
        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }

    private fun animateClock() {
        val rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }
}