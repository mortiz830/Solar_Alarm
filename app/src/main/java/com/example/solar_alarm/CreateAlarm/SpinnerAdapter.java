package com.example.solar_alarm.CreateAlarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Location> {
    public SpinnerAdapter(Context context,
                          List<Location> locationList)
    {
        super(context, 0, locationList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_spinner, parent, false);
        }

        TextView locationName = convertView.findViewById(R.id.spinner_location_name);
        TextView latitude = convertView.findViewById(R.id.spinner_latitude);
        TextView longitude = convertView.findViewById(R.id.spinner_longitude);
        Location currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            locationName.setText(currentItem.Name);
            latitude.setText(String.valueOf(currentItem.Latitude));
            longitude.setText(String.valueOf(currentItem.Longitude));
        }
        return convertView;
    }
}
