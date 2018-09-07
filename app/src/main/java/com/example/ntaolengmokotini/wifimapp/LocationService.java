package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;




/*
This class is a background service for the application. It retrieves location
information from the device and calls API Post methods to send it to the API if
certain requirements are met.
*/
public class LocationService extends Service implements android.location.LocationListener {

    protected LocationManager locationManager;
    protected APIServices apiServices;
    protected WifiServices wifiServices;
    private boolean inLocationBound = false;
    private static final double UPPER_CAMPUS_LAT_1 = -33.960900;
    private static final double UPPER_CAMPUS_LNG_1 = 18.460952;
    private static final double UPPER_CAMPUS_LAT_2 = -33.954600;
    private static final double UPPER_CAMPUS_LNG_2 = 18.462969;


    /*
    This method is called once the service is started. It contains code
    to initialise the required objects and start requesting location updates.
    */
    @Override
    public void onCreate(){

        apiServices = new APIServices(getApplicationContext());
        wifiServices = new WifiServices(getApplicationContext());

        //The code below outlines the criteria for the location update requests
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        //Creates a reference to the device's location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        /*
        Request location updates from the location services.
        Location updates are given to the service every 500 milliseconds
        and only once the user has moved a distance of 1 meter. This
        ensures that the same location from the same user is not POSTed
        to the API more than once.
        */
        locationManager.requestLocationUpdates(500, 1, criteria, this, null);

    }

    /*
    This method is used to determine whether the user is within a specific
    location area. For example, whether the location is within UCT's Upper Campus.
    The app uses this method to do exactly that; ensure that a user does not
    send a location if their app is opened in a location which is not on Upper Campus.
    The method is generic and thus allows for re-use if the application is used for all UCT
    campuses/expanded to other universities.
    */
    private boolean locBoundCheck(Location loc, Double lat1, Double lng1,Double lat2,Double lng2){

        inLocationBound = false;

        Double wlat1;
        Double wlat2;
        Double wlng1;
        Double wlng2;
        //Creating a generic rectangle which is easier to use
        if(lat1 < lat2){
            wlat1 = lat1;
            wlat2 = lat2;
        }
        else{
            wlat1 = lat2;
            wlat2 = lat1;
        }

        if(lng1 < lng2){
            wlng1 = lng1;
            wlng2 = lng2;
        }
        else{
            wlng1 = lng2;
            wlng2 = lng1;
        }
        //check whether the location is within the bounds and set boolean
        if(Math.abs(loc.getLatitude()) <= wlat1 && wlat2 <= Math.abs(loc.getLatitude()) && Math.abs(loc.getLongitude()) <= wlng1 && Math.abs(loc.getLongitude()) <= wlng2){
            inLocationBound = true;
        }

        return inLocationBound;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    This overridden method is called by the Location Manager due to the requestLocationUpdates
    method being called - when a user's location changes (within the requirements provided).
    This method then checks certain additional requirements:
        1 - That the user is connected to the eduroam network by using the Wifi Services object
        2 - That the location parsed to the method is not null, which could potentially occur
        3 - That the location parsed to the method is within the required bounds
    If these requirements are met then the API Services object is used to Post certain information
    to the API. Parsed objects are: the application context, the locations latitude and longitude
    and the wifi signal level out of 5, supplied by the Wifi Services object.
    @Parameter Location location: the location of the device.
    */
    @Override
    public void onLocationChanged(Location location) {

        //Log.d("Location changed","true");

        if(wifiServices.checkWifiValidity()) {

            //Log.d("Wifi connected", "true");

            if(location != null){

                //Log.d("Location is not null", "true");

                //if(locBoundCheck(location, UPPER_CAMPUS_LAT_1, UPPER_CAMPUS_LNG_1, UPPER_CAMPUS_LAT_2, UPPER_CAMPUS_LNG_2)){

                    apiServices.post(location.getLatitude(), location.getLongitude(), wifiServices.getWifiSignalLevel(5));

               //}
            }
        }
    }

    /*
    Below are methods that are essentially unused in this application however they require
    implementation due to the implemented interfaces.
    */

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        return;
    }

    /*
    The functionality supplied by these methods is not necessary since a
    permission check and request has already been made. If the user revokes
    their permissions the App will no longer function - and the service will
    be destroyed until the permissions are granted again.
    */
    @Override
    public void onProviderEnabled(String provider) {
        return;
    }

    @Override
    public void onProviderDisabled(String s) {
        this.onDestroy();
    }
}
