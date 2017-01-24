package com.example.shashankreddy.fooddoor.models;


import java.util.ArrayList;

public class FoodItemList {

    String foodId,foodName,foodReciepee,foodPrice,foodCat,foodImage;
    ArrayList<FoodItemList> mFoodItemLists;


    public FoodItemList(){
        mFoodItemLists = new ArrayList<>();
    }
    public FoodItemList(String foodId, String foodName, String foodReciepee, String foodPrice, String foodCat, String foodImage) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodReciepee = foodReciepee;
        this.foodPrice = foodPrice;
        this.foodCat = foodCat;
        this.foodImage = foodImage;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodReciepee() {
        return foodReciepee;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodCat() {
        return foodCat;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public ArrayList<FoodItemList> getmFoodItemLists() {
        return mFoodItemLists;
    }

    public void setmFoodItemLists(ArrayList<FoodItemList> mFoodItemLists) {
        this.mFoodItemLists = mFoodItemLists;
    }

    public ArrayList<FoodItemList> getVegFood(){
        ArrayList<FoodItemList> vegList= new ArrayList<>();
        for(FoodItemList foodItem : mFoodItemLists){
            if(foodItem.foodCat.equalsIgnoreCase("veg"))
                vegList.add(foodItem);
        }
        return vegList;
    }


    public ArrayList<FoodItemList> getNonVegFood(){
        ArrayList<FoodItemList> nonVegList= new ArrayList<>();
        for(FoodItemList foodItem : mFoodItemLists){
            if(foodItem.foodCat.equalsIgnoreCase("non-veg"))
                nonVegList.add(foodItem);
        }
        return nonVegList;
    }

    public FoodItemList getFoodItem(String foodItemID){
        for(FoodItemList foodItem : mFoodItemLists){
            if(foodItem.foodId.equalsIgnoreCase(foodItemID))
            return  foodItem;
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == foodId ? 0 : foodId.hashCode());
        hash = 31 * hash + (null == foodName ? 0 : foodName.hashCode());
        hash = 31 * hash + (null == foodPrice ? 0 : foodPrice.hashCode());
        hash = 31 * hash + (null == foodCat ? 0 : foodCat.hashCode());
        hash = 31 * hash + (null == foodReciepee ? 0 : foodReciepee.hashCode());
        hash = 31 * hash + (null == foodImage ? 0 : foodImage.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        FoodItemList foodItemList = (FoodItemList)obj;
        return  (foodId == foodItemList.foodId || (foodId != null && foodId.equals(foodItemList.foodId)));
    }
}
