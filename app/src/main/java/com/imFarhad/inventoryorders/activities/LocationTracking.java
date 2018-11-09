package com.imFarhad.inventoryorders.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.LinkedHashMap;

public class LocationTracking extends AppCompatActivity {

    private PubNub pubNub;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = LocationTracking.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        checkPermissions();
    }

    //TODO: CHECKING PERMISSION
    private void checkPermissions(){
        // IF PERMISSIONS REQUIRED
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
           (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
        else
            initPubNub();
    }


    //TODO: INITIALIZING PUB NUB
    private void initPubNub(){
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(AppConfig.PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(AppConfig.PUBNUB_PUBLISHKEY);
        pnConfiguration.setSecure(true);
        pubNub = new PubNub(pnConfiguration);
        trackLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "Permissions Result : " + grantResults[0] + " " + grantResults[1]);
                    initPubNub();
                } else
                    Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();

            }
        }
    }

    //TODO: TRACKING LOCATION OF SALES-MAN
    @SuppressLint("MissingPermission")
    private void trackLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            mFusedLocationClient.requestLocationUpdates(getLocationRequestParams(), new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());
                    pubNub.publish()
                            .message(message)
                            .channel(AppConfig.PUBNUB_CHANNEL_NAME)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // handle publish result, status always present, result if successful
                                    // status.isError() to see if error happened
                                    if (!status.isError()) {
                                        System.out.println("pub timetoken: " + result.getTimetoken());
                                    }
                                    System.out.println("pub status code: " + status.getStatusCode());
                                }
                            });
                }
            }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private LocationRequest getLocationRequestParams(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(10);
        return locationRequest;
    }

    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
    }
}
