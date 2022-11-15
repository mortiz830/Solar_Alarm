package com.example.solar_alarm.CreateAlarm

import com.example.solar_alarm.R
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.content.Context
import android.view.View
import com.example.solar_alarm.Data.Tables.*

class SpinnerAdapter(context: Context?,
                     locationList: List<Location>?) : ArrayAdapter<Location?>(context!!, 0, locationList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView!!, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView!!, parent)
    }

    private fun initView(position: Int, convertView: View,
                         parent: ViewGroup): View {
        // It is used to set our custom view.
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.location_spinner, parent, false)
        }
        val locationName = convertView.findViewById<TextView>(R.id.spinner_location_name)
        val latitude = convertView.findViewById<TextView>(R.id.spinner_latitude)
        val longitude = convertView.findViewById<TextView>(R.id.spinner_longitude)
        val currentItem = getItem(position)

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            locationName.text = currentItem.Name
            latitude.text = currentItem.Latitude.toString()
            longitude.text = currentItem.Longitude.toString()
        }
        return convertView
    }
}