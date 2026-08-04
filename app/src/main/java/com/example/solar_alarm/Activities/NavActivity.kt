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
import com.example.solar_alarm.data.viewmodels.LocationViewModelFactory

import com.example.solar_alarm.data.viewmodels.MainViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModel
import com.example.solar_alarm.data.viewmodels.SolarAlarmViewModelFactory
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModelFactory
import com.example.solar_alarm.location.LocationListFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.solarTime.SolarTimeFragment
import com.example.solar_alarm.databinding.ActivityBottomNavigationBinding


// Main activity for the app.

@RequiresApi(Build.VERSION_CODES.O)
class NavActivity : AppCompatActivity()
{
    private val viewModel: MainViewModel by viewModels()
    private val locationListViewModel : LocationListViewModel by viewModels {LocationViewModelFactory((application as SolarAlarmApp).locationRepository) }
    private val solarTimeViewModel : SolarTimeViewModel by viewModels {SolarTimeViewModelFactory((application as SolarAlarmApp).solarTimeRepository)}
    private val solarAlarmViewModel : SolarAlarmViewModel by viewModels {SolarAlarmViewModelFactory((application as SolarAlarmApp).solarAlarmRepository) }

    private lateinit var binding : ActivityBottomNavigationBinding

    //@OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(SolarAlarmListFragment())

        binding.navView.setOnItemSelectedListener {
            when (it.itemId)
            {
//                R.id.navigation_home         -> replaceFragment(AlarmListFragment())
                R.id.navigation_home         -> replaceFragment(SolarAlarmListFragment())
                R.id.navigation_location     -> replaceFragment(LocationListFragment())
                R.id.navigation_create_alarm -> replaceFragment(CreateAlarmFragment())

                // NEED TO CHANGE ICON AND ADD NEW SCREE FOR SOLAR TIMES
                R.id.navigation_solar_times -> replaceFragment(SolarTimeFragment())
                else -> { }
            }
            true
        }
    }

    internal fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}