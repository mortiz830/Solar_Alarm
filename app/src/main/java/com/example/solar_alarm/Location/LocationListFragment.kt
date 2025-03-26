package com.example.solar_alarm.Location

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solar_alarm.Data.ViewModels.LocationViewModel
import com.example.solar_alarm.R

@RequiresApi(Build.VERSION_CODES.O)
class LocationListFragment : Fragment()
{
    //private val locationViewModel : LocationViewModel by activityViewModels()
    private val locationViewModel : LocationViewModel by viewModels()

    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_listlocations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        locationRecyclerView = view.findViewById(R.id.fragment_listlocations_recylerView)

        locationAdapter = LocationAdapter(emptyList())
        locationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        locationRecyclerView.adapter = locationAdapter

        try {
            locationViewModel.All.observe(viewLifecycleOwner, Observer { locationList -> locationAdapter.updateLocations(locationList) })
        } catch (e: Exception) {
            Toast.makeText(getContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }
}
