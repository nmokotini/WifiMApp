package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService extends Service implements android.location.LocationListener {

    protected LocationManager locationManager;
    protected APIServices apiServices = new APIServices();
    protected WifiServices wifiServices = new WifiServices();
    protected android.location.LocationListener locationListener;
    protected Handler handler = new Handler();
    private boolean inLocationBound = false;
    private static final double UPPER_CAMPUS_LAT_1 = -33.960347;
    private static final double UPPER_CAMPUS_LNG_1 = 18.458184;
    private static final double UPPER_CAMPUS_LAT_2 = -33.954616;
    private static final double UPPER_CAMPUS_LNG_2 = 18.461242;

        @Override
    public void onCreate(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("Service", "started");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 2, this);

    }

    private boolean locBoundCheck(Location loc, Double lat1, Double lng1,Double lat2,Double lng2){

        inLocationBound = false;

        Double wlat1;
        Double wlat2;
        Double wlng1;
        Double wlng2;

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


 // CHECK THIS FOR BUGSS


    @Override
    public void onLocationChanged(Location location) {

        Log.d("Location changed","true");

        if(wifiServices.getWifiSignalLevel(getApplicationContext(), 5) != 0) {

            Log.d("Wifi connected", "true");

            if(location != null){

                if(locBoundCheck(location, UPPER_CAMPUS_LAT_1, UPPER_CAMPUS_LNG_1, UPPER_CAMPUS_LAT_2, UPPER_CAMPUS_LNG_2)){

                    apiServices.post(getApplicationContext(), location.getLatitude(), location.getLongitude(), wifiServices.getWifiSignalLevel(getApplicationContext(), 5));

                }
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        // TODO: add notification to tell user that GPS Services are disabled.
    }
}
