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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.location.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/*
    This is the main activity of the application. As such
    it is created upon the starting of the application.
    It extends FragmentActivity to allow the use of a Google Map within the application.
    It implements OnMapReadyCallback to cause the calling of an overridden method once the
    map has been successfully loaded and is ready to use. It also implements OnItemSelectedListener
    which allows the onItemSelectedListener method to be overridden when a Data Filter Spinner item
    is selected.
*/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap; //The GoogleMap object which will be set to the Map object once it is created

    private boolean firstCall = true; //A boolean to manage the first call of the

    private APIServices apiServices;  //An object which provides the map activity with the required API Services (such as HTTP GETs and POSTs)

    private static final String[] REQUIRED_PERMS = { //A string array containing the required permissions for the application
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET
    };

    LatLngBounds UPPER_CAMPUS = new LatLngBounds(new LatLng(-33.960900, 18.460952), new LatLng(-33.954600, 18.462969)); //The map bounds for upper campus


    /*
     Method called when starting the activity & this is the method where most initialization is done.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiServices = new APIServices(getApplicationContext());
        super.onCreate(savedInstanceState); //Super method call to allow the application to be resumed (if it has been minimized)

        setContentView(R.layout.activity_maps); //setting the layout (user interface layout XML) for the activity

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this); //getting the map asynchronously and setting this class as the listener


        /*
        The below code initialises the Age Filter Spinner. It sets the values of the spinner
        defined in the strings.xml document. It sets the spinner's listener to this class as well
        as defining its Z positioning, so that it is drawn infront of the map. It also sets
        the default selection to "All Time"
         */
        Spinner mySpinner = (Spinner) findViewById(R.id.times_spinner);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.times_array, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setZ(2);
        mySpinner.setSelection(5);
    }

    //The onDestroy method which is called when the app is closed.
    protected void onDestroy() {
        super.onDestroy();
    }


    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where map settings are initialised. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        The code below initialises some of the map settings. It sets the zoom capabilities as
        well as the bounds the map can have. It also moves the map to the center of campus and
        defines the map type as Hybrid (Topological data overlaid upon a Satellite map).
        The permissions required to run the App are also requested at this point, using the pre-defined
        REQUIRED_PERMS.
        */
        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.setLatLngBoundsForCameraTarget(UPPER_CAMPUS);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UPPER_CAMPUS.getCenter(), 17));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        requestPermissions(REQUIRED_PERMS, 1);
    }

    /*
    This method is called after a requestPermission method has run, and requested
    permissions from the user. It contains information about whether, and which, permissions have been
    granted. This method then makes calls only callable once the permissions have been granted.
    */
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Checks if the permissions required have been granted, if not request them.
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMS, 1);
                return;
            }
            /*
            The code below starts to show the users location, and the relevant UI changes.
            It also calls an API Service method to get data to populate the heatmap, and
            finally starts the Location Service.
            */

            mMap.setMyLocationEnabled(true);
            apiServices.getAndUpdateMap(mMap, "alltime", 1);
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        }
    }

    /*
    This method is used to check whether the user has granted the required permissions.
    It returns a boolean true if they have been granted and false if they have not.
    */
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

    /*
    Method called when a selection is made on the Age Filter Spinner, parsing the position
    of the selected option. The method determines which option has been selected and calls
    the relevant API Get method. The method defaults to the All Time option if the
    selected value cannot be retrieved.
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        String item = parent.getItemAtPosition(pos).toString();
        if(firstCall){
            firstCall = false;
        }
        else{
            if(item.equals("1 Hour")){
                apiServices.getAndUpdateMap(mMap, "hours", 1);
            }
            else if(item.equals("12 Hours")){
                apiServices.getAndUpdateMap(mMap, "hours", 12);
            }
            else if(item.equals("1 Day")){
                apiServices.getAndUpdateMap(mMap, "days", 1);
            }
            else if(item.equals("3 Days")){
                apiServices.getAndUpdateMap(mMap, "days", 3);
            }
            else if(item.equals("1 Week")){
                apiServices.getAndUpdateMap(mMap, "days", 7);
            }
            else if(item.equals("All Time")) {
                apiServices.getAndUpdateMap(mMap, "alltime", 1);
            }
            else{
                apiServices.getAndUpdateMap(mMap, "alltime", 1);
            }
        }

    }

    /*
    This method (unused for our application) is called when nothing is
    selected on the Age Filter Spinner - which cannot occur in our case.
    However, it is a method that must be implemented regardless.
    */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        return;
    }
}
