package com.example.shashankreddy.fooddoor.models;

/**
 * Created by shashank reddy on 1/19/2017.
 */
public class OrderItem {

    private String id, name,quantity, totalCost, status, address, date;

    public OrderItem() { }

    public void setOrderId(String string)
    {
        this.id=string;
    }

    public String getOrderId()
    {
        return this.id;
    }

    public void setOrderName(String string)
    {
        this.name=string;
    }

    public String getOrderName()
    {
        return this.name;
    }

    public void setOrderQuantity(String string)
    {
        this.quantity=string;
    }

    public String getOrderQuantity()
    {
        return this.quantity;
    }

    public void setTotalCost(String string)
    {
        this.totalCost=string;
    }

    public String getTotalCost()
    {
        return this.totalCost;
    }

    public void setOrderStatus(String string)
    {
        this.status=string;
    }

    public String getOrderStatus()
    {
        return this.status;
    }

    public void setDeliverAdd(String string)
    {
        this.address=string;
    }

    public String getDeliverAdd()
    {
        return this.address;
    }

    public void setOrderDate(String string)
    {
        this.date=string;
    }

    public String getOrderDate()
    {
        return this.date;
    }

}


