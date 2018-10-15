package com.imFarhad.inventoryorders.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.SessionManager;

/**
 * Created by Farhad on 17/09/2018.
 */

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail;
    private ImageView userImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        userName = (TextView)view.findViewById(R.id.profile_name);
        userEmail= (TextView)view.findViewById(R.id.profile_email);
        userImage= (ImageView) view.findViewById(R.id.profile_image);

        SessionManager sessionManager = new SessionManager(getActivity());
        userName.setText(sessionManager.getName());
        userEmail.setText(sessionManager.getEmail());

        return view;
    }
}
