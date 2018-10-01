package com.imFarhad.inventoryorders.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Farhad on 17/09/2018.
 */

public class Product implements Parcelable{

    private int id;
    private String name;
    private String price;
    private int cat_id;
    private String description;
    private String image;
    private String totalProductPrice;
    private int quantity;

    public Product(){}

    public Product(Parcel parcel){

        id = parcel.readInt();
        cat_id = parcel.readInt();
        quantity = parcel.readInt();

        name = parcel.readString();
        price = parcel.readString();
        totalProductPrice = parcel.readString();
        description = parcel.readString();
        image = parcel.readString();


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(String totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity(){ return  this.quantity;}

    public void setQuantity(int quantity){ this.quantity = quantity;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(cat_id);
        parcel.writeInt(quantity);
        parcel.writeString(name);
        parcel.writeString(price);
        parcel.writeString(totalProductPrice);
        parcel.writeString(description);
        parcel.writeString(image);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>(){

        @Override
        public Product createFromParcel(Parcel parcel) {
            return new Product(parcel);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
