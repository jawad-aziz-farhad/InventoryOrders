package com.imFarhad.inventoryorders.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.adapters.CartItemsAdapter;
import com.imFarhad.inventoryorders.adapters.OrderDetailsAdapter;
import com.imFarhad.inventoryorders.adapters.OrdersAdapter;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.models.OrderDetails;

import java.util.ArrayList;

public class OrderDetailsFragment extends Fragment {

    private ArrayList<OrderDetails> selectedOrder;
    private OrderDetailsAdapter orderDetailsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container,false);

        selectedOrder = new ArrayList<>();
        if(getArguments() != null)
            selectedOrder = getArguments().getParcelableArrayList("selectedOrder");

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        orderDetailsAdapter = new OrderDetailsAdapter(getActivity(), selectedOrder);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(AppController.getDividerItemDecoration());
        recyclerView.setAdapter(orderDetailsAdapter);
        return view;
    }
}
