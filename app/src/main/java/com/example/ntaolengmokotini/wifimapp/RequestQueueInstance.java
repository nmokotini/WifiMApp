package com.example.ntaolengmokotini.wifimapp;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueInstance{

    private static RequestQueueInstance queueInstance;
    private Context context;
    private RequestQueue requestQueue;

    private RequestQueueInstance(Context c) {
        context = c;
        requestQueue = getRequestQueue();
    }

    public static RequestQueueInstance getInstance(Context c) {
        if (queueInstance == null) {
            queueInstance = new RequestQueueInstance(c);
        }
        return queueInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

}
