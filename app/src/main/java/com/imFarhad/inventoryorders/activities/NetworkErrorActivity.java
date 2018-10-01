package com.imFarhad.inventoryorders.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkErrorActivity extends AppCompatActivity {

    private static final String TAG = NetworkError.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_network_error);

        Button retry = (Button)findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetAvailability();
            }
        });

    }

    //TODO: CHECKING INTERNET AVAILABILITY
    private void checkInternetAvailability(){
        if(Connectivity.isConnected(this) && (Connectivity.isConnectedMobile(this) || Connectivity.isConnectedWifi(this))) {
            if(new SessionManager(this).isLoggedIn()){
                startActivity(new Intent(NetworkErrorActivity.this, SliderMenu.class));
                finish();
            }
            else {
                startActivity(new Intent(NetworkErrorActivity.this, LoginActivity.class));
                finish();
            }
        }
        else {
            Toast.makeText(this, getString(R.string.internet_error_msg), Toast.LENGTH_LONG).show();
        }
    }

}
