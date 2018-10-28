package com.imFarhad.inventoryorders.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.interfaces.OrderItemClickListener;
import com.imFarhad.inventoryorders.models.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> orders;
    private OrderItemClickListener orderItemClickListener;

    public OrdersAdapter(Context context, ArrayList<Order> orders, OrderItemClickListener orderItemClickListener){
        this.context = context;
        this.orders  = orders;
        this.orderItemClickListener = orderItemClickListener;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order, viewGroup, false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder viewHolder, int i) {
        Order order = orders.get(i);
        viewHolder.bind(order, orderItemClickListener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productName;
        private TextView productUnitPrice;
        private TextView productTotalPrice;
        private TextView productDescription;
        private ImageView productImage;
        public static final String TAG = ProductsAdapter.class.getSimpleName();

        public ViewHolder(final View itemView) {
            super(itemView);
            productName        = (TextView) itemView.findViewById(R.id.productName);
            productUnitPrice   = (TextView) itemView.findViewById(R.id.productUnitPrice);
            productTotalPrice  = (TextView) itemView.findViewById(R.id.productSubTotal);
            productDescription = (TextView)itemView.findViewById(R.id.productDescription);
            productImage       = (ImageView) itemView.findViewById(R.id.productImage);
        }

        public void bind(final Order order, final OrderItemClickListener listener) {
            productName.setText("Order Num : ");
            String currency = "  " + itemView.getContext().getString(R.string.currency);
            productUnitPrice.setText(String.valueOf(order.getUnit_price()));
            productTotalPrice.setText(String.valueOf(order.getAmount()));
            productUnitPrice.append(currency);
            productTotalPrice.append(currency);
            productDescription.setText("");
        }
    }
}
