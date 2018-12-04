package com.imFarhad.inventoryorders.app;

/**
 * Created by Farhad on 17/09/2018.
 */

public class AppConfig {
    public static final String SERVER_URL = "http://ocsas.premiummeat.co.nz/api/";
    public static final String SIGNUP_URL = SERVER_URL + "user/create";
    public static final String SALEMAN_SIGNUP_URL = SERVER_URL + "add/saleman";
    public static final String LOGIN_URL = SERVER_URL + "user/auth";
    public static final String SALEMAN_LOGIN_URL = SERVER_URL + "saleman/auth";
    public static final String CATEGORIES_URL = SERVER_URL + "category/all";
    public static final String PRODUCTS_URL = SERVER_URL + "products/category/";
    public static final String ORDER_SUBMIT_URL = SERVER_URL + "order/submit";
    public static final String ORDERS_URL = SERVER_URL + "order/user/orders/";
    public static final String SALEMAN_ORDERS_URL = SERVER_URL + "order/saleman/single/";
    public static final String ORDER_STATUS_URL = SERVER_URL + "order/saleman/accepted";
    public static final String PUBNUB_PUBLISHKEY = "pub-c-f86699fe-3463-4dc7-a4a6-766de441b451";
    public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-5dcd8e30-c78c-11e8-8f2a-6ea01b4be699";
    public static final String PUBNUB_CHANNEL_NAME = "TrackLocation";
    public static final String STRIPE_KEY = "pk_test_XpE228pCSBMrfoZmC5601SlQ";
}
