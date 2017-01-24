package com.example.shashankreddy.fooddoor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.models.ReviewItem;

import java.util.ArrayList;

/**
 * Created by shashank reddy on 1/20/2017.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {

    ArrayList<ReviewItem> list;
    Context context;

    public ReviewAdapter(Context context, ArrayList<ReviewItem> list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_view,parent,false);
        ReviewHolder holder = new ReviewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        ReviewItem item = list.get(position);
        holder.user.setText(item.getUserName());
        holder.date.setText(item.getDate());
        holder.review.setText(item.getReview());
        String rate = item.getRate();
        switch(rate)
        {
            case "1":
                holder.star1.setImageResource(R.drawable.star_select);
                break;
            case "2":
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                break;
            case "3":
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                break;
            case "4":
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                holder.star4.setImageResource(R.drawable.star_select);
                break;
            case "5":
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                holder.star4.setImageResource(R.drawable.star_select);
                holder.star5.setImageResource(R.drawable.star_select);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class ReviewHolder extends RecyclerView.ViewHolder{

    TextView user, date, review;
    ImageView star1, star2, star3, star4, star5;

    public ReviewHolder(View v) {
        super(v);
        user = (TextView) v.findViewById(R.id.reviewUser);
        date = (TextView) v.findViewById(R.id.reviewDate);
        review = (TextView) v.findViewById(R.id.userReview);
        star1 = (ImageView) v.findViewById(R.id.rate_star1);
        star2 = (ImageView) v.findViewById(R.id.rate_star2);
        star3 = (ImageView) v.findViewById(R.id.rate_star3);
        star4 = (ImageView) v.findViewById(R.id.rate_star4);
        star5 = (ImageView) v.findViewById(R.id.rate_star5);
    }
}
