package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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

/*
    This class provides API Services. It is ultimately how the application communicates
    with the API. It provides a GET and a POST method. The class utilises the Volly Libraries
    to manage API calls.
*/
public class APIServices {

    //The base url's for both posting and getting
    private final static String BASE_URL_POST="http://196.47.217.16:8765/api/v2/lwdata";
    private final static String BASE_URL_GET="http://196.47.217.16:8765/api/v2/lwdata";

    private Context context;

    //APIServices constructor, sets the local context variable to the parsed value
    public APIServices(Context context){
        this.context = context;
    }

    //The colours that will be used by the heat map
    int[] colours = {
            Color.rgb(255, 0, 0), // red
            Color.rgb(102, 225, 0), // green

    };

    //Defines the intensity of the colours used by the heat map colours
    float[] startPoints = {
            0.2f, 1f
    };

    //Defines the gradient object used by the heat map
    Gradient gradient = new Gradient(colours, startPoints);

    //Defines the opacity and radius values to be used by the heat map
    double opacity = 0.3;
    int radius = 50;

    //Objects required by the heat map
    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;

    /*
    This method is used to make a HTTP GET call to the API and update the map.
    It uses the Volly Library to facilitate the HTTP GET call. It makes a GET call
    including age information required to filter the data within the API. Filtering
    is made using a type and an age value. For example, type = hours, age = 1; thus
    data will be filtered to only include data that is a maximum of a 1 hour old.
    This includes creating and setting the heat map to the Google Map used in
    the Maps Activity.
    @Parameter GoogleMap mMap: The GoogleMap that the heat map must be applied to
    @Parameter String type: The age type for filtering (e.g. Days, Hours or All Time)
    @Parameter int Age: The age defined as an integer, used in conjunction with type (e.g. 1,2,3..)
    */
    public void getAndUpdateMap(final GoogleMap mMap, String type, int age){

        RequestQueueInstance requestQueueInstance = new RequestQueueInstance(context);

        //setting the URL for the GET call
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

        //Initialising the request queue
        RequestQueue rQueue = requestQueueInstance.getInstance(context).getRequestQueue();
        //constructing the request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    /*
                    The method below outlines the operations made once a response has been received
                    from the API. It converts the returned JSON array into Lwdata object using the GSON library.
                    It then populates Weighted Lat Lng objects and adds them to the list, agg. It also
                    creates the methods that will deal with the response.
                    */
                    @Override
                    public void onResponse(JSONArray response){
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        ArrayList<WeightedLatLng> aggData = new ArrayList<WeightedLatLng>();
                        for(int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject jsonObject = response.getJSONObject(i);
                                Lwdata dataPoint = gson.fromJson(jsonObject.toString(), Lwdata.class);
                                LatLng point = new LatLng(dataPoint.getLat(),dataPoint.getLng());
                                WeightedLatLng weightedLatLng = new WeightedLatLng(point, dataPoint.getRssilvl());
                                aggData.add(weightedLatLng);

                            }
                            catch(JSONException e) {
                                Log.e("Rest Response", e.toString());
                            }

                        /*
                        The code below is responsible for building the heat map. This includes creating a Heat Map Tile Provider
                        and and Tile Overlay. It uses the list agg as the data, and uses the gradient, radius and opacity settings
                        initialised earlier. If the heat map has not been created, it creates it; otherwise it removes the current
                        heat map data and adds the new data contained in agg.
                        */

                        if(mProvider == null && mOverlay == null){
                            mProvider = new HeatmapTileProvider.Builder().weightedData(aggData).gradient(gradient).radius(radius).opacity(opacity).build();
                            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                        }
                        else if(aggData.size()==0) {
                            mOverlay.remove();
                        }
                        else{
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

    /*
    This method is used to make a HTTP POST call to the API in order to add data
    to the database. It uses the Volly Library to facilitate the HTTP POST call.
    The POST call includes a JSON object, populated with the required parsed information.
    The JSON object ultimately includes a locations latitude, longitude and the RSSI Level
    at that location.
    @Parameter double lat: The latitude of the location to be posted.
    @Parameter double lng: The longitude of the location to be posted.
    @Parameter int rssilvl: The RSSI level at the location (lat, lng).
    */
    public void post(double lat, double lng, int rssilvl){

        //Initialising the request queue
        RequestQueueInstance requestQueueInstance = new RequestQueueInstance(context);

        //The code below sets the request URL and creates and populates the JSON object with parsed data
        String URL = BASE_URL_POST;
        JSONObject js = new JSONObject();
        try {
            js.put("lat", lat);
            js.put("lng", lng);
            js.put("rssilvl", rssilvl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        The code below makes a POST call using the outlined URL and
        inserts the created JSON objects into the body. It also creates
        methods to deal with the response or any errors.
        */

        RequestQueue rQueue = requestQueueInstance.getInstance(context).getRequestQueue();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, js,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        //Log.d("POST Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                    }
                }
        );

        rQueue.add(jsonObjReq);
    }

}
