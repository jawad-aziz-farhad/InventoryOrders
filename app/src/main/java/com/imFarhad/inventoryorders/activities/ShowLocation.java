package com.imFarhad.inventoryorders.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.JsonUtil;
import com.imFarhad.inventoryorders.interfaces.LatLngInterpolator;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShowLocation extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment supportMapFragment;
    private GoogleMap google_Map;
    private Marker marker;
    private PubNub pubNub;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = LocationTracking.class.getSimpleName();
    private String CHANNEL_NAME = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_show_location);

        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        if(getIntent().getExtras() != null)
            CHANNEL_NAME = getIntent().getExtras().getString("OrderId");

        Log.w(TAG, "Order ID "+ CHANNEL_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissions();
        else
            initPubNub();
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

    //TODO: LISTENING TO UPDATED LOCATION BY SUBSCRIBING CHANNEL
    private void subscribeLocationChannel() {
        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, String> newLocatin = JsonUtil.fromJson(message.getMessage().toString(), LinkedHashMap.class);
                            updateUI(newLocatin);
                        }
                        catch (Exception e){ e.printStackTrace(); }
                    }
                });
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubNub.subscribe()
              .channels(Arrays.asList(CHANNEL_NAME != null ?  (AppConfig.PUBNUB_CHANNEL_NAME + CHANNEL_NAME ) : AppConfig.PUBNUB_CHANNEL_NAME))
              .execute();
    }


    //TODO: UPDATING UI
    private void updateUI(Map<String,String> newLoc){

        LatLng newLocation = new LatLng(Double.valueOf(newLoc.get("lat")), Double.valueOf(newLoc.get("lng")));
        if(marker != null){
            animateIcon(newLocation);
            boolean contains = google_Map.getProjection().getVisibleRegion().latLngBounds.contains(newLocation);
            if(!contains)
                google_Map.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
        else {
            google_Map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.5f));
            marker = google_Map.addMarker(new MarkerOptions().position(newLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
        }

    }

    //TODO: ANIMATING ICON ACCORING TO THE LOCATION
    private void animateIcon(final LatLng location){
        final LatLng startPosition = marker.getPosition();
        final LatLng endPosition   = new LatLng(location.latitude, location.longitude);

        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0 , 1);
        valueAnimator.setDuration(5000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = valueAnimator.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                marker.setPosition(newPosition);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        valueAnimator.start();
    }



    @Override
    protected void onResume() {
        super.onResume();
        supportMapFragment.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        supportMapFragment.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        supportMapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        supportMapFragment.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            google_Map = googleMap;
            google_Map.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        subscribeLocationChannel();
    }
}
