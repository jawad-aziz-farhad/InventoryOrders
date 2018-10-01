package com.imFarhad.inventoryorders.app;

import android.app.Application;
import android.app.ProgressDialog;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.imFarhad.inventoryorders.R;

/**
 * Created by Farhad on 17/09/2018.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue requestQueue;
    private static AppController mInstance;
    private LayerDrawable drawableBadge;
    private ProgressDialog progressDialog;

    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sessionManager = new SessionManager(mInstance);
        progressDialog = new ProgressDialog(mInstance);
    }

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request <T> req){
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if(tag != null)
            requestQueue.cancelAll(tag);
    }

    public void setDrawable(LayerDrawable drawable){ drawableBadge = drawable; }
    public LayerDrawable getDrawable() { return drawableBadge; }

}
