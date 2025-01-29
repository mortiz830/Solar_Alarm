package com.example.solar_alarm.Location

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.R

class LocationListFragment : Fragment(){

    private val locationViewModel : LocationViewModel by activityViewModels()

    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_listlocations, container, false)
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        locationRecyclerView = view.findViewById(R.id.fragment_listlocations_recylerView)

        locationAdapter = LocationAdapter(emptyList())
        locationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        locationRecyclerView.adapter = locationAdapter

        locationViewModel.All.observe(viewLifecycleOwner, Observer { locationList -> locationAdapter.updateLocations(locationList) })
    }
}
