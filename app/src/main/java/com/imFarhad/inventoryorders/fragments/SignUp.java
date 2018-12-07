package com.imFarhad.inventoryorders.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.LoginActivity;
import com.imFarhad.inventoryorders.activities.SliderMenu;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 17/09/2018.
 */

public class SignUp extends Fragment {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView;
    private TextView mErrorView;
    private ImageView mImageView;

    private SessionManager sessionManager;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private RadioGroup mUserTypeGroup;
    private String userType = "shopkeeper";
    private static final int IMAGE_PICKER_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.signup, container, false);
        //TODO: Set up the login form.
        sessionManager = new SessionManager(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        mErrorView     = (TextView)view.findViewById(R.id.errorView);
        mUserTypeGroup = (RadioGroup)view.findViewById(R.id.login_userType);
        mFirstNameView      = (EditText)view.findViewById(R.id.first_name);
        mLastNameView      = (EditText)view.findViewById(R.id.last_name);
        mEmailView     = (AutoCompleteTextView)view. findViewById(R.id.email);
        mPasswordView  = (EditText)view. findViewById(R.id.password);
        mImageView     = (ImageView)view.findViewById(R.id.signUpImage);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(), IMAGE_PICKER_CODE);
            }
        });

        Button mSignUpButton = (Button)view. findViewById(R.id.email_sign_in_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        mUserTypeGroup = (RadioGroup)view.findViewById(R.id.userType);
        mUserTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                userType = checkedRadioButton.getText().toString();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER_CODE){

        }
    }

    //TODO: CHECKING FOR INTERNET CONNECTION
    private void isInternetAvailable(){
        if(Connectivity.isConnected(getActivity()) && (Connectivity.isConnectedMobile(getActivity()) || Connectivity.isConnectedWifi(getActivity())))
            createAccount();
        else
            Toast.makeText(getActivity(), getString(R.string.internet_error_msg),Toast.LENGTH_LONG).show();
    }


    //TODO: Creating New Account
    public void createAccount() {

        progressDialog.setMessage(getString(R.string.loader_msg));
        showDialog();

        Map<String, String> params = new HashMap<String, String>();
        String first_name  = mFirstNameView.getText().toString();
        String last_name   = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(userType.toLowerCase().equals("saleman")) {
            params.put("firstName", first_name);
            params.put("lastName", last_name);
            params.put("phone", "0123456");
            params.put("image", "");
        }
        else
            params.put("name", first_name + " " + last_name);

        params.put("email", email);
        params.put("password", password);

        //CALLBACK FOR SIGNUP RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.d(TAG,"Response: "+ response.toString());
                try {

                    if(response.has("failed"))
                        Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
                    else {

                        if (response.has("saleman")) {
                            response.put("type", "saleman");
                            sessionManager.setUpUser(response.getJSONObject("saleman"));
                        }
                        else {
                            response.put("type", "shopkeeper");
                            sessionManager.setUpUser(response);
                        }
                    }
                }
                catch (JSONException e){e.printStackTrace();}
                hideDialog();
                startActivity(new Intent(getActivity(), SliderMenu.class));
                getActivity().finish();
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Sign Up Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                mErrorView.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mErrorView.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        };
        String SignUpUrl = userType.toLowerCase().equals("shopkeeper") ? AppConfig.SIGNUP_URL : AppConfig.SALEMAN_SIGNUP_URL;
        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService.postRequest(SignUpUrl, "POST" , new JSONObject(params));

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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //TODO: Store values at the time of the login attempt.
        String first_name  = mFirstNameView.getText().toString();
        String last_name   = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(first_name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            mEmailView.setError(getString(R.string.error_field_required));
            mPasswordView.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(last_name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            mEmailView.setError(getString(R.string.error_field_required));
            mPasswordView.setError(getString(R.string.error_field_required));
            return;
        }

        //TODO: Check for a Name.
        if (TextUtils.isEmpty(first_name)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(last_name)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        //TODO: Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //TODO: Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //TODO: There was an error; don't attempt login and focus the first
        //TODO: form field with an error.
        if (cancel)
            focusView.requestFocus();
        else
            isInternetAvailable();

    }

    //TODO: CHECKING ENTERED EMAIL FOR VALIDATION
    private boolean isEmailValid(String email) { return email.contains("@"); }
    //TODO: VALIDATING PASSWORD'S LENGTH
    private boolean isPasswordValid(String password) { return password.length() > 4; }
}
