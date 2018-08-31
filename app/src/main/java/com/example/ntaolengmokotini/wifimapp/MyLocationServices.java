package com.example.ntaolengmokotini.wifimapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MyLocationServices implements Runnable{


    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    Handler handler = new Handler();

    Context context;
    LocationM


    public static Location getCurrentLocation(Context context, LocationManager locationManager) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location != null) {
            return location;
        } else {
            return null;
        }

    }


    @Override
    public void run() {
        try {

            Location myLocation = MyLocationServices.getCurrentLocation(getApplicationContext(), locationManager); //maybe this wont work with context issues

            handler.postDelayed(this, 1000);//re run the method every 1000 seconds
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
