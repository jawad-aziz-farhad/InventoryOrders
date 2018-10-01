package com.imFarhad.inventoryorders.interfaces;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
