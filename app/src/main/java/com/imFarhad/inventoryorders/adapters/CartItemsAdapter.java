package com.imFarhad.inventoryorders.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.models.Product;

import java.util.ArrayList;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder>{

    public Context context;
    public ArrayList<Product> cartItems;

    public CartItemsAdapter(Context context, ArrayList<Product> cartItems){
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Product product = cartItems.get(i);
        viewHolder.cartItemName.setText(product.getName());
        viewHolder.cartItemPrice.setText(product.getPrice());
        viewHolder.cartItemDescription.setText(product.getDescription());

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cartItemName;
        public TextView cartItemDescription;
        public TextView cartItemPrice;
        public ImageView carItemImage;
        public RelativeLayout foreGroundLayout, backGroundLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemName = (TextView)itemView.findViewById(R.id.cart_item_name);
            cartItemDescription = (TextView)itemView.findViewById(R.id.cart_item_description);
            cartItemPrice = (TextView)itemView.findViewById(R.id.cart_item_price);
            foreGroundLayout = (RelativeLayout)itemView.findViewById(R.id.view_foreground);
            backGroundLayout = (RelativeLayout)itemView.findViewById(R.id.view_background);
        }


    }
    //TODO: REMOVING CART ITEM
    public void removeCartItem(int position){
        cartItems.remove(position);
        notifyItemRemoved(position);
    }

    public void resotreCartItem(int position, Product product){
        cartItems.add(position, product);

        notifyItemInserted(position);
    }

}
