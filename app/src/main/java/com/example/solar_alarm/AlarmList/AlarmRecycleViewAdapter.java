package com.example.solar_alarm.AlarmList;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solar_alarm.Data.Tables.SolarAlarm;
import com.example.solar_alarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmRecycleViewAdapter extends RecyclerView.Adapter<AlarmViewHolder>
{
    private List<SolarAlarm> alarms;
    private OnToggleAlarmListener listener;
    Context context;

    public AlarmRecycleViewAdapter(OnToggleAlarmListener listener) {
        this.alarms = new ArrayList<SolarAlarm>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        context = parent.getContext();
        return new AlarmViewHolder(itemView, listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position)
    {
        SolarAlarm alarm = alarms.get(position);
        try
        {
            holder.bind(alarm);
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<SolarAlarm> alarms)
    {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull AlarmViewHolder holder) {
        super.onViewRecycled(holder);
        holder.alarmStarted.setOnCheckedChangeListener(null);
    }
    public SolarAlarm removeItem(int position)
    {
        SolarAlarm alarm = alarms.get(position);
        alarms.remove(alarm);
        notifyItemRemoved(position);
        return alarm;
    }

    public SolarAlarm getAlarm(int position)
    {
        return this.alarms.get(position);
    }
}

