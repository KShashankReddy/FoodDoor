package com.example.shashankreddy.fooddoor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.adapters.FoodCatAdapter;

public class NonVegFragment extends Fragment {

    public NonVegFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_food_catageroy,container,false);
        RecyclerView recyclerView = (RecyclerView) retView.findViewById(R.id.food_catageroy_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FoodCatAdapter foodCatAdapter = new FoodCatAdapter(getActivity(),FoodCatAdapter.NON_VEG);
        recyclerView.setAdapter(foodCatAdapter);
        return retView;
    }
}
