package com.imFarhad.inventoryorders.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PubNub pubNub;
    private Marker marker;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = LocationTracking.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        subscribeLocationChannel();
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
    private void subscribeLocationChannel(){
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
                .channels(Arrays.asList(AppConfig.PUBNUB_CHANNEL_NAME))
                .execute();
    }


    //TODO: UPDATING UI
    private void updateUI(Map<String,String> newLoc){

        LatLng newLocation = new LatLng(Double.valueOf(newLoc.get("lat")), Double.valueOf(newLoc.get("lng")));
        if(marker != null){
            animateIcon(newLocation);
            boolean contains = mMap.getProjection().getVisibleRegion().latLngBounds.contains(newLocation);
            if(!contains)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
        else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.5f));
            marker = mMap.addMarker(new MarkerOptions().position(newLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp)));
        }

    }

    private void animateIcon(final LatLng location) {
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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        try {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
