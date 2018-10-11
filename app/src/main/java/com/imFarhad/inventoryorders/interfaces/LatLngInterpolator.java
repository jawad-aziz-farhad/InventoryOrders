package com.imFarhad.inventoryorders.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Farhad on 11/10/2018.
 */

public interface LatLngInterpolator {
   LatLng interpolate(float fraction, LatLng a, LatLng b);

    public class LinearFixed implements LatLngInterpolator {

        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {

            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lngDelta = b.longitude - a.longitude;
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
}
