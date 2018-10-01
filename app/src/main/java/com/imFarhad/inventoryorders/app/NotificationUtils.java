package com.imFarhad.inventoryorders.app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imFarhad.inventoryorders.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Farhad on 17/09/2018.
 */

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    public Context mContext;
    private SessionManager sessionManager;

    public NotificationUtils(Context context){
        this.mContext = context;
        this.sessionManager = new SessionManager(this.mContext);
    }

    public void buildNotificationMessage(final String title, final String message, Intent intent){
        if(TextUtils.isEmpty(message))
            return;

        final  int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ mContext.getPackageName() + "/raw/notification");

        showNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound);

    }
    //TODO: IF IMAGE IS NOT IN THE NOTIFICATION PAYLOAD, SHOWING SMALL NOTIFICATION
    private void showNotification(NotificationCompat.Builder builder, int icon, String title, String message,
                                  PendingIntent intent, Uri alarmSound){

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Preferences preferences = new Preferences(mContext);
        int count = preferences.getNotificationCount();

        Notification notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(intent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setNumber(count)
                .setStyle(inboxStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AppConfig.NOTIFICATION_ID, notification);

    }


    //TODO: PLAYING NOTIFICATION SOUND
    public void playNotificatinSound(){
        try {
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ mContext.getPackageName() + "/raw/notification");

            Ringtone ringtone = RingtoneManager.getRingtone(mContext, uri);
            ringtone.play();;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO:CHECKING APP's STATUS, WHETHER APP IS IN FOREGROUND OR BACKGROUND
    public static boolean isAppInBackground(Context context){
        boolean isAppInBackground = true;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo: runningAppProcessInfo){
                if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    for(String activeProcess: processInfo.pkgList){
                        if(activeProcess.equals(context.getPackageName())){
                            isAppInBackground = false;
                        }
                    }
                }
            }
        }
        else {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName componentName = tasks.get(0).topActivity;
                if (componentName.getPackageName().equals(context.getPackageName())) {
                    isAppInBackground = false;
                }
            }

        }

        return  isAppInBackground;
    }

    //TODO:CLEARING ALL NOTIFICATIONS
    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    //TODO: SENDING NEW TOKEN TO SERVER
    public void sendTokenToServer(String token){

        final String url =  AppConfig.SAVING_TOKEN_URL;

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        Log.w(TAG, "SAVING TOKEN RESPONSE: "+ response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "SAVING TOKEN ERROR: "+ error.toString());
                    }
                })

        {

            /** Passing some request headers* */
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", sessionManager.getToken());
                return headers;
            }
        };
        // add it to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this.mContext);
        requestQueue.add(jsonObjReq);
    }


    //TODO: STORING NEW TOKEN IN SHARED PREFERENCES
    public void storeTokenInPref(String token){
        Preferences preferences = new Preferences(this.mContext);
        preferences.setFCMToken(token);
    }


}
