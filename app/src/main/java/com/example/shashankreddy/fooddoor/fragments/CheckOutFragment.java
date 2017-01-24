package com.example.shashankreddy.fooddoor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.adapters.CartAdapter;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.example.shashankreddy.fooddoor.utils.SwipeHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckOutFragment extends Fragment implements CartAdapter.NotifyFragment, SwipeHelper.OnSwipeDataChange {

    private TextView cartTotal,countText;
    private CardView confirmOrder;
    private RelativeLayout emptyCartView;

    public CheckOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView= inflater.inflate(R.layout.fragment_check_out, container, false);
        RecyclerView cartReyclerView = (RecyclerView) returnView.findViewById(R.id.cartRecyclerView);
        cartTotal = (TextView) returnView.findViewById(R.id.cartTotal);
        confirmOrder = (CardView) returnView.findViewById(R.id.checkoutCart);
        NestedScrollView cartCardView = (NestedScrollView) returnView.findViewById(R.id.cart_card_view);
        emptyCartView= (RelativeLayout) returnView.findViewById(R.id.emptyCartDistplay);
        if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Cart");
        }

        if(AppController.getInstance().getmCartItems().getCartItemsList().size()>0){
            cartCardView.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
            cartReyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            CartAdapter adapter = new CartAdapter(getActivity(),this);
            cartReyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new SwipeHelper(0,ItemTouchHelper.LEFT,adapter,this);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(cartReyclerView);
            cartTotal.setText(getTotalValueOfCart());
            confirmOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_replaceable,new PaymentFragment(),"PaymentFragment").commit();
                    fragmentTransaction.addToBackStack(null);
                }
            });
        }else{
            emptyCartView.setVisibility(View.VISIBLE);
            cartCardView.setVisibility(View.GONE);
        }
        return returnView;
    }

    public String getTotalValueOfCart() {
        double count = 0;
        ArrayList<FoodItemList> cartItems = AppController.getInstance().getmCartItems().getCartItemsList();
        for (FoodItemList selectedItem:cartItems){
            int quantity = AppController.getInstance().getmCartItems().getItemQuantity().get(selectedItem);
            count = count+quantity*Double.parseDouble(selectedItem.getFoodPrice());
        }
        return "$"+count;
    }

    @Override
    public void onItemTotalChanged() {
        cartTotal.setText(getTotalValueOfCart());
       /* int count=getItemCount();
        if( count>0){
            countText.setVisibility(View.VISIBLE);
            countText.setText(count+"");
        }*/
    }

    public int getItemCount() {
        int quantity=0;
        ArrayList<FoodItemList> cartItems = AppController.getInstance().getmCartItems().getCartItemsList();
        for (FoodItemList selectedItem:cartItems){
            quantity += AppController.getInstance().getmCartItems().getItemQuantity().get(selectedItem);
        }
        return quantity;
    }

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        View count = menu.findItem(R.id.cart_icon).getActionView();
        countText = (TextView) count.findViewById(R.id.cartViewCountNotify);
    }*/

    @Override
    public void onSwipeCountChange() {
        cartTotal.setText(getTotalValueOfCart());
        /*int count = getItemCount();
        if( count>0){
            countText.setVisibility(View.VISIBLE);
            countText.setText(count+"");
        }else if(AppController.getInstance().getmCartItems().getCartItemsList().size() == 0)
            emptyCartView.setVisibility(View.VISIBLE);*/
    }

}
