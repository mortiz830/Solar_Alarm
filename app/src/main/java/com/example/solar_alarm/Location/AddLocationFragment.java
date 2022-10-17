package com.example.solar_alarm.Location;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.solar_alarm.Data.AsyncDbAccess;
import com.example.solar_alarm.Data.Repositories.LocationRepository;
import com.example.solar_alarm.Data.Tables.Location;
import com.example.solar_alarm.R;
import com.example.solar_alarm.Service.GpsTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.HttpURLConnection;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddLocationFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.fragment_add_location_addLocationButton)
    Button addLocationButton;
    @BindView(R.id.fragment_add_location_Latitude)
    TextView latitudeText;
    @BindView(R.id.fragment_add_location_Longitude)
    TextView longitudeText;
    @BindView(R.id.fragment_add_location_TimeZone)
    TextView timeZoneText;
    @BindView(R.id.fragment_add_location_LocationNameText)
    EditText locationNameText;
    GoogleMap googleMap;
    private GpsTracker gpsTracker;
    private double latitude;
    private double longitude;
    public int timeZoneID;
    private HttpURLConnection httpUrlConnection;
    Boolean isLocationNameExists;
    boolean isLocationLatitudeExists;
    boolean isLocationLongitudeExists;
    boolean isLocationPointExists;
    String locationName;

    LocationRepository locationRepository;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationRepository = new LocationRepository();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);
        getCurrentLocation(view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_add_location_map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);
        latitudeText.setText(String.valueOf(latitude));
        longitudeText.setText(String.valueOf(longitude));
        timeZoneText.setText(TimeZone.getDefault().toZoneId().toString());

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationName = locationNameText.getText().toString();
                try{
                    isLocationNameExists = new AsyncDbAccess.LocationNameExistsTask().execute(locationName).get();
                    isLocationPointExists = new AsyncDbAccess.LocationPointExistsTask().execute(latitude, longitude).get();
                } catch(Exception e){
                    e.printStackTrace();
                }

                if(!isLocationNameExists  && !isLocationPointExists)
                {
                    saveLocation();
                    Navigation.findNavController(view).navigate(R.id.action_addLocationFragment_to_alarmsListFragment);
                }
                else if(isLocationNameExists && isLocationPointExists)
                {
                    Toast.makeText(getContext(), "Location Name & Point Already Exists!", Toast.LENGTH_LONG).show();
                } else if (isLocationNameExists)
                    Toast.makeText(getContext(), "Location Name Already Exists!", Toast.LENGTH_LONG).show();
                else if(isLocationPointExists)
                    Toast.makeText(getContext(), "Location Point Already Exists!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void getCurrentLocation(View view){
        gpsTracker = new GpsTracker(view.getContext());
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveLocation()
    {
        String locationName = locationNameText.getText().toString();

        Location location = new Location();
        location.Name = locationName;
        location.Latitude = latitude;
        location.Longitude = longitude;

        locationRepository.Insert(location);
        Toast.makeText(getContext(), "New Location Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        LatLng current = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(current).title(latitude + ", " + longitude));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(current));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11.0f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude + ", " + latLng.longitude));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f));
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                latitudeText.setText(String.valueOf(latitude));
                longitudeText.setText(String.valueOf(longitude));
            }
        });
    }
}