package com.imFarhad.inventoryorders.services;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.imFarhad.inventoryorders.activities.Splash;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.app.BadgeCount;
import com.imFarhad.inventoryorders.app.NotificationUtils;
import com.imFarhad.inventoryorders.app.Preferences;
import com.imFarhad.inventoryorders.app.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Farhad on 17/09/2018.
 */

public class FireBaseMessagingService extends FirebaseMessagingService {

        private static final String TAG = FirebaseMessagingService.class.getSimpleName();
        private NotificationUtils notificationUtils;

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);

            if(remoteMessage == null)
                return;

            if(remoteMessage.getNotification() != null){
                Log.d(TAG, "Notification: "+ remoteMessage.getNotification());
                handleNotification(remoteMessage.getNotification().getBody());
            }

            if(remoteMessage.getData().size() > 0) {

                Log.w(TAG, "Data PayLoad: "+ remoteMessage.getData().toString());
                try{
                    JSONObject object = new JSONObject(remoteMessage.getData());
                    handleData(object);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    private void handleNotification(String message){
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificatinSound();

        if(!NotificationUtils.isAppInBackground(getApplicationContext())){
            Intent intent = new Intent(AppConfig.PUSH_NOTIFICATION);
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }
    private void handleData(JSONObject data){

        try {
            String title     =  data.getString("title");
            String message   = data.getString("body");
            String url       = data.getString("click_action");

            Log.w(TAG, "TITLE: "+ title + "\nMESSAGE: "+ message);

            Preferences preferences = new Preferences(getApplicationContext());
            int previousCount = preferences.getNotificationCount();
            int count = 1 + previousCount;
            preferences.setNotificationCount(count);

            ShortcutBadger.applyCount(this, count);

            // IF APP IS NOT IN FOREGROUND
            if(!NotificationUtils.isAppInBackground(getApplicationContext())){

                Log.w(TAG, "App is in ForeGround");
                //this.setNotificationBadge(count);
                Intent intent = new Intent(AppConfig.PUSH_NOTIFICATION);
                intent.putExtra("url", url);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificatinSound();
            }
            //IF APP IS IN BACKGROUND
            else {
                Log.w(TAG, "App is in Background");
                Intent intent = new Intent(getApplicationContext(), Splash.class);
                intent.putExtra("url", url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.buildNotificationMessage(title, message,intent);

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    //TODO: SETTING NOTIFICATION BADGE
    private void setNotificationBadge(final int count){

        Handler h = new Handler(getApplicationContext().getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                AppController appController = AppController.getInstance();
                LayerDrawable drawable = appController.getDrawable();
                BadgeCount.setBadgeCount(getApplicationContext(),drawable, String.valueOf(count));
            }
        });
    }



    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.w("NEW TOKEN ", token);

        NotificationUtils notificationUtils = new NotificationUtils(this);
        SessionManager sessionManager = new SessionManager(FireBaseMessagingService.this);
        if(sessionManager.getToken() != null)
            notificationUtils.sendTokenToServer(token);

        notificationUtils.storeTokenInPref(token);

        Intent intent = new Intent(AppConfig.REGISTRATION_COMPLETE);
        intent.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
