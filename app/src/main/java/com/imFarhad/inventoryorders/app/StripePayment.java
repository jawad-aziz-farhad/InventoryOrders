package com.imFarhad.inventoryorders.app;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imFarhad.inventoryorders.R;
import com.stripe.android.Stripe;

import org.w3c.dom.Text;

/**
 * Created by Farhad on 11/10/2018.
 */

public class StripePayment {

    private Context context;
    private Dialog dialog;
    private EditText cardNum, cardCVV , cardExpiryMonth , cardExpiryYear;
    private TextView totalAmount, halfAmount;

    public StripePayment(Context context){
        this.context = context;
    }

    public void OpenDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.stripe);

        cardNum = (EditText)dialog.findViewById(R.id.cardNo);
        cardCVV = (EditText)dialog.findViewById(R.id.cvv);
        cardExpiryMonth = (EditText)dialog.findViewById(R.id.card_expiry_month);
        cardExpiryYear  = (EditText)dialog.findViewById(R.id.card_expiry_year);

        totalAmount = (TextView)dialog.findViewById(R.id.total_amount);
        halfAmount  = (TextView)dialog.findViewById(R.id.half_amount);

        TextView submit  =(TextView)dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateCardInfo();
            }
        });
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

        if(TextUtils.isEmpty(card_cvv))
            cardCVV.setError(context.getString(R.string.error_field_required));

        if(TextUtils.isEmpty(card_expiry_month))
            cardExpiryMonth.setError(context.getString(R.string.error_field_required));

        if(TextUtils.isEmpty(card_expiry_year))
            cardExpiryYear.setError(context.getString(R.string.error_field_required));

        else{


        }


    }


}
