package com.example.solar_alarm.solarTime

// Repair: Fixed broken package/import lines
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
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModel
import com.example.solar_alarm.data.viewmodels.SolarTimeViewModelFactory
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentSolarTimeBinding

class SolarTimeFragment : Fragment()
{
    private lateinit var fragmentSolarTimeBinding    : FragmentSolarTimeBinding
    private lateinit var solarTimeAdapter            : SolarTimeAdapter
    private lateinit var recyclerView                : RecyclerView

    private val solarTimeViewModel: SolarTimeViewModel by activityViewModels {
        SolarTimeViewModelFactory((requireActivity().application as SolarAlarmApp).solarTimeRepository)
    }

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
            solarTimeViewModel.allSolarTimes.observe(viewLifecycleOwner, androidx.lifecycle.Observer { solarTimes -> solarTimeAdapter.updateSolarTimes(solarTimes)})
        }
        catch (e: Exception)
        {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }

        return fragmentSolarTimeBinding.getRoot()
    }

}
