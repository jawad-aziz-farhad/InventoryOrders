package com.imFarhad.inventoryorders.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 17/09/2018.
 */

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "APP_LOGIN";
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";
    private static final String TOKEN = "token";
    public static final String NAME  = "name";
    public static final String EMAIL = "email";
    public static final String TYPE  = "user_type";
    public static final String  ID = "";
    public static final String ADDRESS = "address";
    public static final String PROFILE_IMAGE = "profile_image";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }
    //TODO: SETTING LOGIN STATUS AND AUTH TOKEN TO SHARED PREFERENCES
    public void setUpUser(JSONObject user){
        try {
            editor = sharedPreferences.edit();
            String name = null;
            if(user.has("name"))
                name = user.getString("name");
            else {
                name = user.getString("firstName") + " " + user.getString("lastName");
                editor.putString(TYPE, "saleman");
            }
            editor.putString(NAME, name);

            if(user.has("app_token"))
                editor.putString(TOKEN, user.getString("app_token"));

            if(user.has("type"))
                editor.putString(TYPE, user.getString("type"));

            editor.putString(EMAIL, user.getString("email"));
            editor.putBoolean(IS_LOGGED_IN_KEY, true);
            editor.putInt(ID , user.getInt("id"));

            //editor.putString(ADDRESS, user.getString("address"));
            if(user.has("image"))
                editor.putString(PROFILE_IMAGE, user.getString("image"));

            editor.apply();
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    //TODO: GETTING USERS DETAILS
    public Map<String, String> getUser(){
        Map<String, String> user = new HashMap<String, String>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(TYPE, sharedPreferences.getString(TYPE, null));
        //user.put(ADDRESS, sharedPreferences.getString(ADDRESS, null));
        user.put(PROFILE_IMAGE, sharedPreferences.getString(PROFILE_IMAGE, null));
        return user;
    }
    //TODO: GETTING LOGIN STATUS
    public boolean isLoggedIn(){ return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false); }
    //TODO: GETTING TOKEN
    public String getToken(){ return sharedPreferences.getString(TOKEN,null);}
    //TODO: GETTING NAME
    public String getName(){ return sharedPreferences.getString(NAME,null);}
    //TODO: GETTING EMAIL
    public String getEmail(){ return sharedPreferences.getString(EMAIL,null);}
    //TODO: GETTING ID
    public int getId(){ return sharedPreferences.getInt(ID,0);}
    //TODO: GETTING IMAGE
    public String getImage(){ return sharedPreferences.getString(PROFILE_IMAGE,null);}
    //TODO: GETTING USER TYPE (SHOP KEEPER OR SALE)
    public String getType(){ return sharedPreferences.getString(TYPE,null);}


    //TODO: CLEARING PREFERENCES
    public void clearLogin(){
        editor = sharedPreferences.edit();
        editor.remove(IS_LOGGED_IN_KEY);
        editor.remove(TOKEN);
        editor.remove(NAME);
        editor.remove(EMAIL);
        editor.remove(PROFILE_IMAGE);
        editor.apply();

    }

}
