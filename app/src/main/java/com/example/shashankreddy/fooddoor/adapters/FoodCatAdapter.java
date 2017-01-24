package com.example.shashankreddy.fooddoor.adapters;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.fragments.CheckOutFragment;
import com.example.shashankreddy.fooddoor.fragments.FoodItemViewFragment;
import com.example.shashankreddy.fooddoor.models.CartItems;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodCatAdapter extends RecyclerView.Adapter<FoodCatAdapter.MyViewHolder> {
    public static final int VEG=1;
    public static final int NON_VEG =2;
    ArrayList<FoodItemList> mFoodItemList = new ArrayList<>();
    private Context mContext;

    public FoodCatAdapter(Context mContext, int cat){
        this.mContext = mContext;
        if(cat == VEG)
            mFoodItemList = AppController.getInstance().getmFoodItemList().getVegFood();
        else if(cat == NON_VEG)
            mFoodItemList = AppController.getInstance().getmFoodItemList().getNonVegFood();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_catagery_layout,parent,false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TextView foodName = holder.foodName;
        TextView foodReciepee = holder.foodReciepee,foodPrice = holder.foodPrice;
        final ImageView foodImage = holder.foodImage,addToCartOnMainView = holder.addToCartOnHome;

        foodName.setText(mFoodItemList.get(position).getFoodName());
        foodReciepee.setText(mFoodItemList.get(position).getFoodReciepee());
        foodPrice.setText("$ "+mFoodItemList.get(position).getFoodPrice());
        CardView foodItemCardView = holder.foodItemCardView;
        Picasso.with(mContext).load(mFoodItemList.get(position).getFoodImage()).into(foodImage);

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(FoodCatAdapter.class.getSimpleName(), mFoodItemList.get(position).getFoodId() + "  " + position);
                FoodItemViewFragment foodItemViewFragment = FoodItemViewFragment.newInstance(mFoodItemList.get(position).getFoodId(), position);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_replaceable, foodItemViewFragment).commit();
                fragmentTransaction.addToBackStack("FoodItemView");
            }
        });

        addToCartOnMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodItemList foodItemList = mFoodItemList.get(position);
                Log.d("FoodCatAdapter","add to cart in home i clicked");
                if (!(AppController.getInstance().getmCartItems().getItemQuantity().containsKey(foodItemList))) {
                    CartItems cartItems = AppController.getInstance().getmCartItems();
                    Log.d("CartCheck", "FirstTime entered");
                    cartItems.addToCartList(foodItemList, 1);
                } else {
                    CartItems cartItems = AppController.getInstance().getmCartItems();
                    cartItems.putintoHashMap(foodItemList, 1);
                    Log.d("CartCheck", "secondTime entered");

                }

                addToCartOnMainView.setImageResource(R.drawable.add_to_basket_after_click);
                addToCartOnMainView.setClickable(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFoodItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView foodName,foodReciepee,foodPrice;
        ImageView foodImage,addToCartOnHome;
        CardView foodItemCardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            foodItemCardView = (CardView) itemView.findViewById(R.id.food_catageroy_layout_cardview);
            foodName = (TextView) itemView.findViewById(R.id.food_title);
            foodReciepee= (TextView) itemView.findViewById(R.id.food_recieppe);
            foodPrice =(TextView) itemView.findViewById(R.id.food_price);
            foodImage = (ImageView) itemView.findViewById(R.id.food_image_view);
            addToCartOnHome= (ImageView) itemView.findViewById(R.id.addtocart_main_view);
        }
    }
}
