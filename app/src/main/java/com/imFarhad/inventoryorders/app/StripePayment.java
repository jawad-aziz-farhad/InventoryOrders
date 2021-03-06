package com.imFarhad.inventoryorders.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.fragments.OrdersFragment;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.models.Product;
import com.imFarhad.inventoryorders.services.VolleyService;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Farhad on 11/10/2018.
 */

public class StripePayment {

    private Context context;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private EditText cardNum, cardCVV , cardExpiryMonth , cardExpiryYear;
    private TextView totalAmount, halfAmount;
    private Stripe stripe;
    private ArrayList<Product> cartItems;
    private static final String TAG = StripePayment.class.getSimpleName();

    public StripePayment(Context context, ArrayList<Product> cartItems){
        this.context = context;
        progressDialog = new ProgressDialog(context);
        this.cartItems = cartItems;
    }

    public void OpenDialog() {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.stripe);

        cardNum         = (EditText)dialog.findViewById(R.id.cardNo);
        cardCVV         = (EditText)dialog.findViewById(R.id.cvv);
        cardExpiryMonth = (EditText)dialog.findViewById(R.id.card_expiry_month);
        cardExpiryYear  = (EditText)dialog.findViewById(R.id.card_expiry_year);

        totalAmount     = (TextView)dialog.findViewById(R.id.total_amount);
        halfAmount      = (TextView)dialog.findViewById(R.id.half_amount);
        int totalPrice  = 0;
        Iterator<Product> iterator = cartItems.iterator();
        do {
            Product product = iterator.next();
            Log.w(TAG, product.getTotalProductPrice());
            totalPrice += getPrice(product.getTotalProductPrice());
        }
        while (iterator.hasNext());

        totalAmount.setText(String.valueOf(totalPrice));
        totalAmount.append(" PKR");
        halfAmount.setText(String.valueOf(totalPrice / 2));
        halfAmount.append(" PKR");

        TextView submit = (TextView)dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCardInfo();
            }
        });

        dialog.show();
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


    private void validateCardInfo(){

        String card_num = cardNum.getText().toString();
        String card_cvv = cardCVV.getText().toString();
        String card_expiry_month = cardExpiryMonth.getText().toString();
        String card_expiry_year = cardExpiryYear.getText().toString();


        if(TextUtils.isEmpty(card_num) && TextUtils.isEmpty(card_cvv) && TextUtils.isEmpty(card_expiry_month) && TextUtils.isEmpty(card_expiry_year)){
            cardNum.setError(context.getString(R.string.error_field_required));
            cardCVV.setError(context.getString(R.string.error_field_required));
            cardExpiryMonth.setError(context.getString(R.string.error_field_required));
            cardExpiryYear.setError(context.getString(R.string.error_field_required));
            Toast.makeText(context, context.getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(card_num))
            cardNum.setError(context.getString(R.string.error_field_required));
        else if(card_num.length() < 16)
            cardNum.setError(context.getString(R.string.wrond_card_number));

        if(TextUtils.isEmpty(card_cvv))
            cardCVV.setError(context.getString(R.string.error_field_required));
        else if(card_cvv.length() < 4)
            cardCVV.setError(context.getString(R.string.wrond_card_cvv));

        if(TextUtils.isEmpty(card_expiry_month))
            cardExpiryMonth.setError(context.getString(R.string.error_field_required));
        else if(card_expiry_month.length() < 2)
            cardExpiryMonth.setError(context.getString(R.string.wrond_card_expiry_month));

        if(TextUtils.isEmpty(card_expiry_year))
            cardExpiryYear.setError(context.getString(R.string.error_field_required));
        else if(card_expiry_year.length() < 2)
            cardExpiryYear.setError(context.getString(R.string.wrond_card_expiry_year));

        else {
            Card card = new Card(
                    //card_num,
                    "4242424242424242",
                    Integer.valueOf(cardExpiryMonth.getText().toString()),
                    Integer.valueOf(cardExpiryYear.getText().toString()),
                    card_cvv
            );
            /*
            card.setCurrency("PKR");
            card.setName("[NAME_SURNAME]");
            card.setAddressZip("[ZIP]");
            */
            submitCard(card);

        }
    }

    //TODO: SUBMITTING CARD TO GET THE TOKEN (WE CAN USE THIS TOKEN TO SEND TO THE SERVER AND SERVER SIDE SCRIPT WILL USE THIS TOKEN TO DO THE TRANSACTION)
    private void submitCard(Card card){
        dialog.cancel();
        showDialog();
        stripe = new Stripe(context, AppConfig.STRIPE_KEY);
        stripe.createToken(card, AppConfig.STRIPE_KEY, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                hideDialog();
                Log.e(TAG, error.toString());
                Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_LONG).show();
               // hideDialog();
                placeOrder();
            }

            @Override
            public void onSuccess(Token token) {
                hideDialog();
                String _token = "TOKEN CREATED : " + token;
                Log.w(TAG, _token);
                Toast.makeText(context, context.getString(R.string.stripe_payment_done) + _token, Toast.LENGTH_LONG).show();
                placeOrder();
            }
        });
    }

    private void placeOrder(){
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

        Log.w(TAG, String.valueOf(new SessionManager(context).getId()));
        JSONObject data = new JSONObject();
        try {
            data.put("user_id", new SessionManager(context).getId());
            data.put("products", products);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //CALLBACK FOR ORDER RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                hideDialog();
                Log.w(TAG,"Order Placing Response: "+ response.toString());

                try {
                    JSONObject success = response.getJSONObject("sucess");
                    if (success != null && success.has("message")) {
                        String message = success.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        gotoHistory();
                    }else
                        Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_LONG).show();
                }
                catch (JSONException e){ e.printStackTrace();}
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Order Placing Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                Toast.makeText(context, context.getString(R.string.error_message),Toast.LENGTH_LONG).show();
            }
        };

        Log.w(TAG + " PAYLOAD ", data.toString());
        VolleyService volleyService = new VolleyService(iResult , context);
        volleyService.postRequest(AppConfig.ORDER_SUBMIT_URL, "POST" , data);
    }

    //TODO: GOING TO ORDERS HISTORY
    public void gotoHistory(){
        Fragment fragment = new OrdersFragment();
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        //fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    //TODO: SHOWING PROGRESS DIALOG
    public void showDialog() {
        progressDialog.setMessage(context.getString(R.string.loader_msg));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    //TODO: HIDING PROGRESS DIALOG
    public void hideDialog() {        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
