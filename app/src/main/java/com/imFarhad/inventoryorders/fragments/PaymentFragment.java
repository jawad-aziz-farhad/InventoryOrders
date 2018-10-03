package com.imFarhad.inventoryorders.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.imFarhad.inventoryorders.R;

/**
 * Created by Farhad on 03/10/2018.
 */

public class PaymentFragment extends Fragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            /* HIDING FLOATING ACTION BUTTON ON THIS FRAGMENT */
            RelativeLayout relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.cart_wrapper_layout);
            relativeLayout.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment, container, false);
        return view;
    }
}
