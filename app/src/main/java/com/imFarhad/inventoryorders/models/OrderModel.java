package com.imFarhad.inventoryorders.models;

import java.util.ArrayList;

/**
 * Created by Farhad on 14/11/2018.
 */

public class OrderModel {

    private int status;
    private ArrayList<OrderDetails> orderDetails;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
