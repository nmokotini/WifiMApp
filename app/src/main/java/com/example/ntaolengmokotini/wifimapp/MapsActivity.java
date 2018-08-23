package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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
    /*
    public void GET(){
        String URL = "http://196.47.227.36:8081/api/v2/lwdata/1";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //constructing the request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            lt = response.getString("lat");
                            lg = response.getString("lng");
                            Log.d("Response", lt);


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

    }
    */

    /*
    public void POST(final double lat, final double lng, final int rssilvl){
        String URL = "http://196.24.186.131:8080/";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lat", String.valueOf(lat));
                params.put("long",String.valueOf(lng ));
                params.put("rssilvl",String.valueOf(rssilvl));
                return params;
            }

        };

        requestQueue.add(postRequest);
    }
    */

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
        // ur for the 4th element in the db
        String URL = "http://196.24.187.121:8080/api/v2/lwdata/67";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //constructing the request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            String lt = response.getString("lat");
                            String lg = response.getString("lng");
                            String rs = response.getString("rssilvl");
                            LatLng marker = new LatLng(Double.valueOf(lt),Double.valueOf(lg));
                            //places marker on the map
                            mMap.addMarker(new MarkerOptions().position(marker).title("Location - WifiLevel:"+rs+"/5"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

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

    }

    /**
     * Posts the Updated location+wifilevel data
     */
    @Override
    protected void onStop(){
        super.onStop();
        //creates new json object
        JSONObject js = new JSONObject();
        try {
            js.put("lat","25");
            js.put("lng","-36");
            js.put("rssilvl","4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //adds json object to the database through response object
        String URL = "http://196.24.187.121:8080/api/v2/lwdata";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL,js,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", response.toString());
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

        };

        requestQueue.add(jsonObjReq);

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
}
