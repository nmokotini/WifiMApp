package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        requestQueueInstance = new RequestQueueInstance(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);


        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLngBounds UPPER_CAMPUS = new LatLngBounds(new LatLng(-33.960347, 18.458184), new LatLng(-33.954616, 18.461242));
        mMap.setLatLngBoundsForCameraTarget(UPPER_CAMPUS);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UPPER_CAMPUS.getCenter(), 17));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

        }
        mMap.setMyLocationEnabled(true);

        apiServices.getAndUpdateMap(getApplicationContext(), mMap);
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





}
