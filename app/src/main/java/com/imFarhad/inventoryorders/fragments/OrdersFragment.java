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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.ShowLocation;
import com.imFarhad.inventoryorders.adapters.OrdersAdapter;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.models.Order;
import com.imFarhad.inventoryorders.models.OrderDetails;
import com.imFarhad.inventoryorders.models.OrderModel;
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
    public OrdersAdapter ordersAdapter;
    private OrderItemClickListener orderItemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        progressDialog = new ProgressDialog(getActivity());
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(getActivity());
        orderItemClickListener = new OrderItemClickListener() {
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
                Intent intent = new Intent(getActivity(), ShowLocation.class);
                intent.putExtra("OrderId", order.getOrder_id());
                startActivity(intent);
            }
        };

        orders = new ArrayList<>();
        getOrders();
        return view;
    }

    //TODO: GETTING PENDING ORDERS
    private void getOrders(){
        showDialog();
        //CALLBACK FOR ORDERS RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.w(TAG, response.toString());
                hideDialog();
                if(response.length() == 0){
                    Toast.makeText(getActivity(), "No Order Found.",Toast.LENGTH_LONG).show();
                    return;
                }
                extractingData(response);
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
        volleyService.getRequest(uri , "GET", null);
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
                            orderDetailsArray.add(order_Details);
                            if(i == (orders.length() - 1)){
                                orderModel.setStatus(order_Details.getStatus());
                                orderModel.setOrderDetails(orderDetailsArray);
                                allOrders.add(orderModel);
                            }
                        }
                        else{
                            orderModel.setStatus(order_details.getStatus());
                            orderModel.setOrderDetails(orderDetailsArray);
                            allOrders.add(orderModel);

                            orderDetailsArray = new ArrayList<>();
                            orderDetailsArray.add(order_Details);
                        }
                    }

                    else {
                        orderDetailsArray.add(order_Details);
                        if(orders.length() == 1){
                            orderModel.setStatus(order_Details.getStatus());
                            orderModel.setOrderDetails(orderDetailsArray);
                            allOrders.add(orderModel);
                        }
                    }
                }

                this.orders = new ArrayList<>();

                for(int i=0; i<allOrders.size(); i++) {
                    OrderModel orderModel = allOrders.get(i);
                    if(orderModel.getOrderDetails().size() > 0) {
                        int amount = 0;
                        for (int j = 0; j < orderModel.getOrderDetails().size(); j++) {
                            OrderDetails orderDetails = orderModel.getOrderDetails().get(j);
                            amount += orderDetails.getAmount();
                        }
                        Order order = new Order();
                        order.setOrder_id(orderModel.getOrderDetails().get(0).getOrder_id());
                        order.setStatus(orderModel.getOrderDetails().get(0).getStatus());
                        order.setTotal_amount(amount);
                        order.setPaid_amount(amount/2);
                        order.setOrder_name("Order Num: " + (i+ 1));
                        order.setOrderDetailsArrayList(orderModel.getOrderDetails());

                        this.orders.add(order);
                    }
                }
                ordersAdapter = new OrdersAdapter(getActivity(), this.orders , orderItemClickListener , "shopkeeper");
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(AppController.getDividerItemDecoration());
                recyclerView.setAdapter(ordersAdapter);

            }
        }catch (JSONException e){e.printStackTrace();}
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
    private void showDetails(Order order) {
        ArrayList<OrderDetails> selectedOrder = new ArrayList<>();
        selectedOrder = order.getOrderDetailsArrayList();
        if(selectedOrder.size() > 0){
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("selectedOrder", selectedOrder);
            Fragment fragment = new OrderDetailsFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
        }
    }

}
