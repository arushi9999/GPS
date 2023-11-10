package com.example.gpslocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.SystemClock;
import android.util.Log;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";
    private static final int REQUEST_PERMISSION_COARSE_LOCATION = 2;
    private static final int REQUEST_CODE = 100;
    private Location currLocation;
    private Location newLocation;
    private float distanceTravelled = 0.0f;
    private long t;

    TextView latitude;
    TextView longitude;
    TextView location;
    TextView distance;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        System.out.println("checking permission:");
        distance=findViewById(R.id.totalDist);
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);
        location=findViewById(R.id.location);
        time=findViewById(R.id.time);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // Permission has already been granted
            // Continue with location-related operations
            MyLocationListener myLocationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, myLocationListener);

        }
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            long timeSinceLastCall = System.currentTimeMillis()-t;
            timeSinceLastCall/=1000;
            time.setText("Time spent @prev location: "+timeSinceLastCall);
            try {
                System.out.println("on location change called:");
                newLocation = loc;

                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                //putAddress and long/lat
                location.setText("Curr: "+addresses.get(0).getAddressLine(0));
                longitude.setText("Longitude: "+loc.getLatitude());
                latitude.setText("Latitude: "+loc.getLongitude());

                if (currLocation != null) {
                    System.out.println("LOCATION UPDATED :");
                    if (addresses.size() > 0) {
                        String addLine = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        distanceTravelled += currLocation.distanceTo(newLocation)/1609.344f;
                        distance.setText("Distance Traveled: "+String.valueOf(distanceTravelled));
                    }
                } else {
                    // first time to set start
                    System.out.println("FIRST:");
                    if (addresses.size() > 0) {
                        String addLine = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    }
                }
                currLocation = newLocation;
                t = System.currentTimeMillis();
            } catch (IOException e) {
                    e.printStackTrace();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


}