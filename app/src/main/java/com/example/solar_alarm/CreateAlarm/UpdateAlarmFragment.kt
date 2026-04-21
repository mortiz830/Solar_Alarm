package com.example.solar_alarm.CreateAlarm

import android.os.Bundle
import com.example.solar_alarm.R
import com.example.solar_alarm.AlarmList.AlarmListViewModel
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel
import androidx.lifecycle.ViewModelProvider
import android.view.View
import androidx.fragment.app.Fragment
import com.example.solar_alarm.databinding.FragmentUpdatealarmBinding

class UpdateAlarmFragment : Fragment() {

    private var _binding: FragmentUpdatealarmBinding? = null
    private val binding get() = _binding!!

    private var updateAlarmViewModel: AlarmListViewModel? = null
    private val updatedAlarm: SolarAlarmDisplayModel? = null
    var location = 0

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        location = bundle!!.getInt("position")
        updateAlarmViewModel = ViewModelProvider(requireParentFragment()).get(AlarmListViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdatealarmBinding.inflate(inflater, container, false)
        
        binding.fragmentUpdatealarmRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.fragmentUpdatealarmRecurringOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateAlarm() {
        // Implementation for updating alarm
    }
}
