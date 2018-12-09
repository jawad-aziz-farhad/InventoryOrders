package com.imFarhad.inventoryorders.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.ProxyHurlStack;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces._IResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 17/09/2018.
 */

public class VolleyService {

    private IResult mResultCallBack = null;
    private _IResult _mResultCallBack = null;
    private Context context;
    private SessionManager sessionManager;
    private static final String TAG = VolleyService.class.getSimpleName();

    public VolleyService(IResult mResultCallBack, Context context){
        this.mResultCallBack = mResultCallBack;
        this.context = context;
        sessionManager = new SessionManager(this.context);
    }

    public VolleyService(_IResult _mResultCallBack, Context context){
        this._mResultCallBack = _mResultCallBack;
        this.context = context;
        sessionManager = new SessionManager(this.context);
    }


    public void postRequest(String url , final String requestType, JSONObject data){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        Log.w(TAG, "RESPONSE: "+ response.toString());
                        mResultCallBack.onSuccess(requestType, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ERROR  "+ error.toString());
                        mResultCallBack.onError(requestType,error);
                    }
                })

        {

            /** Passing some request headers* */
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", sessionManager.getToken());
                return headers;
            }
        };
        // add it to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //RequestQueue requestQueue = Volley.newRequestQueue(context, new ProxyHurlStack());
        requestQueue.add(jsonObjReq);
    }


    public void getRequest(String url , final String requestType, JSONObject data){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        Log.w(TAG, "RESPONSE: "+ response.toString());
                        mResultCallBack.onSuccess(requestType, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ERROR  "+ error.toString());
                        mResultCallBack.onError(requestType,error);
                    }
                })

        {

            /** Passing some request headers* */
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", sessionManager.getToken());
                return headers;
            }
        };
        // add it to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjReq);
    }

    public void _getRequest(String url, final String requestType){

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Success Callback
                        Log.w(TAG, "RESPONSE: "+ response.toString());
                        _mResultCallBack.onSuccess(requestType, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ERROR  "+ error.toString());
                        _mResultCallBack.onError(requestType,error);
                    }
                })
        {

            /** Passing some request headers* */
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", sessionManager.getToken());
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
