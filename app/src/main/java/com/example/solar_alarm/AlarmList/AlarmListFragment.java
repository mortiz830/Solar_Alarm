package com.example.solar_alarm.AlarmList;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solar_alarm.CreateAlarm.UpdateAlarmFragment;
import com.example.solar_alarm.Data.Alarm;
import com.example.solar_alarm.R;
import com.example.solar_alarm.Service.GpsTracker;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

public class AlarmListFragment extends Fragment implements OnToggleAlarmListener {
    private AlarmRecycleViewAdapter alarmRecyclerViewAdapter;
    private AlarmListViewModel alarmsListViewModel;
    private RecyclerView alarmsRecyclerView;
    private Button addAlarm;
    private Button addLocation;
    private GpsTracker gpsTracker;
    TextView timeZone;
    TextView latitude;
    TextView longitude;
    ZoneId zoneId;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listalarms, container, false);

        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
        this.configureOnClickRecyclerView();

        zoneId = TimeZone.getDefault().toZoneId();
        timeZone = view.findViewById(R.id.fragment_listalarms_timezone);
        latitude = view.findViewById(R.id.fragment_listalarms_latitude);
        longitude = view.findViewById(R.id.fragment_listalarms_longitude);

        timeZone.setText(zoneId.toString());
        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment);
            }
        });
        addLocation = view.findViewById(R.id.fragment_listAlarms_addLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_alarmsListFragment_to_addLocationFragment);
            }
        });

        getLocation(view);

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

    public void getLocation(View view){
        gpsTracker = new GpsTracker(view.getContext());
        if(gpsTracker.canGetLocation()){
            double lat = gpsTracker.getLatitude();
            double lon = gpsTracker.getLongitude();
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lon));
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(alarmsRecyclerView, R.layout.item_alarm)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Alarm alarm = alarmRecyclerViewAdapter.getAlarm(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        UpdateAlarmFragment updateAlarmFragment = new UpdateAlarmFragment();
                        updateAlarmFragment.setArguments(bundle);
                        FragmentManager manager = getFragmentManager();
                        manager.beginTransaction().replace(R.id.activity_main_nav_host_fragment, updateAlarmFragment).commit();

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