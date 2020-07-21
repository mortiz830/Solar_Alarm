package com.example.solar_alarm;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
 * */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder>
{
    private List<Alarm> Alarms;

    public AlarmAdapter(List<Alarm> alarms)
    {
        Alarms = alarms;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.alarm_list_item, parent, false);

        // Return a new holder instance
        AlarmViewHolder viewHolder = new AlarmViewHolder(contactView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position)
    {
        // Get the data model based on position
        Alarm alarm = Alarms.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.NameTextView;
        textView.setText(alarm.GetAlarmName());

        textView = holder.TimeTextView;
        textView.setText(alarm.GetAlarmTime().toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Alarms.size();
    }
}
