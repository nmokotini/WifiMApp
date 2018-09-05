package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.Collator;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class APIServices {

    private final static String BASE_URL_POST="http://192.168.0.103:8765/api/v2/lwdata";
    private final static String BASE_URL_GET="http://192.168.0.103:8765/api/v2/lwdata";

    int[] colors = {
            Color.rgb(255, 0, 0), // red
            Color.rgb(102, 225, 0), // green

    };

    float[] startPoints = {
            0.2f, 1f
    };

    Gradient gradient = new Gradient(colors, startPoints);

    double oppacity = 0.3;
    int radius = 50;

    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;

    public void getAndUpdateMap(Context context, final GoogleMap mMap, String type, int age){

        RequestQueueInstance requestQueueInstance = new RequestQueueInstance(context);
        String URL = BASE_URL_GET;

        if(type.toUpperCase().equals("DAYS")){
            URL = BASE_URL_GET+"/age/days/"+age;
        }
        else if(type.toUpperCase().equals("HOURS")){
            URL = BASE_URL_GET+"/age/hours/"+age;
        }
        else if(type.toUpperCase().equals("ALLTIME")){
            URL = BASE_URL_GET+"/age/alltime/"+age;
        }

        Log.d("URL", URL);
        RequestQueue rQueue = requestQueueInstance.getInstance(context).getRequestQueue();
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
                        ArrayList<WeightedLatLng> aggData = new ArrayList<WeightedLatLng>();
                        for(int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject jsonObject = response.getJSONObject(i);
                                Lwdata dataPoint = gson.fromJson(jsonObject.toString(), Lwdata.class);
                                LatLng marker = new LatLng(dataPoint.getLat(),dataPoint.getLng());
                                WeightedLatLng weightedLatLng = new WeightedLatLng(marker, dataPoint.getRssilvl()); //maybe multiply to get higher difference
                                aggData.add(weightedLatLng);

                            }
                            catch(JSONException e) {
                                Log.e("Rest Response", e.toString());
                            }



                        if(mProvider == null && mOverlay == null){
                            mProvider = new HeatmapTileProvider.Builder().weightedData(aggData).gradient(gradient).radius(radius).opacity(oppacity).build();
                            //add tile overlay options here
                            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                        }
                        else {
                            mProvider.setWeightedData(aggData);
                            mOverlay.clearTileCache();
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

    public void post(Context context, double lat, double lng, int rssilvl){

        RequestQueueInstance requestQueueInstance = new RequestQueueInstance(context);

        String URL = BASE_URL_POST;
        JSONObject js = new JSONObject();
        try {
            js.put("lat", lat);
            js.put("lng", lng);
            js.put("rssilvl", rssilvl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue rQueue = requestQueueInstance.getInstance(context).getRequestQueue();
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
        );

        rQueue.add(jsonObjReq);
    }

}
