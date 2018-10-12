package com.imFarhad.inventoryorders.app;

/**
 * Created by Farhad on 17/09/2018.
 */

public class AppConfig {
    public static final String SERVER_URL = "http://stark-brook-90313.herokuapp.com/api/";
    public static final String SIGNUP_URL = SERVER_URL + "user/create";
    public static final String LOGIN_URL = SERVER_URL + "user/auth";
    public static final String TOKEN_VALIDATION_URL = SERVER_URL + "validate-token/";
    public static final String CATEGORIES_URL = SERVER_URL + "category/all";
    public static final String PRODUCTS_URL = SERVER_URL + "products/category/";
    public static final String ORDER_SUBMIT_URL = SERVER_URL + "order/submit";
    public static final String SAVING_TOKEN_URL = SERVER_URL + "";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final int NOTIFICATION_ID = 100;
    public static final String PUBNUB_PUBLISH_KEY = "pub-c-f86699fe-3463-4dc7-a4a6-766de441b451";
    public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-5dcd8e30-c78c-11e8-8f2a-6ea01b4be699";
}
