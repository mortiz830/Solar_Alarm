package com.example.solar_alarm;
import android.view.LayoutInflater;
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
        AlarmViewHolder alarmViewHolder = null;

        try {
            TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_alarm_list, parent, false);

            alarmViewHolder = new AlarmViewHolder(textView);
        }
        catch (Exception e)
        {
            System.out.print(e);
        }

        return alarmViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position)
    {
        Alarm a = Alarms.get(position);
        holder.TextView.setText(a.GetAlarmName() + " - " + a.GetAlarmTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Alarms.size();
    }
}
