package com.example.shashankreddy.fooddoor.models;

import android.location.Address;
import android.util.Log;

import com.example.shashankreddy.fooddoor.fragments.AddressMapFragment;

public class UserAddress {
    String Address1,Address2,country,postalCode;
    Boolean currentAddress=true;
    double mLatitude,mLogitude;
    String defaultAddress;
    Address currentAddressObj;
    Address deliveryAddress;

    public Address getDefaultUserAddressObj() {
        return defaultUserAddressObj;
    }

    public void setDefaultUserAddressObj(Address defaultUserAddressObj) {
        this.defaultUserAddressObj = defaultUserAddressObj;
    }

    Address defaultUserAddressObj;

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Address getCurrentAddressObj() {
        return currentAddressObj;
    }

    public void setCurrentAddressObj(Address currentAddressObj) {
        this.deliveryAddress = currentAddressObj;
        this.currentAddressObj = currentAddressObj;
    }



    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
        Log.d(AddressMapFragment.class.getSimpleName(), mLatitude+" set");
    }

    public void setmLogitude(double mLogitude) {
        this.mLogitude = mLogitude;
        Log.d(AddressMapFragment.class.getSimpleName(), mLatitude+" set");
    }

    public double getmLatitude() {
        Log.d(AddressMapFragment.class.getSimpleName(), mLatitude+" get");
        return mLatitude;
    }

    public double getmLogitude() {
        Log.d(AddressMapFragment.class.getSimpleName(), mLogitude+" get");
        return mLogitude;
    }

    public UserAddress(String address1, String address2, String country, String postalCode,double mLatitude,double mLogitude) {
        Address1 = address1;
        Address2 = address2;
        this.country = country;
        this.postalCode = postalCode;
        this.mLatitude = mLatitude;
        this.mLogitude = mLogitude;

    }

    public UserAddress() {

    }

    public String getAddress1() {
        return Address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(Boolean currentAddress) {
        this.currentAddress = currentAddress;
    }

}
