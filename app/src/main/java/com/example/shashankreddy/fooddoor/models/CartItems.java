package com.example.shashankreddy.fooddoor.models;

import java.util.ArrayList;
import java.util.HashMap;

public class CartItems {

    private ArrayList<FoodItemList> cartItemsList;
    private HashMap<FoodItemList,Integer> itemQuantity;

    public HashMap<FoodItemList, Integer> getItemQuantity() {
        return itemQuantity;
    }

    public CartItems() {
        cartItemsList = new ArrayList<FoodItemList>();
        itemQuantity = new HashMap<>();
    }


    public ArrayList<FoodItemList> getCartItemsList() {
        return cartItemsList;

    }

    public void addToCartList(FoodItemList selectedProduct, int i) {
        cartItemsList.add(selectedProduct);
        itemQuantity.put(selectedProduct, i);
    }

    public void putintoHashMap(FoodItemList selectedProduct,int i){
        int quantity = itemQuantity.get(selectedProduct);
        itemQuantity.put(selectedProduct,quantity+i);
    }

    public void clearCart(){
        cartItemsList.clear();
        itemQuantity.clear();
    }
}
