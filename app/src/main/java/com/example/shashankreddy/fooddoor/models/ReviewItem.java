package com.example.shashankreddy.fooddoor.models;

/**
 * Created by shashank reddy on 1/20/2017.
 */
public class ReviewItem {
    private String userName, foodName, rate, date, review;

    public ReviewItem() { }

    public void setUserName(String string)
    {
        userName = string;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setFoodName(String string)
    {
        foodName = string;
    }

    public String getFoodName()
    {
        return foodName;
    }

    public void setRate(String string)
    {
        rate = string;
    }

    public String getRate()
    {
        return rate;
    }

    public void setDate(String string)
    {
        date = string;
    }

    public String getDate()
    {
        return date;
    }

    public void setReview(String string)
    {
        review = string;
    }

    public String getReview()
    {
        return review;
    }
}

