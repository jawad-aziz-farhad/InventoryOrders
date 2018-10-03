package com.imFarhad.inventoryorders.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.imFarhad.inventoryorders.R;
import com.imFarhad.inventoryorders.adapters.CategoriesAdapter;
import com.imFarhad.inventoryorders.app.AppConfig;
import com.imFarhad.inventoryorders.app.GridSpacingItemDecoration;
import com.imFarhad.inventoryorders.interfaces.IResult;
import com.imFarhad.inventoryorders.interfaces.ItemClickListener;
import com.imFarhad.inventoryorders.models.Category;
import com.imFarhad.inventoryorders.services.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farhad on 27/09/2018.
 */

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories;
    private ProgressDialog progressDialog;
    private Button checkOutBtn;

    private static final String TAG = CategoriesFragment.class.getSimpleName();

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

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        FloatingActionButton floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.fab_cart);
        view.findViewById(R.id.checkOutbtn).setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getActivity());
        categories = new ArrayList<>();

        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onCategoryItemClickListener(Category category) {
            Fragment fragment = new ProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", category.getId());
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        };

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        categoriesAdapter = new CategoriesAdapter(getActivity(), categories, listener);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPix(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoriesAdapter);

        pullServerData();

        return view;
    }

    //TODO: PULLING ALL CATEGORIES FROM SERVER
    private void pullServerData(){
        showDialog();
        //CALLBACK FOR TOKEN VALIDATION RESPONSE
        IResult iResult = new IResult() {
            @Override
            public void onSuccess(String requestType, JSONObject response) {
                Log.w(TAG, "CATEGORIES RESPONSE "+ response);
                populateCategories(response);
            }

            @Override
            public void onError(String requestType, VolleyError error) {
                hideDialog();
                Log.e(TAG, "CATEGORIES ERROR " + error.toString());
            }
        };
        VolleyService volleyService = new VolleyService(iResult, getActivity());
        volleyService.getRequest(AppConfig.CATEGORIES_URL, "GET" , null);
    }

     //TODO: EXTRACTING CATEGORIES DATA FROM SERVER RESPONSE
    private void populateCategories(JSONObject response){
        try{
            JSONArray allCategories = response.getJSONArray("categories");
            for(int i=0; i<allCategories.length();i++){
                JSONObject categoryObj = allCategories.getJSONObject(i);
                Gson gSon = new Gson();
                Category category = gSon.fromJson(categoryObj.toString(), Category.class);
                categories.add(category);
            }
        } catch (JSONException e){ e.printStackTrace();}

        hideDialog();
        categoriesAdapter.notifyDataSetChanged();
    }

    //TODO: CONVERTING DENSITY PIXELS TO PIXELS
    private int dpToPix(int dp){
        Resources resources = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
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
    }


}
