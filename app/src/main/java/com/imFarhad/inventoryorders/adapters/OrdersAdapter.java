package com.imFarhad.inventoryorders.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.fragments.OrdersFragment;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.models.Order;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public Context context;
    private ArrayList<Order> orders;
    private OrderItemClickListener orderItemClickListener;
    public Order order = null;
    public String ordersFor = null;
    public static final String TAG = OrdersAdapter.class.getSimpleName();
    public ProgressDialog progressDialog;

    public OrdersAdapter(Context context, ArrayList<Order> orders, OrderItemClickListener orderItemClickListener, String ordersFor){
        this.context = context;
        this.orders  = orders;
        this.ordersFor = ordersFor;
        this.progressDialog = new ProgressDialog(this.context);
        this.orderItemClickListener = orderItemClickListener;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders, viewGroup, false);

        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder viewHolder, int i) {

        final Order order = orders.get(i);
        Log.w(TAG, "Order Status at " + (i + 1) + " is "  + order.getStatus());
        //VIEW FOR SALE MAN
        if(ordersFor.equals("saleman")){

            if(order.getStatus() == 2 || order.getStatus() == 5) {
                //viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }
            else if(order.getStatus() == 3){
                //viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green));
                viewHolder.overFlow.setVisibility(View.GONE);
            }
        }

        //VIEW FOR SHOP KEEPER
        else {
            if(order.getStatus() == 2) {
                //viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }

            if(order.getStatus() == 3 || order.getStatus() == 5){
                //viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green));
                viewHolder.overFlow.setVisibility(View.GONE);
            }
        }

        viewHolder.bind(order, orderItemClickListener);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           orderItemClickListener.OnItemClick(order);
            }
        });
        viewHolder.overFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Log.w(TAG, "Order Id "+ order.getOrder_id() + " Status " + order.getStatus());
            setOrder(order);
            showPopupMenu(view);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderName;
        private TextView orderPaidAmount;
        private TextView orderTotalAmount;
        private ImageView overFlow;

        public final String TAG = ProductsAdapter.class.getSimpleName();

        public ViewHolder(final View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            orderName        = (TextView) itemView.findViewById(R.id.orderName);
            orderPaidAmount  = (TextView) itemView.findViewById(R.id.orderPaidAmount);
            orderTotalAmount = (TextView) itemView.findViewById(R.id.orderTotalAmount);
            overFlow         = (ImageView) itemView.findViewById(R.id.overflow);
        }

        public void bind(final Order order, final OrderItemClickListener listener) {
            orderName.setText(order.getOrder_name());
            orderPaidAmount.setText(context.getString(R.string.paid));
            orderTotalAmount.setText(context.getString(R.string.total));
            String currency = "  " + itemView.getContext().getString(R.string.currency);
            orderPaidAmount.append("  "  + String.valueOf(order.getPaid_amount()) + "  " + currency);
            orderTotalAmount.append("  " + String.valueOf(order.getTotal_amount())+ "  " + currency);
        }
    }


    //TODO Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.slider_menu, popup.getMenu());
        Menu popupMenu = popup.getMenu();
        setstatusMenuItem(popupMenu);
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(this.orderItemClickListener));
        popup.show();
    }

    //TODO: STATUS MENU ITEM TEXT CHANGE
    public void setstatusMenuItem(Menu popupMenu){
        Log.w(TAG, "Status " + getOrder().getStatus());
        if(ordersFor.equals("saleman")) {
            if (getOrder().getStatus() == 1)
                popupMenu.findItem(R.id.action_status_change).setTitle("ACCEPT");
            else if (getOrder().getStatus() == 2)
                popupMenu.findItem(R.id.action_status_change).setTitle("Delivered");
        }
        else{

            if (getOrder().getStatus() == 2) {
                popupMenu.findItem(R.id.action_status_change).setVisible(true);
                popupMenu.findItem(R.id.action_status_change).setTitle("Received");
            }
            else
                popupMenu.findItem(R.id.action_status_change).setVisible(false);
        }

    }

    // TODO Click listener for popup menu items
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private OrderItemClickListener orderItemClickListener;
        public MyMenuItemClickListener(OrderItemClickListener orderItemClickListener) {
            this.orderItemClickListener = orderItemClickListener;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_status_change:
                    if(ordersFor.equals("saleman")) {
                        if(getOrder().getStatus() == 2 || getOrder().getStatus() == 5)
                            changeStatus(getOrder());
                        else {
                            //if (!checkAllOrdersStatus())
                                changeStatus(getOrder());
                        }
                    }
                    else
                        changeStatus(getOrder());
                    return  true;
                case R.id.action_details:
                    orderItemClickListener.showOrderDetails(getOrder());
                    return true;
                case R.id.action_location:
                    orderItemClickListener.showOrderLocation(getOrder());
                    return true;
                default:
            }
            return false;
        }
    }

    //TODO: CHECKING ALL THE ORDERS BEFORE CALLING THE ORDER STATUS CHANGE END POINT IF SOME ORDER IS ALREAY ACCEPTED AND NOT DELIVERED, NO OTHER ORDER WILL BE ACCEPTED
    public boolean checkAllOrdersStatus() {
        for(Order order: this.orders) {
            if(order.getStatus() == 2) {
                return true;
            }
        }
        return false;
    }

    /*
    * STATUS 0 : UN - ASSIGNED
    * STATUS 1 : ASSIGNED
    * STATUS 2 : ACCEPTED
    * STATUS 3 : RECEIVED BY SHOP KEEPER
    * STATUS 4 : DELIVERED BY SALE MAN
    * STATUS 5 : ORDER CLOSED
    */

    //TODO CHANGING ORDER STATUS
    public void changeStatus(Order order){
        progressDialog.setMessage(context.getString(R.string.loader_msg));
        showDialog();

        HashMap<String, Object> params = new HashMap<>();
        params.put("order_id", order.getOrder_id());
        params.put("request_type", ordersFor);

        if(ordersFor.equals("saleman")){
            if(order.getStatus() == 1)
                params.put("status", 2);
            else if(order.getStatus() == 2 || order.getStatus() == 5)
                params.put("status", 3);
        }

        else if(ordersFor.equals("shopkeeper")){
            if(order.getStatus() == 2)
                params.put("status", 5);
        }

         //CALLBACK FOR STATUS CHANGE RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                hideDialog();
                Log.d(TAG,"Order Status Change Response: "+ response.toString());
                if(response.has("success")){
                    try {
                        JSONObject jsonObject = response.getJSONObject("orders");
                        getOrder().setStatus(jsonObject.getInt("status"));
                        // FOR SALE-MAN
                        if(ordersFor.equals("saleman")) {
                            if (jsonObject.getInt("status") == 2)
                                Toast.makeText(context, "Order accepted successfully.", Toast.LENGTH_LONG).show();
                        }
                        //FOR SHOP-KEEPER
                        else {
                            if(jsonObject.getInt("status") == 2)
                                Toast.makeText(context, "Order Received Successfully", Toast.LENGTH_LONG).show();

                        }
                        notifyDataSetChanged();

                    }catch (JSONException e){e.printStackTrace();}
                }
                else if(response.has("failed")){
                    try {
                        String message = response.getString("failed");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e){e.printStackTrace();}
                }

            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Order Status Change Error: "+ error.getMessage());
            }
        };
        VolleyService volleyService = new VolleyService(iResult ,context);
        volleyService.postRequest(AppConfig.ORDER_STATUS_URL, "POST" , new JSONObject(params));
    }

    //TODO: SHOWING PROGRESS DIALOG
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    //TODO: HIDING PROGRESS DIALOG
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void setOrder(Order order){  this.order = order; }
    public Order getOrder(){ return  this.order; }
}
