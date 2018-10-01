package com.imFarhad.inventoryorders.models;

/**
 * Created by Farhad on 17/09/2018.
 */

public class Notification {

    public static final String TABLE_NAME= "Notifications";
    // COLUMN NAMES
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TIMESTAMP = "timeStamp";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_STATUS = "status";

    private int id;
    private String title;
    private String url;
    private String timeStamp;
    private int status;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_URL + " TEXT,"
                    + COLUMN_STATUS + " INTEGER , "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Notification(){}

    public Notification(int id, String url, int status , String timeStamp ) {
        this.id = id;
        this.url = url;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    //TODO: SETTERS
    public void setTitle(String title){ this.title = title; }
    public void setUrl(String url) { this.url = url; }
    public void setId(int id) { this.id = id;}
    public void setStatus(int status){ this.status = status; }
    public void setTimestamp(String timeStamp) { this.timeStamp = timeStamp; }


    //TODO: GETTERS
    public int getId() {return id; }
    public String getTitle(){ return  this.title; }
    public String getUrl() { return url; }
    public int getStatus() { return  status; }
    public String getTimestamp() { return timeStamp; }
}
