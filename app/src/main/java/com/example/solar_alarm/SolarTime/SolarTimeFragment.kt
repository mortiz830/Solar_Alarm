package com.example.solar_alarm.SolarTime

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.ViewModels.SolarTimeViewModel
import com.example.solar_alarm.R
import com.example.solar_alarm.databinding.FragmentSolarTimeBinding

class SolarTimeFragment constructor(private var solarTimeViewModel: SolarTimeViewModel) : Fragment()
{
    private lateinit var fragmentSolarTimeBinding    : FragmentSolarTimeBinding
    private lateinit var solarTimeAdapter            : SolarTimeAdapter
    private lateinit var recyclerView                : RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        fragmentSolarTimeBinding  = FragmentSolarTimeBinding.inflate(layoutInflater, container, false)
        solarTimeAdapter          = SolarTimeAdapter(emptyList())
        recyclerView              = fragmentSolarTimeBinding.fragmentSolartimeRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(solarTimeAdapter)

        try
        {
            solarTimeViewModel.AllSolarTimes.observe(viewLifecycleOwner, androidx.lifecycle.Observer { solarTimes -> solarTimeAdapter.UpdateSolarTimes(solarTimes)})
        }
        catch (e: Exception)
        {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }

        return fragmentSolarTimeBinding.getRoot()
    }

}