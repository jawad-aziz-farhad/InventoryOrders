package com.imFarhad.inventoryorders.interfaces;

import com.imFarhad.inventoryorders.models.Order;

public interface OrderItemClickListener {
    void OnItemClick(Order order);
    void showOrderDetails(Order order);
    void showOrderLocation(Order order);
}
