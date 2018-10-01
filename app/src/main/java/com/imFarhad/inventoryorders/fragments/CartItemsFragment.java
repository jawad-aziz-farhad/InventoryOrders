package com.imFarhad.inventoryorders.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.adapters.CartItemsAdapter;
import com.imFarhad.inventoryorders.interfaces.ItemTouchListener;
import com.imFarhad.inventoryorders.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartItemsFragment extends Fragment implements ItemTouchListener{

    private static final String TAG = CartItemsFragment.class.getSimpleName();
    private ArrayList<Product> cartItems;
    private CartItemsAdapter cartItemsAdapter;
    private RelativeLayout recyclerViewLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            RelativeLayout relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.cart_wrapper_layout);
            relativeLayout.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        cartItems = new ArrayList<>();
            if(getArguments() != null)
                cartItems = getArguments().getParcelableArrayList("products");
        recyclerViewLayout = (RelativeLayout)view.findViewById(R.id.recycler_view_layout);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        cartItemsAdapter = new CartItemsAdapter(getActivity(), cartItems);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(cartItemsAdapter);
        return view;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartItemsAdapter.ViewHolder){
            String name = cartItems.get(viewHolder.getAdapterPosition()).getName();

            final Product deletedItem = cartItems.get(viewHolder.getAdapterPosition());
            final int deletedItemIndex = viewHolder.getAdapterPosition();

            cartItemsAdapter.removeCartItem(deletedItemIndex);

            Snackbar snackbar = Snackbar.make(recyclerViewLayout , name + " removed from Cart! ", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartItemsAdapter.resotreCartItem(deletedItemIndex, deletedItem);
                }
            });

            snackbar.setActionTextColor(getActivity().getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    }
}
