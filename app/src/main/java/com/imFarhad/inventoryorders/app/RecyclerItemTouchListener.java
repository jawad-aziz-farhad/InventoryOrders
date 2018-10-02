package com.imFarhad.inventoryorders.app;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.imFarhad.inventoryorders.adapters.CartItemsAdapter;
import com.imFarhad.inventoryorders.interfaces.ItemTouchListener;

public class RecyclerItemTouchListener extends ItemTouchHelper.SimpleCallback {

    public ItemTouchListener itemTouchListener;

    public RecyclerItemTouchListener(int dragDirs, int swipeDirs, ItemTouchListener itemTouchListener) {
        super(dragDirs, swipeDirs);

        this.itemTouchListener = itemTouchListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
       return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        itemTouchListener.onSwiped(viewHolder,i, viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(viewHolder != null){
            final View forGroundView = ((CartItemsAdapter.ViewHolder)viewHolder).foreGroundLayout;
            getDefaultUIUtil().onSelected(forGroundView);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundLayout = ((CartItemsAdapter.ViewHolder)viewHolder).foreGroundLayout;
        getDefaultUIUtil().onDraw(c, recyclerView, foreGroundLayout, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        final View forGroundView = ((CartItemsAdapter.ViewHolder)viewHolder).foreGroundLayout;
        getDefaultUIUtil().onDrawOver(c ,recyclerView, forGroundView, dX, dY, actionState, isCurrentlyActive );
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View forGroundView = ((CartItemsAdapter.ViewHolder)viewHolder).foreGroundLayout;
        getDefaultUIUtil().clearView(forGroundView);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

}
