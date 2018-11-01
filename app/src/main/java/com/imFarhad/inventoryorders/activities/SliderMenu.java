package com.imFarhad.inventoryorders.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.fragments.CategoriesFragment;
import com.imFarhad.inventoryorders.fragments.OrdersFragment;
import com.imFarhad.inventoryorders.fragments.ProfileFragment;
import com.imFarhad.inventoryorders.fragments.SettingsFragment;

public class SliderMenu extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private static final String TAG = SliderMenu.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_menu);

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

        setupDrawerContent(navigationView);
        show_hide_item(navigationView);

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

    //TODO: SHOWING HIDING MENU ITEMS
    private void show_hide_item(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        String type = new SessionManager(this).getType();
        if(type.equals("salesman")) {
            menu.findItem(R.id.nav_orders).setVisible(false);
        }
    }

    //TODO: CONFIGURING THE ACTION WHEN EACH DRAWER ITEM CLICKED
    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentTransaction(new CategoriesFragment() , menuItem);
                break;
            case R.id.nav_manage:
                fragmentTransaction(new SettingsFragment() , menuItem);
                break;
            case R.id.nav_profile:
                fragmentTransaction(new ProfileFragment() , menuItem);
                break;
            case R.id.nav_orders:
                if(sessionManager.getType().equals("shopkeeper"))
                    fragmentTransaction(new OrdersFragment() , menuItem);
                else
                    menuItem.setVisible(false);
                break;
            case R.id.nav_signout:
                logOut(menuItem);
                break;
            default:
                fragmentTransaction(new CategoriesFragment() , menuItem);
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

    //TODO: SIGNING THE USER OUT FROM APP
    private void logOut(MenuItem menuItem){
        menuItem.setChecked(true);
        drawer.closeDrawers();
        new SessionManager(this).clearLogin();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
