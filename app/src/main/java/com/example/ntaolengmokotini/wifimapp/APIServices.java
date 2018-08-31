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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIServices {

    private final static String BASE_URL_POST="http://196.24.186.35:8888/api/v2/lwdata";
    private final static String BASE_URL_GET="http://196.24.186.35:8888/api/v2/lwdata";

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
