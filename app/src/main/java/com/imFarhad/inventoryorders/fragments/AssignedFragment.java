package com.imFarhad.inventoryorders.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces._IResult;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;

/**
 * Created by Farhad on 12/11/2018.
 */

public class AssignedFragment extends Fragment {

    private static final String TAG = AssignedFragment.class.getSimpleName();
    private Button OrderStatus;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assigned_deliveries, container, false);
        OrderStatus = (Button)view.findViewById(R.id.order_status);
        OrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String text = ((Button)view).getText().toString();
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        getOrders();

        return view;
    }

    //TODO: GETTING ASSIGNED ORDERS TO THE SALE MAN
    private void getOrders(){
        showDialog();
        //CALLBACK FOR ORDERS RESPONSE
        _IResult iResult = new _IResult() {
            @Override
            public void onSuccess(String requestType, JSONArray response) {
                hideDialog();
                Log.w(TAG, "ASSIGNED ORDERS "+ response.toString());
                if(response.length() == 0){
                    Toast.makeText(getActivity(), "No Order Found.",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Assigned Orders Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_message),Toast.LENGTH_LONG).show();
            }
        };

        int user_id = new SessionManager(getActivity()).getId();
        String uri = AppConfig.SALEMAN_ORDERS_URL + user_id;
        Log.w(TAG, uri);
        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService._getRequest(uri , "GET");
    }

    //TODO: SHOWING PROGRESS DIALOG
    public void showDialog() {
        progressDialog.setMessage(getActivity().getString(R.string.loader_msg));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    //TODO: HIDING PROGRESS DIALOG
    public void hideDialog() {        if (progressDialog.isShowing())
        progressDialog.dismiss();
    }

}
