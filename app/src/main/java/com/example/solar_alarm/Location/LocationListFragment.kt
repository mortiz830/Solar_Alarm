package com.example.solar_alarm.Location

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.ViewModels.LocationListViewModel
import com.example.solar_alarm.databinding.FragmentListlocationsBinding

@RequiresApi(Build.VERSION_CODES.O)
class LocationListFragment constructor(private var locationListViewModel: LocationListViewModel) : Fragment()
{
    private lateinit var fragmentListlocationsBinding : FragmentListlocationsBinding
    private lateinit var locationListAdapter          : LocationListAdapter
    private lateinit var recyclerView                 : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        fragmentListlocationsBinding = FragmentListlocationsBinding.inflate(layoutInflater, container, false)
        locationListAdapter          = LocationListAdapter(emptyList())
        recyclerView                 = fragmentListlocationsBinding.fragmentListlocationsRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(locationListAdapter)

        try
        {
            locationListViewModel.AllLocations.observe(viewLifecycleOwner, androidx.lifecycle.Observer { solarAlarms -> locationListAdapter.UpdateLocations(solarAlarms)})
        }
        catch (e: Exception)
        {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }

        return fragmentListlocationsBinding.getRoot()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
//    {
//        super.onViewCreated(view, savedInstanceState)
//
//        recyclerView = view.findViewById(R.id.fragment_listlocations_recylerView)
//
//        locationListAdapter = LocationListAdapter(emptyList())
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = locationListAdapter
//
//        try
//        {
//            locationViewModel.AllLocations.observe(viewLifecycleOwner, Observer { locations -> locationListAdapter.UpdateLocations(locations) })
//        }
//        catch (e: Exception)
//        {
//            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
//        }
//    }
}
