package com.service.badaeasy;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.service.badaeasy.Adapters.BannerSliderAdapter;
import com.service.badaeasy.Models.BannerSliderModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment {


    public FoodFragment() {
        // Required empty public constructor
    }

    private ViewPager bannerSliderViewPager;
    private int currentPage;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    private List<BannerSliderModel> arrangedList;
    FusedLocationProviderClient mFusedLocationClient;
    private TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_food, container, false);
        latitudeTextView = view.findViewById(R.id.latTextView);
        longitTextView = view.findViewById(R.id.lonTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // method to get the location
        getLastLocation();
        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation()
    {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient
                        .getLastLocation()
                        .addOnCompleteListener(
                                new OnCompleteListener<Location>() {

                                    @Override
                                    public void onComplete(
                                            @NonNull Task<Location> task)
                                    {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        }
                                        else {
                                            latitudeTextView
                                                    .setText(
                                                            location
                                                                    .getLatitude()
                                                                    + "");
                                            longitTextView
                                                    .setText(
                                                            location
                                                                    .getLongitude()
                                                                    + "");
                                        }
                                    }
                                });
            }

            else {
                Toast
                        .makeText(
                                getContext(),
                                "Please turn on"
                                        + " your location...",
                                Toast.LENGTH_LONG)
                        .show();

                Intent intent
                        = new Intent(
                        Settings
                                .ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData()
    {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest
                = new LocationRequest();
        mLocationRequest.setPriority(
                LocationRequest
                        .PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(getContext());

        mFusedLocationClient
                .requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper());
    }

    private LocationCallback
            mLocationCallback
            = new LocationCallback() {

        @Override
        public void onLocationResult(
                LocationResult locationResult)
        {
            Location mLastLocation
                    = locationResult
                    .getLastLocation();
            latitudeTextView
                    .setText(
                            "Latitude: "
                                    + mLastLocation
                                    .getLatitude()
                                    + "");
            longitTextView
                    .setText(
                            "Longitude: "
                                    + mLastLocation
                                    .getLongitude()
                                    + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions()
    {
        return ActivityCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED

                && ActivityCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission
                                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }

    // method to requestfor permissions
    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[] {
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION },
                PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(
                        LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager
                    .PERMISSION_GRANTED) {

                getLastLocation();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}
