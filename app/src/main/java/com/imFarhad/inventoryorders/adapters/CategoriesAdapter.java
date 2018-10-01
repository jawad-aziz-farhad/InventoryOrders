package com.imFarhad.inventoryorders.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.interfaces.ItemClickListener;
import com.imFarhad.inventoryorders.models.Category;

import java.util.List;

/**
 * Created by Farhad on 17/09/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final ItemClickListener listener;
    public Context mContext;
    public List<Category> categories;

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryTitle;
        public TextView categoryProductsCount;
        public ImageView categoryImage;
        public CardView cardView;

        public ViewHolder(final View itemView){
            super(itemView);
            cardView      = (CardView) itemView.findViewById(R.id.category_card_view);
            categoryTitle = (TextView) itemView .findViewById(R.id.category_title);
            categoryProductsCount = (TextView) itemView.findViewById(R.id.category_products_count);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
        }

        public void bind(final Category category, final ItemClickListener listener){
            categoryTitle.setText(category.getName());
            categoryProductsCount.setText(String.valueOf(100));
            categoryImage.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_remove_shopping_cart_black_24dp));
        }
    }

    public CategoriesAdapter(Context mContext, List<Category> categories, ItemClickListener listener){
        this.mContext = mContext;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Category category = categories.get(i);
        viewHolder.bind(category, listener);
        viewHolder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCategoryItemClickListener(category);
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCategoryItemClickListener(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
