package com.imFarhad.inventoryorders.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetails implements Parcelable {

    private int id;
    private int order_id;
    private int product_id;
    private int quantity;
    private int unit_price;
    private int amount;
    private String created_at;
    private String updated_at;

    public OrderDetails() {}

    public OrderDetails(Parcel parcel){
        id = parcel.readInt();;
        order_id = parcel.readInt();;
        product_id = parcel.readInt();;
        quantity = parcel.readInt();;
        unit_price = parcel.readInt();;
        amount = parcel.readInt();;
        created_at = parcel.readString();;
        updated_at = parcel.readString();;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(order_id);
        parcel.writeInt(product_id);
        parcel.writeInt(quantity);
        parcel.writeInt(unit_price);
        parcel.writeInt(amount);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }

    public static final Parcelable.Creator<OrderDetails> CREATOR = new Parcelable.Creator<OrderDetails>(){

        @Override
        public OrderDetails createFromParcel(Parcel parcel) {
            return new OrderDetails(parcel);
        }

        @Override
        public OrderDetails[] newArray(int size) {
            return new OrderDetails[size];
        }
    };
}
