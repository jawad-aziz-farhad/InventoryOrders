package com.imFarhad.inventoryorders.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Farhad on 17/09/2018.
 */

public class Preferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences fcmPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor fcmEditor;
    private Context mContext;

    private int PRIVATE_MODE = 0;

    public static final String FCM_PREF = "FCM_TOKEN";
    public static final String TOKEN = "TOKEN";
    public static final String NOTIF_PREF = "NOTIFICATION_COUNT";
    public static final String COUNT_KEY = "Count";
    public static final String NOTIFICATIONS_SETTINGS = "NOTIFICATIONS_SETTINGS";

    public Preferences(Context context){
        this.mContext = context;
        fcmPreferences = mContext.getSharedPreferences(FCM_PREF,PRIVATE_MODE);
        sharedPreferences = mContext.getSharedPreferences(NOTIF_PREF,PRIVATE_MODE);
    }

    //TODO: SETTING FCM TOKEN IN SHARED PREFERENCES
    public void setFCMToken(String token){
        Log.w("PREFERENCES ", "PREFERENCE SAVING: "+ token);
        fcmEditor = fcmPreferences.edit();
        fcmEditor.putString(TOKEN, token).apply();
    }

    //TODO: SETTING NOTIFICATION COUNT IN SHARED PREFERENCE
    public void setNotificationCount(int count){
        editor = sharedPreferences.edit();
        editor.putInt(COUNT_KEY, count).apply();
    }
    //TODO: GETTING COUNT FROM SHARED PREFERENCES
    public int getNotificationCount(){
        sharedPreferences = mContext.getSharedPreferences(NOTIF_PREF,PRIVATE_MODE);
        return sharedPreferences.getInt(COUNT_KEY, 0);
    }

    //TODO: SETTING NOTIFICATION SETTINGS
    public void setNotificatinsSettings(boolean status){
        editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATIONS_SETTINGS, status);
        editor.apply();
    }

    //TODO: GETTING NOTIFICATIONS SETTINGS
    public boolean getNotificationsSettings(){
        sharedPreferences = mContext.getSharedPreferences(NOTIF_PREF, PRIVATE_MODE);
        return sharedPreferences.getBoolean(NOTIFICATIONS_SETTINGS, true);
    }

    //TODO: GETTING FCM TOKEN
    public String getFCMToken(){
        fcmPreferences = mContext.getSharedPreferences(FCM_PREF,PRIVATE_MODE);
        return fcmPreferences.getString(TOKEN, null);
    }

    //TODO: REMOVING NOTIFICATION
    public void clearNotifications(){
        editor = sharedPreferences.edit();
        editor.remove(COUNT_KEY).apply();
    }

    //TODO: REMOVING FCM TOKEN PREFERNCES
    public void clearFCMPrefernces(){
        fcmEditor = fcmPreferences.edit();
        fcmEditor.remove(TOKEN).apply();
    }
}
