package com.example.shashankreddy.fooddoor.fragments;


import android.app.ProgressDialog;
import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.example.shashankreddy.fooddoor.utils.FoodCatageroyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private static final String FROMCITY = "fromCity";
    private String city;
    View returnView;
    private Toolbar mToolbar;
    boolean selected = true;
    ProgressDialog pd;
    public HomeFragment() {

    }

    public static HomeFragment newInstance(String fromCity){
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(FROMCITY, fromCity);
        fragment.setArguments(args);
        return  fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.city = getArguments().getString(FROMCITY);
        }
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("please wait");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        returnView = inflater.inflate(R.layout.fragment_home, container, false);
        mTabLayout = (TabLayout) returnView.findViewById(R.id.food_catatgeory_tab_layout);
        mViewPager = (ViewPager) returnView.findViewById(R.id.food_catageroy_view_pager);
        mToolbar = (Toolbar) returnView.findViewById(R.id.hometoolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        manageToolBar();
        getFoodList();
        return returnView;
    }

    private void manageToolBar() {
        TextView toAddress;
        View toAddressLayout;

        toAddress = (TextView) mToolbar.findViewById(R.id.toAddress);
        toAddressLayout = mToolbar.findViewById(R.id.toAddressLayout);
        Address address = AppController.getInstance().getmUserAddress().getDeliveryAddress();
        //toAddress.setText(address.getAddressLine(0));
        toAddress.setText("3089 il Ave");
        AppCompatSpinner selectedDeliveryCity = (AppCompatSpinner) mToolbar.findViewById(R.id.select_delivery_city);
        ArrayAdapter<CharSequence> foodcityAdapter
                =ArrayAdapter.createFromResource(getContext(), R.array.foodCities, android.R.layout.simple_spinner_dropdown_item);
        selectedDeliveryCity.setAdapter(foodcityAdapter);
        toAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(HomeFragment.class.getSimpleName(), "toAddress is clicked");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_replaceable, AddressMapFragment.newInstance("HomeFragment")).commit();
                fragmentTransaction.addToBackStack(null);
            }
        });

        selectedDeliveryCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("HomeFragment", parent.getItemAtPosition(position).toString() + "intial");
                if (!selected) {
                    Log.d("HomeFragment", parent.getItemAtPosition(position).toString());
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_replaceable, HomeFragment.newInstance(parent.getItemAtPosition(position).toString())).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("HomeFragment", "Delhi");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_replaceable, HomeFragment.newInstance("Delhi")).commit();
            }
        });
    }

    private void getFoodList(){
        show();
        if(city==null)
            city ="delhi";
        else if(city.equalsIgnoreCase("city")|| city.equals(""))
            city = "delhi";

        String foodListURL="http://rjtmobile.com/ansari/fos/fosapp/fos_food_loc.php?city="+city;
        final ArrayList<FoodItemList> foodItemLists = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, foodListURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray foodList = response.getJSONArray("Food");
                    for(int i=0;i<foodList.length();i++){
                        JSONObject foodItem = foodList.getJSONObject(i);
                        String foodId = foodItem.getString("FoodId");
                        String foodName = foodItem.getString("FoodName");
                        String foodRecepiee = foodItem.getString("FoodRecepiee");
                        String foodPrice = foodItem.getString("FoodPrice");
                        String foodCat = foodItem.getString("FoodCategory");
                        String foodImage = foodItem.getString("FoodThumb");
                        FoodItemList foodItemList = new FoodItemList(foodId,foodName,foodRecepiee,foodPrice,foodCat,foodImage);
                        foodItemLists.add(foodItemList);
                    }

                    AppController.getInstance().getmFoodItemList().setmFoodItemLists(foodItemLists);
                    mTabLayout.addTab(mTabLayout.newTab().setText("Veg Food"));
                    mTabLayout.addTab(mTabLayout.newTab().setText("Non Veg Food"));
                    FoodCatageroyAdapter foodCatageroyAdapter = new FoodCatageroyAdapter(getChildFragmentManager(),mTabLayout.getTabCount());
                    mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    mTabLayout.setupWithViewPager(mViewPager);
                    mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                    mViewPager.setAdapter(foodCatageroyAdapter);
                    selected= false;
                    stop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    stop();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stop();
            }
        });
        Log.d(HomeFragment.class.getSimpleName(), jsonObjectRequest.getUrl());
        AppController.getInstance().addRequestQueue(jsonObjectRequest);
    }

    void show(){
        if(!pd.isShowing())
            pd.show();
    }
    void stop(){
        if(pd.isShowing())
            pd.dismiss();
    }
}
