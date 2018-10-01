package com.imFarhad.inventoryorders.interfaces;

import com.imFarhad.inventoryorders.models.Product;

/**
 * Created by Farhad on 27/09/2018.
 */

public interface ProductItemClickListener {
    void OnItemClick(Product product);
    void OnAddToCartClick(Product product);
    void OnRemoveFromCartClick(Product product);
}
