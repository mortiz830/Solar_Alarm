package com.example.solar_alarm;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
 * */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder>
 {
    private List<Alarm> Alarms;
    Context context;

    public AlarmAdapter(List<Alarm> alarms)
    {
        Alarms = alarms;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.alarm_list_item, parent, false);

        // Return a new holder instance
        AlarmViewHolder viewHolder = new AlarmViewHolder(contactView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder holder, final int position)
    {
        // Get the data model based on position
        final Alarm alarm = Alarms.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.NameTextView;
        textView.setText(alarm.GetAlarmName());

        textView = holder.TimeTextView;
        textView.setText(alarm.GetAlarmTime().toString());
        holder.parent_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onLongClick(View v) {
                removeItem(alarm);
                Toast.makeText(context, "Alarm " + alarm.GetAlarmName() + " deleted", Toast.LENGTH_LONG).show();
                if(context instanceof AlarmListActivity)
                {
                    ((AlarmListActivity)context).SaveToFile();
                }
                return true;
            }
        });

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, UpdateAlarmActivity.class);
                intent.putExtra("AlarmName",Alarms.get(position).GetAlarmName());
                //intent.putExtra("AlarmTime",Alarms.get(position));
                context.startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Alarms.size();
    }

    public void removeItem(Alarm alarm)
    {
        int currPosition = Alarms.indexOf(alarm);
        Alarms.remove(alarm);
        notifyItemRemoved(currPosition);
    }

    public void updateItem(Alarm alarm)
    {
        int currPosition = Alarms.indexOf(alarm);
        notifyItemChanged(currPosition);

    }
}
