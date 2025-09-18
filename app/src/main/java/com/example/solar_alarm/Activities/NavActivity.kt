package com.example.solar_alarm.Activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.solar_alarm.AlarmList.SolarAlarmListFragment
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment
import com.example.solar_alarm.Data.ViewModels.LocationListViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory

import com.example.solar_alarm.Data.ViewModels.MainViewModel
import com.example.solar_alarm.Data.ViewModels.SolarAlarmViewModel
import com.example.solar_alarm.Data.ViewModels.SolarAlarmViewModelFactory
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModel
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModelFactory
import com.example.solar_alarm.Location.LocationListFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp
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

        replaceFragment(SolarAlarmListFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))

        binding.navView.setOnItemSelectedListener {
            when (it.itemId)
            {
//                R.id.navigation_home         -> replaceFragment(AlarmListFragment())
                R.id.navigation_home         -> replaceFragment(SolarAlarmListFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))
                R.id.navigation_location     -> replaceFragment(LocationListFragment(locationListViewModel))//replaceFragment(AddLocationFragment(locationViewModel))
                R.id.navigation_create_alarm -> replaceFragment(CreateAlarmFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))

                // NEED TO CHANGE ICON AND ADD NEW SCREE FOR SOLAR TIMES
                R.id.navigation_solar_times -> replaceFragment(CreateAlarmFragment(locationListViewModel, solarTimeViewModel, solarAlarmViewModel))
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