package com.example.shashankreddy.fooddoor.appController;

import android.app.Application;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.shashankreddy.fooddoor.models.CartItems;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.example.shashankreddy.fooddoor.models.UserAddress;
import com.example.shashankreddy.fooddoor.models.UserInfo;


public class AppController extends Application{
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mAppController;
    private RequestQueue mRequestQueue;
    private static UserInfo mUserInfo;
    private static UserAddress mUserAddress;
    private static FoodItemList mFoodItemList;
    private static CartItems mCartItems;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppController =this;
    }

    public static synchronized AppController getInstance(){
        return mAppController;
    }

    public  RequestQueue getmRequestQueue(){
        if(mRequestQueue == null)
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    public <T> void addRequestQueue(Request<T> request){
        request.setTag(TAG);
        getmRequestQueue().add(request);
    }

    public void cancelRequestQueue(Object tag){
        if(mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }

    public UserInfo getmUserInfo(){
        if(mUserInfo == null)
            mUserInfo = new UserInfo();
        return mUserInfo;
    }

    public UserAddress getmUserAddress(){
        if(mUserAddress == null)
            mUserAddress = new UserAddress();
        return mUserAddress;
    }

    public  FoodItemList getmFoodItemList() {
        if(mFoodItemList == null)
            mFoodItemList = new FoodItemList();
        return mFoodItemList;
    }

    public CartItems getmCartItems(){
        if(mCartItems == null)
            mCartItems = new CartItems();
        return mCartItems;
    }
}
