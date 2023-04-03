package com.example.solar_alarm.Activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.Data.ViewModels.LocationViewModelFactory
import com.example.solar_alarm.R
import com.example.solar_alarm.SolarAlarmApp


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity()
{
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory((application as SolarAlarmApp).locationRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // https://github.com/googlecodelabs/android-room-with-a-view/blob/kotlin/app/src/main/java/com/example/android/roomwordssample/WordListAdapter.kt#L28
        locationViewModel.All.observe(owner = this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.submitList(it) }
        }
    }
}