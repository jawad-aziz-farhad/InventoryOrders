package com.imFarhad.inventoryorders.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.JsonUtil;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.LatLngInterpolator;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PubNub pubNub;
    private Marker marker , marker1;
    private  SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private static final int REQUEST_CODE = 100;
    private static final String TAG = LocationTracking.class.getSimpleName();
    private String CHANNEL_NAME = null;

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

        if(getIntent().getExtras() != null){
            CHANNEL_NAME = getIntent().getExtras().getString("OrderId");
        }

        Log.w(TAG, "Channel Name "+ CHANNEL_NAME);
        initLocationAPIs();

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

    //TODO INITIALIZING LOCATION APIs
    private void initLocationAPIs(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        //locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
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
                    String Channel_Name = CHANNEL_NAME != null ?  ( AppConfig.PUBNUB_CHANNEL_NAME + CHANNEL_NAME ) : AppConfig.PUBNUB_CHANNEL_NAME;
                    pubNub.publish()
                          .message(message)
                          .channel(Channel_Name)
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
        String Channel_Name = CHANNEL_NAME != null ? AppConfig.PUBNUB_CHANNEL_NAME + CHANNEL_NAME : AppConfig.PUBNUB_CHANNEL_NAME;
        pubNub.subscribe()
                .channels(Arrays.asList(Channel_Name))
                .execute();
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
            LatLng endLatLng = new LatLng(Double.valueOf("33.7681428") , Double.valueOf("72.7732922"));
            marker = mMap.addMarker(new MarkerOptions().position(newLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).title(getAddress(newLocation).get(0).getAddressLine(0)));
            mMap.addMarker(new MarkerOptions().position(endLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(getAddress(endLatLng).get(0).getAddressLine(0)));

            DrawLine(newLocation, endLatLng);
        }
    }

    //TODO: GET ADDRESS
    private List<Address> getAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        }catch (IOException e){ e.printStackTrace(); }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName();
        Log.w(TAG, "Address is : " +address);
        return addresses;
    }

    //TODO: DRAWING POLYLINE
    public  void DrawLine(LatLng location, LatLng endLocation) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(location)
                .color(R.color.green)
                .add(new LatLng(location.latitude, location.longitude))
                .add(new LatLng(endLocation.latitude, endLocation.longitude));

        mMap.addPolyline(polylineOptions);
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
        try {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if(new SessionManager(this).getType().equals("saleman"))
            publishNewLocation();
        else
            subscribeLocationChannel();
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
