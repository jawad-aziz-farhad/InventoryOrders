package com.imFarhad.inventoryorders.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebViewFragment;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.Preferences;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.fragments.CategoriesFragment;
import com.imFarhad.inventoryorders.fragments.NotificationFragment;
import com.imFarhad.inventoryorders.fragments.ProductsFragment;
import com.imFarhad.inventoryorders.fragments.ProfileFragment;
import com.imFarhad.inventoryorders.fragments.SettingsFragment;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.services.VolleyService;
import org.json.JSONException;
import org.json.JSONObject;

public class SliderMenu extends AppCompatActivity {

    private TextView notificationCount;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private IResult iResult;
    private VolleyService volleyService;
    private static final String TAG = SliderMenu.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_menu);

        Log.w(TAG, "FCM TOKEN : "+ new Preferences(this).getFCMToken());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        sessionManager = new SessionManager(SliderMenu.this);

        Fragment fragment = new CategoriesFragment();
        fragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);

        /* SETTING UP HEADER VALUES */
        TextView userName = (TextView)view.findViewById(R.id.userName);
        TextView userEmail = (TextView)view.findViewById(R.id.userEmail);

        userName.setText(sessionManager.getName());
        userEmail.setText(sessionManager.getEmail());

        notificationCount = (TextView) navigationView.getMenu().findItem(R.id.nav_notification).getActionView();

        initializeDrawerMenu();

        setupDrawerContent(navigationView);

    }

    //TODO: SETTING UP DRAWER CONTENT
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //TODO: CONFIGURING THE ACTION WHEN EACH DRAWER ITEM CLICKED
    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_manage:
                fragmentTransaction(new SettingsFragment() , menuItem);
                break;
            case R.id.nav_profile:
                fragmentTransaction(new ProfileFragment() , menuItem);
                break;
            case R.id.nav_notification:
                if(!Connectivity.isConnected(this) && (!Connectivity.isConnectedMobile(this) || !Connectivity.isConnectedWifi(this)))
                    startActivity(new Intent(this, NetworkError.class));
                else
                    fragmentTransaction(new NotificationFragment() , menuItem);
                break;
            case R.id.nav_signout:
                logOut(menuItem);
                break;
            default:
                fragmentTransaction(new ProductsFragment() , menuItem);
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initializeDrawerMenu(){
        notificationCount.setGravity(Gravity.CENTER_VERTICAL);
        notificationCount.setTextColor(getResources().getColor(R.color.colorAccent));
        notificationCount.setTypeface(null , Typeface.BOLD);
        notificationCount.setText("0");
    }

    private void fragmentTransaction(Fragment fragment, MenuItem menuItem){
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment , fragment.getClass().getSimpleName()).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();

    }
    /* SHOWING TOAST MESSAGE */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //TODO: SIGNING THE USER OUT FROM APP
    private void logOut(MenuItem menuItem){
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Close the navigation drawer
        drawer.closeDrawers();

        if(!Connectivity.isConnected(SliderMenu.this) && (!Connectivity.isConnectedMobile(SliderMenu.this) || !Connectivity.isConnectedWifi(SliderMenu.this))){
            Toast.makeText(SliderMenu.this, getString(R.string.internet_error_msg), Toast.LENGTH_LONG).show();
            return;
        }

        //CALLBACK FOR LOGOUT RESPONSE
        iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.w(TAG, "LOG OUT RESPONSE "+ response);
                try {
                    if(response.getInt("success") == 1){
                        sessionManager.clearLogin();
                        startActivity(new Intent(SliderMenu.this, LoginActivity.class));
                        finish();
                    }
                    else
                        showToast(getString(R.string.error_message));

                }
                catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "SIGN OUT EXCEPTION "+ e.getMessage());
                }
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                Log.e(TAG, "LOG OUT ERROR " + error);
                showToast(getString(R.string.error_message));
            }
        };

        //callVolleyService(AppConfig.SIGN_OUT_URL, "POST" , null);
    }


    //TODO: CALLING VOLLEY SERVICE FOR BACK END COMMUNICATION
    private void callVolleyService(String url, String requestType, JSONObject data){
        volleyService = new VolleyService(iResult, SliderMenu.this);
        if(requestType.equals("POST"))
            volleyService.postRequest(url, "POST" , data);
        else
            volleyService.getRequest(url, "GET" , data);
    }
}
