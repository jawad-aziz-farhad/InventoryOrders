package com.imFarhad.inventoryorders.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.SessionManager;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Splash extends AppCompatActivity {

    private static final String TAG = Splash.class.getSimpleName();
    private SessionManager sessionManager;
    private String requestedURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras() != null){
            requestedURL = getIntent().getExtras().getString("url");
            Log.w(TAG, "Notification URL: "+ requestedURL);
            ShortcutBadger.applyCount(Splash.this, 0);
        }
        sessionManager = new SessionManager(this.getBaseContext());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkingLogin();
            }
        }, 3000);
    }

    /*CHECKING LOGIN STATUS ,IF LOGGED IN, OPENING WEBSITE OTHERWISE MOVING TO THE LOGIN SCREEN*/
    private void checkingLogin() {
        Log.w(TAG, "Login Status "+ sessionManager.isLoggedIn());
        if(sessionManager.isLoggedIn()) {
            checkInternetAvailability();
        }
        else {
            startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }
    }

    //TODO: CHECKING INTERNET AVAILABILITY
    private void checkInternetAvailability(){
        if(Connectivity.isConnected(this) && (Connectivity.isConnectedMobile(this) || Connectivity.isConnectedWifi(this)))
        {
            startActivity(new Intent(Splash.this, SliderMenu.class));
            finish();
        }
        else {
            Toast.makeText(this, getString(R.string.internet_error_msg), Toast.LENGTH_LONG).show();
            startActivity(new Intent(Splash.this, NetworkError.class));
            finish();
        }
    }


}
