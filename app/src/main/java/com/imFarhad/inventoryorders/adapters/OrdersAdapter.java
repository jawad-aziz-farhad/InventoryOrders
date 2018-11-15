package com.imFarhad.inventoryorders.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.models.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public Context context;
    private ArrayList<Order> orders;
    private OrderItemClickListener orderItemClickListener;
    public Order order = null;
    public String ordersFor = null;

    public OrdersAdapter(Context context, ArrayList<Order> orders, OrderItemClickListener orderItemClickListener, String ordersFor){
        this.context = context;
        this.orders  = orders;
        this.ordersFor = ordersFor;
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

        if(ordersFor.equals("saleman")){

            viewHolder.overFlow.setVisibility(View.GONE);
            viewHolder.statusBtn.setVisibility(View.VISIBLE);

            if(order.getStatus() == 1)
                viewHolder.statusBtn.setText(context.getString(R.string.accept_status));
            else if(order.getStatus() == 2)
                viewHolder.statusBtn.setText(context.getString(R.string.delivered_status));
            else if(order.getStatus() == 3 || order.getStatus() == 4 || order.getStatus() == 5){
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green));
                viewHolder.statusBtn.setVisibility(View.GONE);
            }
        }
        else {
            viewHolder.overFlow.setVisibility(View.VISIBLE);
            viewHolder.statusBtn.setVisibility(View.GONE);
            if(order.getStatus() == 4 || order.getStatus() == 5){
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.green));
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
            setOrder(order);
            showPopupMenu(view);
            }
        });

        viewHolder.statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderItemClickListener.OnItemClick(order);
            }
        });
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
        private Button statusBtn;

        public final String TAG = ProductsAdapter.class.getSimpleName();

        public ViewHolder(final View itemView) {
            super(itemView);
            orderName        = (TextView) itemView.findViewById(R.id.orderName);
            orderPaidAmount  = (TextView) itemView.findViewById(R.id.orderPaidAmount);
            orderTotalAmount = (TextView) itemView.findViewById(R.id.orderTotalAmount);
            overFlow         = (ImageView) itemView.findViewById(R.id.overflow);
            statusBtn        = (Button) itemView.findViewById(R.id.order_status);
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


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.slider_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(this.orderItemClickListener));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private OrderItemClickListener orderItemClickListener;
        public MyMenuItemClickListener(OrderItemClickListener orderItemClickListener) {
            this.orderItemClickListener = orderItemClickListener;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
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

    public void setOrder(Order order){  this.order = order; }
    public Order getOrder(){ return  this.order; }
}
