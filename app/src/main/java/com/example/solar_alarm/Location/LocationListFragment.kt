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
import com.example.solar_alarm.data.viewmodels.LocationListViewModel
import com.example.solar_alarm.databinding.FragmentListlocationsBinding
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class LocationListFragment : Fragment()
{
    private var _binding: FragmentListlocationsBinding? = null
    private val binding get() = _binding!!

    private val locationListViewModel: LocationListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentListlocationsBinding.inflate(inflater, container, false)
        val locationListAdapter = LocationListAdapter(emptyList())
        val recyclerView = binding.fragmentListlocationsRecylerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(locationListAdapter)

        try
        {
            locationListViewModel.allLocations.observe(viewLifecycleOwner) { locations -> 
                locationListAdapter.UpdateLocations(locations)
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
