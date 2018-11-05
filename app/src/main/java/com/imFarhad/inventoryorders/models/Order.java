package com.imFarhad.inventoryorders.models;

public class Order {

    private int order_id;
    private String order_name;
    private int paid_amount;
    private int total_amount;

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
}
