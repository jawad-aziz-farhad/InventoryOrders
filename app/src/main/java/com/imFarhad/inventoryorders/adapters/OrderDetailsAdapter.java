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
import com.imFarhad.inventoryorders.models.OrderDetails;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderDetails> orders;

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetails> orders){
        this.context = context;
        this.orders  = orders;
    }
    @NonNull
    @Override
    public OrderDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order, viewGroup, false);
        return new OrderDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.ViewHolder viewHolder, int i) {
        OrderDetails orderDetails = orders.get(i);
        viewHolder.bind(orderDetails);
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
        private TextView productQuantity;
        public static final String TAG = ProductsAdapter.class.getSimpleName();

        public ViewHolder(final View itemView) {
            super(itemView);
            productName        = (TextView) itemView.findViewById(R.id.productName);
            productUnitPrice   = (TextView) itemView.findViewById(R.id.productUnitPrice);
            productTotalPrice  = (TextView) itemView.findViewById(R.id.productSubTotal);
            productDescription = (TextView) itemView.findViewById(R.id.productDescription);
            productImage       = (ImageView)itemView.findViewById(R.id.productImage);
            productQuantity    = (TextView) itemView.findViewById(R.id.productQuantity);
        }

        public void bind(final OrderDetails orderDetails) {
            productName.setText("Product Name");
            String currency = "  " + itemView.getContext().getString(R.string.currency);
            productUnitPrice.append(String.valueOf(orderDetails.getUnit_price()));
            productTotalPrice.append(String.valueOf(orderDetails.getAmount()));
            productQuantity.append(String.valueOf(orderDetails.getQuantity()));

            productUnitPrice.append(currency);
            productTotalPrice.append(currency);
            productDescription.setText("");
        }
    }
}
