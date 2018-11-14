package com.imFarhad.inventoryorders.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.imFarhad.inventoryorders.models.OrderDetails;
import com.imFarhad.inventoryorders.models.OrderModel;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 12/10/2018.
 */

public class OrdersFragment extends Fragment {

    private static final String TAG = OrdersFragment.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ArrayList<Order> orders;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private JSONArray ordersResponse ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        progressDialog = new ProgressDialog(getActivity());
        //orders = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(getActivity());
        OrderItemClickListener orderItemClickListener = new OrderItemClickListener() {
            @Override
            public void OnItemClick(Order order) {
                showDetails(order);
            }
            @Override
            public void showOrderDetails(Order order) {
                showDetails(order);
            }
            @Override
            public void showOrderLocation(Order order) {
                Toast.makeText(getActivity(), "SHOW LOCATION "+ order.getOrder_name(), Toast.LENGTH_SHORT).show();
            }
        };

        if (savedInstanceState == null){
            Log.w(TAG, "SAVED INSTANCE STATE IS NULL.");
            orders = new ArrayList<>();
        }else{
            Log.w(TAG, "SAVED INSTANCE STATE IS NOT NULL.");
            orders = savedInstanceState.getParcelableArrayList("orders");
        }

        ordersAdapter = new OrdersAdapter(getActivity(), this.orders , orderItemClickListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(AppController.getDividerItemDecoration());
        recyclerView.setAdapter(ordersAdapter);
        if (savedInstanceState == null)
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
                Log.w(TAG, response.toString());
                hideDialog();
                if(response.length() == 0){
                    Toast.makeText(getActivity(), "No Order Found.",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                      ordersResponse = response;

                      for(int i=0; i<response.length();i++) {
                          JSONArray orderdata = response.getJSONArray(i);
                          int totalAmount = 0;
                          for(int j=0; j<orderdata.length();j++){
                             JSONObject item = orderdata.getJSONObject(j);
                             totalAmount += Integer.parseInt(item.getString("amount"));
                          }
                          Order order = new Order();
                          order.setOrder_id(i);
                          order.setOrder_name("Order Num " + (i + 1));
                          order.setTotal_amount(totalAmount);
                          order.setPaid_amount(totalAmount/2);
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

    public void extractingData(JSONObject object){
        try{
            JSONArray orders = object.getJSONArray("orders");
            if(orders.length() == 0){
                return;
            }
            else{

                ArrayList<OrderModel> allOrders = new ArrayList<>();
                ArrayList<OrderDetails> orderDetailsArray = new ArrayList<>();

                for(int i=0; i<orders.length(); i++){

                    JSONObject jsonObject = orders.getJSONObject(i).getJSONArray("data").getJSONObject(0);
                    OrderDetails order_Details = new OrderDetails();
                    order_Details.setId(jsonObject.getInt("id"));
                    order_Details.setProduct_id(jsonObject.getInt("product_id"));
                    order_Details.setOrder_id(jsonObject.getInt("order_id"));
                    order_Details.setUnit_price(Integer.parseInt(jsonObject.getString("unit_price")));
                    order_Details.setAmount(Integer.parseInt(jsonObject.getString("amount")));
                    order_Details.setQuantity(jsonObject.getInt("quantity"));
                    order_Details.setCreated_at(jsonObject.getString("created_at"));
                    order_Details.setUpdated_at(jsonObject.getString("updated_at"));
                    order_Details.setUser_id(jsonObject.getInt("user_id"));
                    order_Details.setSaleman_id(jsonObject.getInt("saleman_id"));
                    order_Details.setStatus(jsonObject.getInt("status"));


                    OrderModel orderModel = new OrderModel();

                    if(orderDetailsArray.size() > 0){

                        int size = orderDetailsArray.size();
                        OrderDetails order_details = orderDetailsArray.get(size - 1);

                        if(order_details.getOrder_id() == order_Details.getOrder_id()){
                            orderDetailsArray.add(order_details);
                        }
                        else{

                            orderModel.setStatus(order_details.getStatus());
                            orderModel.setOrderDetails(orderDetailsArray);
                            allOrders.add(orderModel);

                            orderDetailsArray = new ArrayList<>();
                            orderDetailsArray.add(order_details);
                        }
                    }

                    else {
                        int size = orderDetailsArray.size();
                        OrderDetails order_details = orderDetailsArray.get(size - 1);
                        orderDetailsArray.add(order_details);
                    }
                }

            }
        }catch (JSONException e){e.printStackTrace();;}
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

    //TODO: SHOWING SELECTED ORDER'S DETAILS
    private void showDetails(Order order){
        try {
            JSONArray orderDetails = ordersResponse.getJSONArray(order.getOrder_id());
            if(orderDetails.length() == 0){
                Toast.makeText(getActivity(), "NO DETAILS FOUND", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<OrderDetails> selectedOrder = new ArrayList<>();
            for(int i=0; i<orderDetails.length(); i++){
                JSONObject jsonObject = orderDetails.getJSONObject(i);
                OrderDetails order_Details = new OrderDetails();
                order_Details.setId(jsonObject.getInt("id"));
                order_Details.setProduct_id(jsonObject.getInt("product_id"));
                order_Details.setOrder_id(jsonObject.getInt("order_id"));
                order_Details.setUnit_price(Integer.parseInt(jsonObject.getString("unit_price")));
                order_Details.setAmount(Integer.parseInt(jsonObject.getString("amount")));
                order_Details.setQuantity(jsonObject.getInt("quantity"));
                order_Details.setCreated_at(jsonObject.getString("created_at"));
                order_Details.setUpdated_at(jsonObject.getString("updated_at"));
                selectedOrder.add(order_Details);
            }

            if(selectedOrder.size() > 0){
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("selectedOrder", selectedOrder);
                Fragment fragment = new OrderDetailsFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        }catch (JSONException e){ e.printStackTrace();}
    }

}
