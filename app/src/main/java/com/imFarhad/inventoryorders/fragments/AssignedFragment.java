package com.imFarhad.inventoryorders.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.MapsActivity;
import com.imFarhad.inventoryorders.activities.ShowLocation;
import com.imFarhad.inventoryorders.adapters.OrdersAdapter;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.interfaces._IResult;
import com.imFarhad.inventoryorders.models.Order;
import com.imFarhad.inventoryorders.models.OrderDetails;
import com.imFarhad.inventoryorders.models.OrderModel;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Farhad on 12/11/2018.
 */

public class AssignedFragment extends Fragment {

    private static final String TAG = AssignedFragment.class.getSimpleName();
    private Button OrderStatus;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ArrayList<Order> orders;
    private OrdersAdapter ordersAdapter;
    private OrderItemClickListener orderItemClickListener;

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

        progressDialog = new ProgressDialog(getActivity());
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        orderItemClickListener = new OrderItemClickListener() {
            @Override
            public void OnItemClick(Order order) {
                showDetails(order);
            }
            @Override
            public void showOrderDetails(Order order) { showDetails(order);}
            @Override
            public void showOrderLocation(Order order) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                Log.w(TAG , "Selected Order Id : " + order.getOrder_id());
                intent.putExtra(" ", String.valueOf(order.getOrder_id()));
                startActivity(intent);
            }
        };
        progressDialog = new ProgressDialog(getActivity());

        getOrders();

        return view;
    }

    //TODO: GETTING ASSIGNED ORDERS TO THE SALE MAN
    private void getOrders(){
        showDialog();
        //CALLBACK FOR ORDERS RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                hideDialog();
                Log.w(TAG, "Assigned Orders To Sale Man "+ response.toString());
                if(response.has("error"))
                    Toast.makeText(getActivity(), getString(R.string.error_message),Toast.LENGTH_LONG).show();
                else{
                    if (response.length() == 0) {
                        Toast.makeText(getActivity(), "No Order Found.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    getResponse(response);
                }
            }
            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Assigned Orders To SaleMan Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                Toast.makeText(getActivity(), getString(R.string.error_message),Toast.LENGTH_LONG).show();
            }
        };

        int user_id = new SessionManager(getActivity()).getId();
        String uri = AppConfig.SALEMAN_ORDERS_URL + user_id;
        Log.w(TAG, uri);
        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService.getRequest(uri , "GET", null);
    }

    //TODO EXTRACTING RESPONSE FROM JSON OBJECT
    public void getResponse(JSONObject response){
        try{
            JSONArray orders = response.getJSONArray("orders");
            if(orders.length() == 0){
                return;
            }
            else{
                ArrayList<OrderModel> allOrders = new ArrayList<>();
                for(int i=0; i<orders.length(); i++) {

                    JSONObject order = orders.getJSONObject(i);
                    JSONObject _order = order.getJSONObject("order");
                    JSONArray details = order.getJSONArray("orderDetails");

                    ArrayList<OrderDetails> orderDetailsArray = new ArrayList<>();

                    for(int j=0; j<details.length(); j++) {

                        OrderDetails order_Details = new OrderDetails();
                        JSONObject jsonObject = details.getJSONObject(j);
                        order_Details.setId(jsonObject.getInt("id"));
                        order_Details.setProduct_id(jsonObject.getInt("product_id"));
                        order_Details.setOrder_id(jsonObject.getInt("order_id"));
                        order_Details.setUnit_price(Integer.parseInt(jsonObject.getString("unit_price")));
                        order_Details.setAmount(Integer.parseInt(jsonObject.getString("amount")));
                        order_Details.setQuantity(jsonObject.getInt("quantity"));
                        order_Details.setCreated_at(jsonObject.getString("created_at"));
                        order_Details.setUpdated_at(jsonObject.getString("updated_at"));

                        order_Details.setUser_id(_order.getInt("user_id"));
                        order_Details.setSaleman_id(_order.getInt("saleman_id"));
                        order_Details.setStatus(_order.getInt("status"));

                        orderDetailsArray.add(order_Details);
                    }

                    OrderModel orderModel = new OrderModel();
                    orderModel.setStatus(_order.getInt("status"));
                    orderModel.setOrderDetails(orderDetailsArray);
                    allOrders.add(orderModel);

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
                ordersAdapter = new OrdersAdapter(getActivity(), this.orders , orderItemClickListener, "saleman");
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
