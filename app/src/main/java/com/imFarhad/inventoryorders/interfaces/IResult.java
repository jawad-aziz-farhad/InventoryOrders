package com.imFarhad.inventoryorders.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Farhad on 17/09/2018.
 */

public interface IResult {
    void onSuccess(String requestType, JSONObject result);
    void onError(String requestType, VolleyError error);
}
