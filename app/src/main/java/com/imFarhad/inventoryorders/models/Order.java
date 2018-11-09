package com.imFarhad.inventoryorders.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

    private int order_id;
    private String order_name;
    private int paid_amount;
    private int total_amount;

    public Order() {}

    public Order(Parcel parcel){
        order_id = parcel.readInt();
        paid_amount = parcel.readInt();
        total_amount = parcel.readInt();
        order_name = parcel.readString();
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(int paid_amount) {
        this.paid_amount = paid_amount;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(order_id);
        parcel.writeInt(paid_amount);
        parcel.writeInt(total_amount);
        parcel.writeString(order_name);

    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>(){

        @Override
        public Order createFromParcel(Parcel parcel) {
            return new Order(parcel);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
