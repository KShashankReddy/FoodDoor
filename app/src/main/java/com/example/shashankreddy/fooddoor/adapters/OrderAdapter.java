package com.example.shashankreddy.fooddoor.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.fragments.OrderTrackFragment;
import com.example.shashankreddy.fooddoor.fragments.ReviewFragment;
import com.example.shashankreddy.fooddoor.models.OrderItem;

import java.util.ArrayList;

/**
 * Created by shashank reddy on 1/19/2017.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private Context context;
    private ArrayList<OrderItem> list;

    public OrderAdapter(Context context, ArrayList<OrderItem> list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view,parent,false);
        OrderViewHolder viewHolder = new OrderViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderItem item = list.get(position);
        final String id = item.getOrderId();
        holder.id.setText(id);
        final String name = item.getOrderName();
        holder.name.setText(name);
        holder.quantity.setText(item.getOrderQuantity());
        holder.totalCost.setText(item.getTotalCost());
        holder.address.setText(item.getDeliverAdd());
        holder.date.setText(item.getOrderDate());
        holder.trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // track order
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                OrderTrackFragment fragment = new OrderTrackFragment();
                fragment.setArguments(bundle);
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_replaceable, fragment).commit();
            }
        });
        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // write a review
                Bundle bundle = new Bundle();
                bundle.putString("foodname",name);
                ReviewFragment fragment = new ReviewFragment();
                fragment.setArguments(bundle);
                FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
                fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_replaceable,fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class OrderViewHolder extends RecyclerView.ViewHolder {

    TextView id, name,quantity, totalCost, address, date;
    Button trackBtn, reviewBtn;

    public OrderViewHolder(View v) {
        super(v);
        id = (TextView) v.findViewById(R.id.orderItemIdText);
        name = (TextView) v.findViewById(R.id.orderItemNameText);
        quantity = (TextView) v.findViewById(R.id.orderItemQuantityText);
        totalCost = (TextView) v.findViewById(R.id.totalOrderItemText);
        address = (TextView) v.findViewById(R.id.orderDeliverAddText);
        date = (TextView) v.findViewById(R.id.orderItemDateText);
        trackBtn = (Button) v.findViewById(R.id.trackBtn);
        reviewBtn = (Button) v.findViewById(R.id.reviewBtn);
    }
}
