package com.example.solar_alarm.Activities

import android.os.Build
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
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
import com.example.solar_alarm.Location.LocationListFragment
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.ActivityBottomNavigationBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


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

        binding..setOnClickListener { view ->
            showPopupMenu(view, binding.fab)
        }

        binding.navView.setOnItemSelectedListener {
            when (it.itemId)
            {
//                R.id.navigation_home         -> replaceFragment(AlarmListFragment())
                R.id.navigation_home         -> replaceFragment(LocationListFragment())
                R.id.navigation_location     -> replaceFragment(LocationListFragment())//replaceFragment(AddLocationFragment(locationViewModel))
                R.id.navigation_create_alarm -> replaceFragment(CreateAlarmFragment(locationViewModel))
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
    private fun showPopupMenu(view: android.view.View, fab: Button) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.fab_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_option_create_location -> {
                    replaceFragment(AddLocationFragment(locationViewModel))
                    //fab.hide()
                    true
                }
                R.id.action_option_create_alarm -> {
                    replaceFragment(CreateAlarmFragment(locationViewModel))
                    //fab.hide()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}