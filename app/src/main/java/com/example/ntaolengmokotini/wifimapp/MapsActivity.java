package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;

    private boolean firstCall = true;

    private APIServices apiServices = new APIServices();
    private WifiServices wifiServices = new WifiServices();
    protected LocationManager locationManager;

    RequestQueueInstance requestQueueInstance;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;
    private Location lastLocation;
    private static final String[] REQUIRED_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET
    };


    /**
     Method called when starting the activity & this is the method where most initialization is done.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueueInstance = new RequestQueueInstance(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        Spinner mySpinner = (Spinner) findViewById(R.id.times_spinner);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.times_array, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setZ(2);


        /*
        createLocationRequest();
        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(settingsRequest);
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    // Location settings are not satisfied, but this can
                    // be fixed by showing the user a dialog
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(), and check the
                        // result in onActivityResult()
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error
                    }
                }
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    post.setLat(location.getLatitude());
                    post.setLng(location.getLongitude());

                }
            }
        };*/
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {


        //start location services
        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLngBounds UPPER_CAMPUS = new LatLngBounds(new LatLng(-33.960347, 18.458184), new LatLng(-33.954616, 18.461242));
        mMap.setLatLngBoundsForCameraTarget(UPPER_CAMPUS);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UPPER_CAMPUS.getCenter(), 17));

        requestPermissions(REQUIRED_PERMS, 1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMS, 1);
                return;
            }
            mMap.setMyLocationEnabled(true);
            apiServices.getAndUpdateMap(getApplicationContext(), mMap, "alltime", 1);
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        }
    }

    public boolean checkPermissions(){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){

            return true;
        }
        else{
            return false;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        String item = parent.getItemAtPosition(pos).toString();
        if(firstCall){
            firstCall = false;
        }
        else{
            if(item.equals("1 Hour")){
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "hours", 1);
            }
            else if(item.equals("12 Hours")){
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "hours", 12);
            }
            else if(item.equals("1 Day")){
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "days", 1);
            }
            else if(item.equals("3 Days")){
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "days", 3);
            }
            else if(item.equals("1 Week")){
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "days", 7);
            }
            else if(item.equals("All Time")) {
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "alltime", 1);
            }
            else{
                apiServices.getAndUpdateMap(getApplicationContext(), mMap, "alltime", 1);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
