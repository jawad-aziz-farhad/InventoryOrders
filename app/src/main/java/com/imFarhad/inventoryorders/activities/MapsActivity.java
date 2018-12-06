package com.imFarhad.inventoryorders.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.imFarhad.inventoryorders.interfaces.LatLngInterpolator;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PubNub pubNub;
    private Marker marker;
    private  SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = LocationTracking.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_show_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        initLocationAPIs();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
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

    //TODO INITIALIZING LOCATION APIs
    private void initLocationAPIs(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

    }

    //TODO: PUBLISH UPDATED LOATION
    private void publishNewLocation(){
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());
                    updateUI(message);

                    pubNub.publish()
                          .message(message)
                          .channel(AppConfig.PUBNUB_CHANNEL_NAME)
                          .async(new PNCallback<PNPublishResult>() {
                              @Override
                              public void onResponse(PNPublishResult result, PNStatus status) {
                                  if(!status.isError())
                                      Log.w(TAG, "PUB NUB TIME TOKEN "+ result.getTimetoken());
                                  else
                                      Log.e(TAG, "PUB NUB ERROR" + status.getStatusCode());
                              }
                          });
                }
            }, Looper.myLooper());
        }
        catch (SecurityException e){e.printStackTrace();}
    }

    //TODO: PUTTING LATITUDE AND LONGITUDE IN HASH MAP OBJECT TO PUBLISH FOR PUBNUB CHANNEL
    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng){
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
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
            marker = mMap.addMarker(new MarkerOptions().position(newLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
        }


    }

    //TODO: ANIMATING ICON ACCORING TO THE LOCATION
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

        publishNewLocation();
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

}
