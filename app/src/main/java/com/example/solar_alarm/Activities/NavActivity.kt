package com.example.solar_alarm.Activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.solar_alarm.AlarmList.AlarmListFragment
import com.example.solar_alarm.CreateAlarm.CreateAlarmFragment
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory

import com.example.solar_alarm.Data.ViewModels.MainViewModel
import com.example.solar_alarm.Location.AddLocationFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.ActivityBottomNavigationBinding



// Main activity for the app.

@RequiresApi(Build.VERSION_CODES.O)
class NavActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory((application as SolarAlarmApp).locationRepository)
    }

    private lateinit var binding : ActivityBottomNavigationBinding

    //@OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(AlarmListFragment())

        binding.navView.setOnItemSelectedListener {
            when (it.itemId)
            {
                R.id.navigation_home         -> replaceFragment(AlarmListFragment())
                R.id.navigation_location     -> replaceFragment(AddLocationFragment(locationViewModel))
                R.id.navigation_create_alarm -> replaceFragment(CreateAlarmFragment())
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