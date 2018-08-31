package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.location.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private APIServices apiServices = new APIServices();
    private WifiServices wifiServices = new WifiServices();
    protected LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Handler handler = new Handler();

    RequestQueueInstance requestQueueInstance;
    Lwdata post;
    private static long UPDATE_TIME_INTERVAL = 1000;
    private static long UPDATE_DISTANCE_INTERVAL = 1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
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
        post = new Lwdata();
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


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME_INTERVAL, UPDATE_DISTANCE_INTERVAL, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        post.setLat(location.getLatitude());
                        post.setLng(location.getLongitude());
                        post.setRssilvl(getWifiSignalLevel());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        System.out.println("Status Change");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                        System.out.println("GPS On");

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        System.out.println("GPS Off");

                    }
                }
        );
    }

    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(startLocationServices);
    }


    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(REQUIRED_PERMS, 1);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location != null) {
            return location;
        } else {
            return null;
        }
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
        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLngBounds UPPER_CAMPUS = new LatLngBounds(new LatLng(-33.960347, 18.458184), new LatLng(-33.954616, 18.461242));
        mMap.setLatLngBoundsForCameraTarget(UPPER_CAMPUS);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UPPER_CAMPUS.getCenter(), 17));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        GET();
        handler.post(startLocationServices);

        Location loc = getCurrentLocation();
        Log.d("location ", loc.toString());
        int wifi = getWifiSignalLevel();
        Log.d("Wifi str ", wifi + "");

        POST(loc.getLatitude(), loc.getLongitude(), getWifiSignalLevel());
        //GET();
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


    /**
     Requests & obtains values through api
     */

    public void GET(){
        String URL = "http://196.24.186.35:8888/api/v2/lwdata";
        RequestQueue rQueue = requestQueueInstance.getInstance(getApplicationContext()).getRequestQueue();
        //constructing the request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        for(int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Lwdata dataPoint = gson.fromJson(jsonObject.toString(), Lwdata.class);
                                LatLng marker = new LatLng(dataPoint.getLat(),dataPoint.getLng());
                                //places marker on the map
                                mMap.addMarker(new MarkerOptions().position(marker).title("Location - WifiLevel:"+dataPoint.getRssilvl()+"/5"));

                            }
                            catch(JSONException e) {
                                Log.e("Rest Response", e.toString());
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Response", error.toString());

                    }
                }

        );
        rQueue.add(jsonArrayRequest);

    }


    public void POST(double lat, double lng, int rssilvl){
        String URL = "http://196.24.186.35:8888/api/v2/lwdata";
        JSONObject js = new JSONObject();
        try {
            js.put("lat", lat);
            js.put("lng", lng);
            js.put("rssilvl", rssilvl);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue rQueue = requestQueueInstance.getInstance(getApplicationContext()).getRequestQueue();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL,js,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("POST Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("lat", String.valueOf(post.getLat()));
                params.put("long",String.valueOf(post.getLng()));
                params.put("rssilvl",String.valueOf(post.getRssilvl()));
                return params;
            }

        };

        rQueue.add(jsonObjReq);
    }

    /**
     Calculates SignalLevel of Wifi connection using RSSI values.
     */
    public int getWifiSignalLevel(){
        //gets access to WIFI service through permissions
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //gets information about the wifi connection and stores it in a WifiInfo object
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //sets RSSI levels, out of which the SignalLevel will be calculated
        final int rssiLevels = 5;
        //calulates Signal level
        int wifiSignalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), rssiLevels);
        return wifiSignalLevel;
    }



    // runnable method to send the data periodically
    private final Runnable startLocationServices = new Runnable(){

        public void run(){
            try {

                Location myLocation = MyLocationServices.getCurrentLocation(getApplicationContext(), locationManager); //maybe this wont work with context issues

                handler.postDelayed(this, 1000);//re run the method every 1000 seconds
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
