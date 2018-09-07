package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
This class allows the creation and utilisation of Request Queue objects.
Request Queues are utilised by Volley to track and manage requests made to
an API.

*/
public class RequestQueueInstance{

    private static RequestQueueInstance queueInstance;
    private Context context;
    private RequestQueue requestQueue;

    public RequestQueueInstance(Context c) {
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
