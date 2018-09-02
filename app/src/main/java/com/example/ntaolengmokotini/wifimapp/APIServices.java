package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
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
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class APIServices {

    private final static String BASE_URL_POST="http://192.168.0.103:8765/api/v2/lwdata";
    private final static String BASE_URL_GET="http://192.168.0.103:8765/api/v2/lwdata";

    HeatmapTileProvider mProvider;

    public void getAndUpdateMap(Context context, final GoogleMap mMap){

        RequestQueueInstance requestQueueInstance = new RequestQueueInstance(context);

        String URL = BASE_URL_GET;
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

                        mProvider = new HeatmapTileProvider.Builder().weightedData(aggData).build();
                            //add tile overlay options here

                        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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
