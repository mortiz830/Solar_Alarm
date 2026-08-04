package com.example.solar_alarm.location

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
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.data.viewmodels.LocationViewModelFactory
import com.example.solar_alarm.SolarAlarmApp
import com.example.solar_alarm.databinding.FragmentListlocationsBinding

@RequiresApi(Build.VERSION_CODES.O)
class LocationListFragment : Fragment()
{
    private lateinit var fragmentListlocationsBinding : FragmentListlocationsBinding
    private lateinit var locationListAdapter          : LocationListAdapter
    private lateinit var recyclerView                 : RecyclerView

    private val locationListViewModel: LocationListViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as SolarAlarmApp).locationRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        fragmentListlocationsBinding = FragmentListlocationsBinding.inflate(layoutInflater, container, false)
        locationListAdapter          = LocationListAdapter(emptyList())
        recyclerView                 = fragmentListlocationsBinding.fragmentListlocationsRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(locationListAdapter)

        try
        {
            locationListViewModel.allLocations.observe(viewLifecycleOwner, androidx.lifecycle.Observer { locations -> locationListAdapter.UpdateLocations(locations)})
        }
        catch (e: Exception)
        {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }

        return fragmentListlocationsBinding.getRoot()
    }
}
