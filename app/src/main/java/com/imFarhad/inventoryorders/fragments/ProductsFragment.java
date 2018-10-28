package com.imFarhad.inventoryorders.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.adapters.ProductsAdapter;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.AppController;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces.ProductItemClickListener;
import com.imFarhad.inventoryorders.models.Product;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Farhad on 17/09/2018.
 */

public class ProductsFragment extends Fragment {

    private static final String TAG = ProductsFragment.class.getSimpleName();
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ProductsAdapter productsAdapter;
    private ArrayList<Product> products;
    private FloatingActionButton cartBtn;
    private TextView totalItems;
    private ArrayList<Product> cartItems;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showCartButton();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container,false);
        view.findViewById(R.id.checkOutbtn).setVisibility(View.GONE);
        ProductItemClickListener listener = new ProductItemClickListener() {
            @Override
            public void OnItemClick(Product product) {
            }
            @Override
            public void OnAddToCartClick(Product product) {
                Log.w(TAG, "ADDED PRODUCT:  "+ new Gson().toJson(product));
                cartBtn.setEnabled(true);
                cartItems.add(product);
                totalItems.setText(String.valueOf(cartItems.size()));
            }

            @Override
            public void OnRemoveFromCartClick(Product product) {
                cartItems.remove(cartItems.indexOf(product));
                totalItems.setText(String.valueOf(cartItems.size()));
            }
        };

        if (savedInstanceState == null){
            Log.w(TAG, "No Value in SavedInstance State.");
            products  = new ArrayList<>();
            cartItems = new ArrayList<>();

        }else {
            Log.e(TAG, savedInstanceState.toString());
            products = savedInstanceState.getParcelableArrayList("products");
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(getActivity());
        productsAdapter = new ProductsAdapter(getActivity(), this.products, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(AppController.getDividerItemDecoration());
        recyclerView.setAdapter(productsAdapter);

        if (savedInstanceState == null)
            pullServerData();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w(TAG, "Saving Products.");
        outState.putParcelableArrayList("products", products);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
    //TODO: PULLING PRODUCTS FROM SERVER
    private void pullServerData(){
        showDialog();
        //CALLBACK FOR TOKEN VALIDATION RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.w(TAG, "PRODUCTS RESPONSE "+ response);
                populateProducts(response);
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "PRODUCTS ERROR " + error.toString());
            }
        };
        String url = AppConfig.PRODUCTS_URL + getArguments().getInt("id");
        VolleyService volleyService = new VolleyService(iResult, getActivity());
        volleyService.getRequest(url, "GET" , null);
    }

    //TODO:  EXTRACTING PRODUCTS FROM JSON ARRAY SENT BY THE SERVER AND POPULATING DATA
    private void populateProducts(JSONObject response){
        try {
            JSONArray products = response.getJSONArray("products");
            if(products.length() > 0){
                for(int i=0; i<products.length(); i++){
                    JSONObject object = products.getJSONObject(i);
                    Gson gson = new Gson();
                    Product product = gson.fromJson(object.toString(), Product.class);
                    Log.w(TAG, product.getName() + " " + product.getPrice());
                    this.products.add(product);
                }
            }
            else
                Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
        }
        catch (JSONException e){ e.printStackTrace(); }

        hideDialog();
        productsAdapter.notifyDataSetChanged();
    }

    //TODO: SHOWING PROGRESS DIALOG
    public void showDialog() {
        progressDialog.setMessage(getString(R.string.loader_msg));
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    //TODO: HIDING PROGRESS DIALOG
    public void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    @Override
    public void onResume() {
        super.onResume();
        showCartButton();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //TODO: SHOWING CART FLOATING ACTIN BUTTON
    private void showCartButton(){
        cartBtn = (FloatingActionButton)getActivity().findViewById(R.id.fab_cart);
        totalItems = (TextView)getActivity().findViewById(R.id.cart_items_count);
        RelativeLayout relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.cart_wrapper_layout);
        relativeLayout.setVisibility(View.VISIBLE);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(cartItems.size() > 0){
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("cartItems", cartItems);
                Fragment fragment = new CartItemsFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
            }
        });

    }
}
