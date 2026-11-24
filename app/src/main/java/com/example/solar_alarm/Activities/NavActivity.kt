package com.example.solar_alarm.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.solar_alarm.alarmList.SolarAlarmListFragment
import com.example.solar_alarm.createAlarm.CreateAlarmFragment
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.MainViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.location.LocationListFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.solarTime.SolarTimeFragment
import com.example.solar_alarm.databinding.ActivityBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class NavActivity : AppCompatActivity()
{
    private val viewModel: MainViewModel by viewModels()
    private val locationListViewModel : LocationListViewModel by viewModels()
    private val solarTimeViewModel : SolarTimeViewModel by viewModels()
    private val solarAlarmViewModel : SolarAlarmViewModel by viewModels()

    private lateinit var binding : ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(SolarAlarmListFragment())
        }

        checkNotificationPermission()
        checkExactAlarmPermission()

        binding.navView.setOnItemSelectedListener {
            when (it.itemId)
            {
                R.id.navigation_home         -> replaceFragment(SolarAlarmListFragment())
                R.id.navigation_location     -> replaceFragment(LocationListFragment())
                R.id.navigation_create_alarm -> replaceFragment(CreateAlarmFragment())
                R.id.navigation_solar_times -> replaceFragment(SolarTimeFragment())
                else -> { }
            }
            true
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                android.widget.Toast.makeText(
                    this,
                    "Notification permission is recommended so alarms can alert you properly.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!com.example.solar_alarm.util.PermissionUtils.hasNotificationPermission(this)) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!com.example.solar_alarm.util.PermissionUtils.hasExactAlarmPermission(this)) {
                com.example.solar_alarm.util.PermissionUtils.showExactAlarmDialog(this)
            }
        }
    }

    fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
