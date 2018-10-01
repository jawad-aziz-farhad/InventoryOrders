package com.imFarhad.inventoryorders.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.imFarhad.inventoryorders.R;

/**
 * Created by Farhad on 17/09/2018.
 */

public class BadgeCount {

    public BadgeCount(){}

    public static void setBadgeCount(Context context, LayerDrawable layerDrawable, String count){

        BadgeDrawable badgeDrawable;

        Drawable drawable = layerDrawable.findDrawableByLayerId(R.id.notificationBadge);

        if(drawable != null && drawable instanceof BadgeDrawable)
            badgeDrawable = (BadgeDrawable)drawable;
        else
            badgeDrawable = new BadgeDrawable(context);

        badgeDrawable.setCount(count);
        layerDrawable.mutate();
        layerDrawable.setDrawableByLayerId(R.id.notificationBadge, badgeDrawable);
    }

}
