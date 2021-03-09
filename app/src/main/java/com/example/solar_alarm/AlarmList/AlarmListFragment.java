package com.example.solar_alarm.AlarmList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.R;

import java.util.List;

public class AlarmListFragment extends Fragment implements OnToggleAlarmListener {
    private AlarmRecycleViewAdapter alarmRecyclerViewAdapter;
    private AlarmListViewModel alarmsListViewModel;
    private RecyclerView alarmsRecyclerView;
    private Button addAlarm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmRecyclerViewAdapter = new AlarmRecycleViewAdapter(this);
        alarmsListViewModel = ViewModelProviders.of(this).get(AlarmListViewModel.class);
        alarmsListViewModel.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if (alarms != null) {
                    alarmRecyclerViewAdapter.setAlarms(alarms);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listalarms, container, false);

        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
        this.configureOnClickRecyclerView();

        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment);
            }
        });

        return view;
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(getContext());
            alarmsListViewModel.update(alarm);
        } else {
            alarm.schedule(getContext());
            alarmsListViewModel.update(alarm);
        }
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Alarm alarm = alarmRecyclerViewAdapter.getAlarm(position);
                        // 2 - Show result in a Toast
                        
                        Toast.makeText(getContext(), "You clicked on user : "+alarm.getTitle(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_updateAlarmFragment);
                    }
                });
        ItemClickSupport.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        Alarm alarm = alarmRecyclerViewAdapter.getAlarm(position);
                        // 2 - Show result in a Toast
                        Toast.makeText(getContext(), "You long clicked on user : "+alarm.getTitle(), Toast.LENGTH_SHORT).show();
                        alarmsListViewModel.delete(alarmRecyclerViewAdapter.removeItem(position));
                        return false;
                    }
                });
    }

}