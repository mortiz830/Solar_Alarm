package com.example.solar_alarm.AlarmList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solar_alarm.Data.AlarmDisplayData;
import com.example.solar_alarm.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmRecycleViewAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private List<AlarmDisplayData> alarms;
    private OnToggleAlarmListener listener;
    Context context;

    public AlarmRecycleViewAdapter(OnToggleAlarmListener listener) {
        this.alarms = new ArrayList<AlarmDisplayData>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        context = parent.getContext();
        return new AlarmViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmDisplayData alarm = alarms.get(position);
        holder.bind(alarm);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<AlarmDisplayData> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull AlarmViewHolder holder) {
        super.onViewRecycled(holder);
        holder.alarmStarted.setOnCheckedChangeListener(null);
    }
    public AlarmDisplayData removeItem(int position)
    {
        AlarmDisplayData alarm = alarms.get(position);
        alarms.remove(alarm);
        notifyItemRemoved(position);
        return alarm;
    }

    public AlarmDisplayData getAlarm(int position)
    {
        return this.alarms.get(position);
    }
}

