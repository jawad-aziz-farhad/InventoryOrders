package com.imFarhad.inventoryorders.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface _IResult {
    void onSuccess(String requestType, JSONArray result);
    void onError(String requestType, VolleyError error);
}
