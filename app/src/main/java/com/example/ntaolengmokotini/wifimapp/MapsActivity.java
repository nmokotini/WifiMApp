package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Request;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    /**
     Method called when starting the activity & this is the method where most initialization is done.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }
    /**
     Requests & obtains values through api
     */
    public ArrayList GET(){
        String URL = "http://196.24.186.131:8080/";
        final ArrayList data = new ArrayList();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //constructing the request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try{
                            data.add(response.getString("lat"));
                            data.add(response.getString("lng"));
                            data.add(response.getString("rssilvl"));
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        requestQueue.add(jsonObjectRequest);

        return data;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     Calculates SignalLevel of Wifi connection using RSSI values.
     */
    public String getWifiSignalLevel(){
        //gets access to WIFI service through permissions
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //gets information about the wifi connection and stores it in a WifiInfo object
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //sets RSSI levels, out of which the SignalLevel will be calculated
        final int rssiLevels = 5;
        //calulates Signal level
        int wifiSignalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), rssiLevels);
        String strWifiSignalLevel = "Wifi Signal Level: "+wifiSignalLevel+"/"+rssiLevels;
        return strWifiSignalLevel;
    }




}
