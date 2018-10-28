package com.imFarhad.inventoryorders.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.ShowLocation;
import com.imFarhad.inventoryorders.adapters.OrdersAdapter;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.interfaces._IResult;
import com.imFarhad.inventoryorders.models.Order;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Farhad on 12/10/2018.
 */

public class OrdersFragment extends Fragment {

    private static final String TAG = OrdersFragment.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ArrayList<Order> orders;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private Button check_location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders, container, false);
        progressDialog = new ProgressDialog(getActivity());
        orders = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        check_location = (Button)view.findViewById(R.id.check_location);
        progressDialog = new ProgressDialog(getActivity());
        OrderItemClickListener orderItemClickListener = new OrderItemClickListener() {
            @Override
            public void OnItemClick(Order order) {
                Toast.makeText(getActivity(), String.valueOf(order.getId()), Toast.LENGTH_LONG).show();
            }
        };

        check_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ShowLocation.class));
            }
        });

        ordersAdapter = new OrdersAdapter(getActivity(), this.orders , orderItemClickListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(AppController.getDividerItemDecoration());
        recyclerView.setAdapter(ordersAdapter);
        getOrders();
        return view;
    }

    //TODO: GETTING PENDING ORDERS
    private void getOrders(){
        showDialog();
        //CALLBACK FOR ORDERS RESPONSE
        _IResult iResult = new _IResult() {
            @Override
            public void onSuccess(String requestType, JSONArray response) {
                hideDialog();
                try {
                    JSONArray _orders = response.getJSONArray(0);
                    for(int i=0; i<_orders.length();i++){
                       JSONObject jsonObject = _orders.getJSONObject(i);
                       Log.w(TAG, jsonObject.getString("unit_price"));
                        Order order = new Order();
                        order.setId(jsonObject.getInt("id"));
                        order.setProduct_id(jsonObject.getInt("product_id"));
                        order.setOrder_id(jsonObject.getInt("order_id"));
                        order.setUnit_price(Integer.parseInt(jsonObject.getString("unit_price")));
                        order.setAmount(Integer.parseInt(jsonObject.getString("amount")));
                        order.setCreated_at(jsonObject.getString("created_at"));
                        order.setUpdated_at(jsonObject.getString("updated_at"));
                        orders.add(order);

                    }
                }catch (JSONException e){ e.printStackTrace();}

                ordersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Shop Keeper's Orders Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_message),Toast.LENGTH_LONG).show();
            }
        };

        int user_id = new SessionManager(getActivity()).getId();
        String uri = AppConfig.ORDERS_URL + user_id;
        Log.w(TAG, uri);
        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService._getRequest(uri , "GET");
    }

    //TODO: SHOWING PROGRESS DIALOG
    public void showDialog() {
        progressDialog.setMessage(getActivity().getString(R.string.loader_msg));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    //TODO: HIDING PROGRESS DIALOG
    public void hideDialog() {        if (progressDialog.isShowing())
        progressDialog.dismiss();
    }


}
