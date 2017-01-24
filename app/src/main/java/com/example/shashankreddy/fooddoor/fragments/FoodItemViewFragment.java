package com.example.shashankreddy.fooddoor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.adapters.FoodCatAdapter;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.CartItems;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.squareup.picasso.Picasso;

import java.security.ProtectionDomain;


public class FoodItemViewFragment extends Fragment {

    public static final String FOODID = "foodID";
    public static final String POSITION = "position";

    String mFoodID;
    int mPosition;
    FoodItemList mFoodItem;
    public FoodItemViewFragment() {
        // Required empty public constructor
    }

    public static FoodItemViewFragment newInstance(String productId,int position){
        FoodItemViewFragment fragment = new FoodItemViewFragment();
        Log.d(FoodCatAdapter.class.getSimpleName(), productId + " new instatance  " + position);
        Bundle args = new Bundle();
        args.putString(FOODID,productId);
        args.putInt(POSITION,position);
        fragment.setArguments(args);
        return  fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.mFoodID = getArguments().getString(FOODID);
            this.mPosition = getArguments().getInt(POSITION);
            mFoodItem = AppController.getInstance().getmFoodItemList().getFoodItem(mFoodID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView =inflater.inflate(R.layout.fragment_food_item_view, container, false);
        TextView foodItemTitle = (TextView) returnView.findViewById(R.id.food_item_view_title);
        TextView foodItemReciepee = (TextView) returnView.findViewById(R.id.food_item_view_reciepee);
        TextView foodItemPrice = (TextView) returnView.findViewById(R.id.food_item_view_price);
        TextView foodItemCat = (TextView) returnView.findViewById(R.id.food_item_view_cat);
        ImageView foodItemImageView = (ImageView) returnView.findViewById(R.id.food_item_view_image);
        final EditText foodQuantiy = (EditText) returnView.findViewById(R.id.food_item_view_quantity);
        final TextView quantityerror = (TextView) returnView.findViewById(R.id.quantityError);
        Button foodItemAddToCart = (Button) returnView.findViewById(R.id.foodItemAddToCart);
        foodItemTitle.setText(mFoodItem.getFoodName());
        foodItemReciepee.setText(mFoodItem.getFoodReciepee());
        foodItemPrice.setText(mFoodItem.getFoodPrice());
        foodItemCat.setText(mFoodItem.getFoodCat());
        Picasso.with(getContext()).load(mFoodItem.getFoodImage()).into(foodItemImageView);
        foodItemAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodQuantiy.getText().toString().equals("") ) {
                    quantityerror.setVisibility(View.VISIBLE);
                } else   if(Integer.parseInt(foodQuantiy.getText().toString()) <= 0)
                    quantityerror.setVisibility(View.VISIBLE);
                else {
                    quantityerror.setVisibility(View.GONE);
                    if (!(AppController.getInstance().getmCartItems().getItemQuantity().containsKey(mFoodItem))) {
                        CartItems cartItems = AppController.getInstance().getmCartItems();
                        Log.d("CartCheck", "FirstTime entered");
                        cartItems.addToCartList(mFoodItem,Integer.parseInt(foodQuantiy.getText().toString()));
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_replaceable, new CheckOutFragment()).commit();
                        fragmentTransaction.addToBackStack(null);
                    } else {
                        CartItems cartItems = AppController.getInstance().getmCartItems();
                        cartItems.putintoHashMap(mFoodItem, Integer.parseInt(foodQuantiy.getText().toString()));
                        Log.d("CartCheck", "secondTime entered");
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_replaceable, new CheckOutFragment()).commit();
                        fragmentTransaction.addToBackStack(null);
                    }
                }

            }
        });
        return returnView;
    }

}
