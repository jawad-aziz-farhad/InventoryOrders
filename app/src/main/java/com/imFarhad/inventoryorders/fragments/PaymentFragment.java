package com.imFarhad.inventoryorders.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.SliderMenu;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.Preferences;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.models.Product;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 03/10/2018.
 */

public class PaymentFragment extends Fragment implements View.OnClickListener {

    EditText cardNum1, cardNum2, cardNum3, cardNum4, amount, customerName, customerEmail, customerAddress;
    Spinner expiryMonth, expiryYear;
    Button cancel, save;
    ProgressDialog progressDialog;
    ArrayList<Product> cartItems;
    private static final String TAG = PaymentFragment.class.getSimpleName();

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
        initView(view);

        cartItems = new ArrayList<>();
        if(getArguments() != null)
            cartItems = getArguments().getParcelableArrayList("cartItems");
        else
            Log.e(TAG, "NO CART ITEM FOUND.");
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return view;
    }

    private void initView(View view){
        cardNum1 = (EditText)view.findViewById(R.id.editStripeCardNumber1);
        cardNum2 = (EditText)view.findViewById(R.id.editStripeCardNumber2);
        cardNum3 = (EditText)view.findViewById(R.id.editStripeCardNumber3);
        cardNum4 = (EditText)view.findViewById(R.id.editStripeCardNumber4);

        customerName    = (EditText)view.findViewById(R.id.editCustName);
        customerEmail   = (EditText)view.findViewById(R.id.editCustEmail);
        customerAddress = (EditText)view.findViewById(R.id.editCustAddress);

        expiryMonth = (Spinner)view.findViewById(R.id.spinnerStripeExpMonth);
        expiryYear  = (Spinner)view.findViewById(R.id.spinnerStripeExpYear);

        cancel = (Button)view.findViewById(R.id.btnStripeCancel);
        save   = (Button)view.findViewById(R.id.btnStripeSave);

        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStripeSave:
              checkValues();
              break;
            case R.id.btnStripeCancel:

             break;
        }
    }


    private void checkValues() {

        String cardNum1Text = cardNum1.getText().toString();
        String cardNum2Text = cardNum2.getText().toString();
        String cardNum3Text = cardNum3.getText().toString();
        String cardNum4Text = cardNum4.getText().toString();

        String customername    = customerName.getText().toString();
        String customeremail   = customerEmail.getText().toString();
        String customeraddress = customerAddress.getText().toString();

        boolean cancel = false;

        if(TextUtils.isEmpty(cardNum1Text) && TextUtils.isEmpty(cardNum2Text) && TextUtils.isEmpty(cardNum3Text) && TextUtils.isEmpty(cardNum4Text)
           && TextUtils.isEmpty(customername) && TextUtils.isEmpty(customeremail) && TextUtils.isEmpty(customeraddress)){

           cardNum1.setError(getString(R.string.card_num1_error));
           cardNum2.setError(getString(R.string.card_num2_error));
           cardNum3.setError(getString(R.string.card_num3_error));
           cardNum4.setError(getString(R.string.card_num4_error));

           customerName.setError(getString(R.string.customer_name_error));
           customerEmail.setError(getString(R.string.customer_email_error));
           customerAddress.setError(getString(R.string.customer_address_error));

           return;
        }

        if(TextUtils.isEmpty(cardNum1Text)){
            cardNum1.setError(getString(R.string.card_num1_error));
            cardNum1.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(cardNum2Text)){
            cardNum2.setError(getString(R.string.card_num2_error));
            cardNum2.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(cardNum3Text)){
            cardNum3.setError(getString(R.string.card_num3_error));
            cardNum3.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(cardNum4Text)){
            cardNum4.setError(getString(R.string.card_num4_error));
            cardNum4.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(customername)){
            customerName.setError(getString(R.string.customer_name));
            customerName.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(customeremail)){
            customerEmail.setError(getString(R.string.customer_email));
            customerEmail.requestFocus();
            cancel = true;
        }

        if(TextUtils.isEmpty(customeraddress)){
            customerAddress.setError(getString(R.string.customer_address));
            customerAddress.requestFocus();
            cancel = true;
        }

        if(!cancel)
            placeOrder();
    }

    private void placeOrder(){
        if(!Connectivity.isConnected(getActivity()) && (!Connectivity.isConnectedMobile(getActivity()) || !Connectivity.isConnectedWifi(getActivity()))){
            Toast.makeText(getActivity(), R.string.internet_error_msg, Toast.LENGTH_LONG).show();
            return;
        }

        showDialog();
        JSONArray products = new JSONArray();
        for(int i=0; i<cartItems.size();i++) {

            JSONObject object = new JSONObject();
            Product product = cartItems.get(i);
            try {
                object.put("product_id", product.getId());
                object.put("quantity", product.getQuantity());
                object.put("unit_price", product.getPrice());
                products.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.w(TAG, String.valueOf(new SessionManager(getActivity()).getId()));
        JSONObject data = new JSONObject();
        try {
            data.put("user_id", new SessionManager(getActivity()).getId());
            data.put("products", products);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //CALLBACK FOR LOGIN RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                hideDialog();
                Log.w(TAG,"Order Placing Response: "+ response.toString());

                try {
                    if (response.has("success")) {
                        JSONObject success = response.getJSONObject("success");
                        Toast.makeText(getActivity(), success.getString("message"), Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
                }
                catch (JSONException e){ e.printStackTrace();}
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Order Placing Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                showError();
            }
        };
        Log.w(TAG + " PAYLOAD ", data.toString());
        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService.postRequest(AppConfig.ORDER_SUBMIT_URL, "POST" , data);
    }

    //TODO: SHOW ERROR IN TOAST
    private void showError(){
        Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
    }

    //TODO: SHOWING PROGRESS DIALOG
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    //TODO: HIDING PROGRESS DIALOG
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
