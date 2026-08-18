package com.example.solar_alarm.solarTime

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.databinding.FragmentSolarTimeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SolarTimeFragment : Fragment()
{
    private var _binding: FragmentSolarTimeBinding? = null
    private val binding get() = _binding!!

    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentSolarTimeBinding.inflate(inflater, container, false)
        val solarTimeAdapter = SolarTimeAdapter(emptyList())
        val recyclerView = binding.fragmentSolartimeRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(solarTimeAdapter)

        try
        {
            solarTimeViewModel.allSolarTimes.observe(viewLifecycleOwner) { solarTimes -> 
                solarTimeAdapter.updateSolarTimes(solarTimes)
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
