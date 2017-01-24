package com.example.shashankreddy.fooddoor.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.shashankreddy.fooddoor.adapters.CartAdapter;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.FoodItemList;

/**
 * Created by shashank reddy on 1/16/2017.
 */
public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    CartAdapter mCartAdapter;
    OnSwipeDataChange onSwipeDataChange;
    public SwipeHelper(int dragDirs, int swipeDirs, CartAdapter adapter,OnSwipeDataChange onSwipeDataChange) {
        super(dragDirs, swipeDirs);
        mCartAdapter =adapter;
        this.onSwipeDataChange= onSwipeDataChange;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        FoodItemList foodItemList = AppController.getInstance().getmCartItems().getCartItemsList().get(viewHolder.getAdapterPosition());
        AppController.getInstance().getmCartItems().getItemQuantity().remove(foodItemList);
        AppController.getInstance().getmCartItems().getCartItemsList().remove(viewHolder.getAdapterPosition());
        mCartAdapter.notifyDataSetChanged();
        onSwipeDataChange.onSwipeCountChange();
    }

    public interface OnSwipeDataChange{
        void onSwipeCountChange();
    }
}
