package com.example.shashankreddy.fooddoor.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.CartItems;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    ArrayList<FoodItemList> cartitemsList;
    Context mContext;
    private TextView selectedItemPriceQuantity,mcartItemTotal,mcartItemQuantitySelected,mTextViewAlert;
    private ImageView mincrimentQuantity,mdecrimentQuantity;
    private NotifyFragment mNotifyFragment;

    public CartAdapter(Context mContext,NotifyFragment mNotifyFragment){
        cartitemsList = AppController.getInstance().getmCartItems().getCartItemsList();
        this.mContext =mContext;
        this.mNotifyFragment = mNotifyFragment;
    }

    public CartAdapter() {
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ImageView selectedImageView = holder.selectedImageView;
        TextView selectedItemTitle = holder.selectedItemTitle;
        TextView selectedItemDesc = holder.selectedItemDesc;
        selectedItemPriceQuantity = holder.selectedItemPriceQuantity;
        mcartItemTotal = holder.cartItemTotal;
        mcartItemQuantitySelected= holder.cartItemQuantitySelected;
        mincrimentQuantity = holder.incrimentQuantity;
        mdecrimentQuantity = holder.decrimentQuantity;
        mTextViewAlert = holder.textViewAlert;
        selectedItemTitle.setText(cartitemsList.get(position).getFoodName());
        selectedItemPriceQuantity.setText(caluculatePriceQuantity(cartitemsList.get(position)));
        Picasso.with(mContext).load(cartitemsList.get(position).getFoodImage()).into(selectedImageView);
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View itemViewToAni, int position) {

    }

    private void itemDecrimented(FoodItemList products,int poition) {
        CartItems cartItems = AppController.getInstance().getmCartItems();
        int quantity = AppController.getInstance().getmCartItems().getItemQuantity().get(products);
        if(quantity>0) {
            mTextViewAlert.setVisibility(View.INVISIBLE);
            cartItems.putintoHashMap(products, -1);
            notifyItemChanged(poition);
        }else {
            mTextViewAlert.setVisibility(View.VISIBLE);
            mTextViewAlert.setText("Item quantity cant't be less then 0");
        }
    }

    private void itemIncrimented(FoodItemList products,int position) {
        CartItems cartItems = AppController.getInstance().getmCartItems();
        int quantity = AppController.getInstance().getmCartItems().getItemQuantity().get(products);
            mTextViewAlert.setVisibility(View.INVISIBLE);
            cartItems.putintoHashMap(products, 1);
            notifyItemChanged(position);
    }

    private String caluculatePriceQuantity(FoodItemList foodItemList) {
        String priceQuantiy="";
        int quantity = AppController.getInstance().getmCartItems().getItemQuantity().get(foodItemList);
        double price = quantity*Double.parseDouble(foodItemList.getFoodPrice());
        mcartItemTotal.setText("$"+price+"");
        mcartItemQuantitySelected.setText(quantity+"");
        priceQuantiy= quantity+"*"+foodItemList.getFoodPrice();
        return priceQuantiy;
    }

    @Override
    public int getItemCount() {
        return cartitemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView selectedImageView,incrimentQuantity,decrimentQuantity;
        TextView selectedItemTitle,selectedItemDesc,selectedItemPriceQuantity,cartItemTotal,cartItemQuantitySelected,textViewAlert;
        public MyViewHolder(final View itemView) {
            super(itemView);
            selectedImageView = (ImageView) itemView.findViewById(R.id.cartItemImage);
            selectedItemTitle = (TextView) itemView.findViewById(R.id.cartItemTitle);
            selectedItemDesc = (TextView) itemView.findViewById(R.id.cartItemDesc);
            selectedItemPriceQuantity = (TextView) itemView.findViewById(R.id.cartItemquantityPrice);
            cartItemTotal = (TextView) itemView.findViewById(R.id.cartItemTotal);
            cartItemQuantitySelected = (TextView) itemView.findViewById(R.id.cartItemquantitySelected);
            incrimentQuantity = (ImageView) itemView.findViewById(R.id.incrimentCartItem);
            decrimentQuantity = (ImageView) itemView.findViewById(R.id.decrimentCartItem);
            textViewAlert = (TextView) itemView.findViewById(R.id.textViewAlert);

            incrimentQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemIncrimented(cartitemsList.get(getAdapterPosition()),getAdapterPosition());
                    mNotifyFragment.onItemTotalChanged();
                }
            });
            decrimentQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemDecrimented(cartitemsList.get(getAdapterPosition()),getAdapterPosition());
                    mNotifyFragment.onItemTotalChanged();
                }
            });

        }
    }

    public interface  NotifyFragment{
        void onItemTotalChanged();
    }

}
