package com.imFarhad.inventoryorders.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.interfaces.ProductItemClickListener;
import com.imFarhad.inventoryorders.models.Product;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.awt.font.TextAttribute;
import java.util.List;

import rx.subjects.BehaviorSubject;

/**
 * Created by Farhad on 17/09/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final ProductItemClickListener listener;
    public Context mContext;
    public List<Product> products;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productName;
        private TextView productUnitPrice;
        private TextView productTotalPrice;
        private TextView productDescription;
        private ImageView productImage;
        private FloatingActionButton addtoCartBtn;
        private FloatingActionButton removeCartBtn;
        private ElegantNumberButton quantityBtn;
        private LinearLayout addToCartLayout, removeFromCartLayout;

        public static final String TAG = ProductsAdapter.class.getSimpleName();

        public ViewHolder(final View itemView) {
            super(itemView);
            productName        = (TextView) itemView.findViewById(R.id.productName);
            productUnitPrice   = (TextView) itemView.findViewById(R.id.productUnitPrice);
            productTotalPrice  = (TextView) itemView.findViewById(R.id.productSubTotal);
            productDescription = (TextView)itemView.findViewById(R.id.productDescription);
            productImage       = (ImageView) itemView.findViewById(R.id.productImage);
            quantityBtn        = (ElegantNumberButton)itemView.findViewById(R.id.productQuantity);
            addToCartLayout    = (LinearLayout)itemView.findViewById(R.id.add_cart_btn_layout);
            removeFromCartLayout=(LinearLayout)itemView.findViewById(R.id.remove_cart_btn_layout);
            addtoCartBtn       = (FloatingActionButton)itemView.findViewById(R.id.add_cart_btn);
            removeCartBtn      = (FloatingActionButton)itemView.findViewById(R.id.remove_cart_btn);

        }

        public void bind(final Product product, final ProductItemClickListener listener) {
            productName.setText(product.getName());
            String currency = "  " + itemView.getContext().getString(R.string.currency);
            productUnitPrice.setText(product.getPrice());
            productUnitPrice.append(currency);
            int totalPrice = Integer.parseInt(product.getPrice()) * Integer.parseInt(quantityBtn.getNumber());
            productTotalPrice.setText(String.valueOf(totalPrice));
            productTotalPrice.append(currency);
            productDescription.setText(product.getDescription());

//            String base64Image = product.getImage().split(",")[1];
//            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            productImage.setImageBitmap(decodedByte);
        }
    }

    public ProductsAdapter(Context mContext, List<Product> products, ProductItemClickListener listener) {
        this.mContext = mContext;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product, viewGroup, false);
        return new ProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsAdapter.ViewHolder viewHolder, int i) {

        final Product product = products.get(i);
        viewHolder.bind(product, listener);

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           listener.OnItemClick(product);
            }
        });

        viewHolder.addtoCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(viewHolder.quantityBtn.getNumber().equals("0")){
                Toast.makeText(mContext, mContext.getString(R.string.quantity_selection_error), Toast.LENGTH_LONG).show();
                return;
            }
            viewHolder.removeFromCartLayout.setVisibility(View.VISIBLE);
            viewHolder.addToCartLayout.setVisibility(View.GONE);
            Product _product = getEditedProduct(
                    product,
                    Integer.parseInt(viewHolder.quantityBtn.getNumber()),
                    viewHolder.productTotalPrice.getText().toString());
            listener.OnAddToCartClick(_product);
            }
        });

        viewHolder.removeCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            viewHolder.removeFromCartLayout.setVisibility(View.GONE);
            viewHolder.addToCartLayout.setVisibility(View.VISIBLE);
            viewHolder.quantityBtn.setNumber("0");
            viewHolder.productTotalPrice.setText("0 ");
            viewHolder.productTotalPrice.append(mContext.getString(R.string.currency));
            listener.OnRemoveFromCartClick(product);
            }
        });

        viewHolder.quantityBtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                String currency = " " + mContext.getString(R.string.currency);
                String btnValue  = view.getNumber();

                if(Integer.parseInt(btnValue) > 0) {
                    int unitPrice = getPrice(viewHolder.productUnitPrice.getText().toString());
                    int totalPrice = unitPrice * Integer.parseInt(btnValue);
                    viewHolder.productTotalPrice.setText(String.valueOf(totalPrice));
                    viewHolder.productTotalPrice.append(currency);
                }
                else {
                    viewHolder.productTotalPrice.setText("0");
                    viewHolder.productTotalPrice.append(currency);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    //TODO: GETTING PRICE VALUE
    private int getPrice(String price){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<price.length();i++){
            char currentChar = price.charAt(i);
            if(Character.isDigit(currentChar))
                builder.append(currentChar);
        }
        return Integer.parseInt(builder.toString());
    }

    //TODO: EDITING PRODUCT TO ADD QUANTITY AND TOTAL PRICE FOR A SINGLE CART ITEM
    private Product getEditedProduct(Product product, int qauntity, String totalPrice){
        product.setCat_id(product.getCat_id());
        product.setDescription(product.getDescription());
        product.setImage(product.getDescription());
        product.setId(product.getId());
        product.setQuantity(qauntity);
        product.setTotalProductPrice(totalPrice);

        return product;
    }


}