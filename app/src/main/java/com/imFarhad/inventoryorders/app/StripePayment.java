package com.imFarhad.inventoryorders.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imFarhad.inventoryorders.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

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
    private static final String TAG = StripePayment.class.getSimpleName();

    public StripePayment(Context context){
        this.context = context;
        progressDialog = new ProgressDialog(context);
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

        TextView submit = (TextView)dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCardInfo();
            }
        });

        dialog.show();
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
                    card_num,
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
            }

            @Override
            public void onSuccess(Token token) {
                hideDialog();
                String _token = "TOKEN CREATED : " + token;
                Log.w(TAG, _token);
                Toast.makeText(context, _token, Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO: SHOWING PROGRESS DIALOG
    public void showDialog() {
        progressDialog.setMessage(context.getString(R.string.loader_msg));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    //TODO: HIDING PROGRESS DIALOG
    public void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
