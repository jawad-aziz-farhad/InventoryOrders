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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.activities.LoginActivity;
import com.imFarhad.inventoryorders.activities.SliderMenu;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.Connectivity;
import com.imFarhad.inventoryorders.app.NotificationUtils;
import com.imFarhad.inventoryorders.app.Preferences;
import com.imFarhad.inventoryorders.app.SessionManager;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhad on 17/09/2018.
 */

public class Login extends Fragment {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mErrorView;
    private SessionManager sessionManager;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private TextView forgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login, container, false);
        //TODO: Set up the login form.
        sessionManager = new SessionManager(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        mErrorView = (TextView)view.findViewById(R.id.loginError);
        mEmailView = (AutoCompleteTextView)view. findViewById(R.id.email);

        mPasswordView = (EditText)view. findViewById(R.id.password);

        Button mEmailSignInButton = (Button)view. findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        forgotPassword = (TextView)view.findViewById(R.id.create_account_textView);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignUp(), "RESET_PASSWORD_FRAGMENT").addToBackStack(null).commit();
            }
        });

        return view;
    }

    //TODO: CHECKING FOR INTERNET CONNECTION
    private void isInternetAvailable(){
        if(Connectivity.isConnected(getActivity()) && (Connectivity.isConnectedMobile(getActivity()) || Connectivity.isConnectedWifi(getActivity())))
            login();
        else
            Toast.makeText(getActivity(), getString(R.string.internet_error_msg),Toast.LENGTH_LONG).show();
    }



    //TODO: LOGGIN IN TO THE APP
    public void login() {
        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        params.put("email", email);
        params.put("password", password);

        //CALLBACK FOR LOGIN RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.d(TAG,"Response: "+ response.toString());

                try {
                    hideDialog();
                    if(response.has("user") ) {
                        JSONArray user = response.getJSONArray("user");
                        sessionManager.setUpUser(user.getJSONObject(0));
                        //NotificationUtils notificationUtils = new NotificationUtils(getActivity());
                        //notificationUtils.sendTokenToServer(new Preferences(getActivity()).getFCMToken());
                        startActivity(new Intent(getActivity(), SliderMenu.class));
                        getActivity().finish();
                    }
                    else{
                        JSONObject error = response.getJSONObject("error");
                        Log.e(TAG, "Login Error: "+ error.getString("message"));
                        showLoginError();
                    }
                }
                catch (JSONException e){ e.printStackTrace();}
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "Login Error: "+ error.getMessage() + "\n" + error.getStackTrace());
                showLoginError();
            }
        };

        VolleyService volleyService = new VolleyService(iResult , getActivity());
        volleyService.postRequest(AppConfig.LOGIN_URL, "POST" , new JSONObject(params));
    }

    //TODO: SHOWING LOGIN ERROR
    private void showLoginError(){
        mEmailView.setError(getString(R.string.login_error));
        mPasswordView.setError(getString(R.string.login_error));
        mErrorView.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mErrorView.setVisibility(View.GONE);
            }
        }, 3000);
    }

    //TODO: SHOWING PROGRESS DIALOG
    private void showDialog() {
        progressDialog.setMessage(getString(R.string.loader_msg));
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
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //TODO: Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mPasswordView.setError(getString(R.string.error_field_required));
            return;
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
