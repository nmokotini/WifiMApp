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
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected boolean gps_enabled, network_enabled;
    protected Handler handler = new Handler();


    public LocationService(Context mContext){
        context = mContext;
    }

    @Override
    public void onCreate(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

        if(wifiServices.getWifiSignalLevel(context, 5) != 0) {

            apiServices.post(context, location.getLatitude(), location.getLongitude(), wifiServices.getWifiSignalLevel(context, 5));

        }

    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        // TODO: add notification to tell user that GPS Services are disabled.
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}