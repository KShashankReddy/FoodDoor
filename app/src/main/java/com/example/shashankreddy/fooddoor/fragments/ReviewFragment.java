package com.example.shashankreddy.fooddoor.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.adapters.ReviewAdapter;
import com.example.shashankreddy.fooddoor.models.ReviewItem;
import com.example.shashankreddy.fooddoor.utils.DbHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shashank reddy on 1/20/2017.
 */
public class ReviewFragment extends Fragment {

    String foodName, userName, rate, date, review;
    ImageView star1, star2, star3, star4, star5;
    TextInputEditText reviewText;
    Button cancelBtn, submitBtn;
    CardView emptyReviewCard, preReviewCard;
    RecyclerView reviewRecyclerView;

    ArrayList<ReviewItem> list;
    int numOfReview;
    DbHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        foodName=bundle.getString("foodname");
        // userName = AppController.getInstance().getmUserInfo().getUserName();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Food@Door", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("name","Unknown");
        rate = "0";
        review = "";
        dbHelper=new DbHelper(getActivity().getApplicationContext());
        db=dbHelper.getWritableDatabase();
        list = new ArrayList<ReviewItem>();
        cursor = dbHelper.searchRecord(foodName);
        numOfReview = cursor.getCount();
        if(numOfReview > 0)
        {
            int position = 0;
            while(cursor.moveToNext())
            {
                ReviewItem item = new ReviewItem();
                item.setUserName(cursor.getString(cursor.getColumnIndex(dbHelper.USERNAME)));
                item.setFoodName(cursor.getString(cursor.getColumnIndex(dbHelper.FOODNAME)));
                item.setRate(cursor.getString(cursor.getColumnIndex(dbHelper.RATE)));
                item.setDate(cursor.getString(cursor.getColumnIndex(dbHelper.DATE)));
                item.setReview(cursor.getString(cursor.getColumnIndex(dbHelper.REVIEW)));
                list.add(position,item);
                position++;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        // get View components
        star1 = (ImageView) v.findViewById(R.id.star1);
        star2 = (ImageView) v.findViewById(R.id.star2);
        star3 = (ImageView) v.findViewById(R.id.star3);
        star4 = (ImageView) v.findViewById(R.id.star4);
        star5 = (ImageView) v.findViewById(R.id.star5);
        reviewText = (TextInputEditText) v.findViewById(R.id.reviewText);
        cancelBtn = (Button) v.findViewById(R.id.button3);
        submitBtn = (Button) v.findViewById(R.id.button2);
        emptyReviewCard = (CardView) v.findViewById(R.id.emptyReviewCard);
        preReviewCard = (CardView) v.findViewById(R.id.preReviewCard);
        reviewRecyclerView = (RecyclerView) v.findViewById(R.id.reviewRecyclerView);
        // get reviews from DB for this item
        if(numOfReview==0)
        {
            // no previous reviews
            emptyReviewCard.setVisibility(View.VISIBLE);
            preReviewCard.setVisibility(View.GONE);
        }
        else
        {
            // show previews reviews
            emptyReviewCard.setVisibility(View.GONE);
            preReviewCard.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);
            reviewRecyclerView.setLayoutManager(linearLayoutManager);
            ReviewAdapter adapter = new ReviewAdapter(getActivity(), list);
            reviewRecyclerView.setAdapter(adapter);
        }
        // set view actions
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_blank);
                star3.setImageResource(R.drawable.star_blank);
                star4.setImageResource(R.drawable.star_blank);
                star5.setImageResource(R.drawable.star_blank);
                rate = "1";
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_blank);
                star4.setImageResource(R.drawable.star_blank);
                star5.setImageResource(R.drawable.star_blank);
                rate = "2";
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                star4.setImageResource(R.drawable.star_blank);
                star5.setImageResource(R.drawable.star_blank);
                rate = "3";
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                star4.setImageResource(R.drawable.star_select);
                star5.setImageResource(R.drawable.star_blank);
                rate = "4";
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                star4.setImageResource(R.drawable.star_select);
                star5.setImageResource(R.drawable.star_select);
                rate = "5";
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrdersFragment fragment = new OrdersFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_replaceable,fragment);
                ft.commit();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review = reviewText.getText().toString();
                if(rate.equals("0") || review.equals(""))
                {
                    // show alert
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Attention");
                    alertDialog.setMessage("Cannot submit an empty review!");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    AlertDialog ad=alertDialog.create();
                    ad.show();
                }
                else
                {
                    // add to DB
                    date = DateFormat.getDateInstance().format(new Date());
                    dbHelper.addRecord(null,userName,foodName,rate,date,review);
                    // reload fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("foodname",foodName);
                    ReviewFragment fragment = new ReviewFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_replaceable,fragment);
                    ft.commit();
                }
            }
        });
        //
        return v;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        list.clear();
    }
}

