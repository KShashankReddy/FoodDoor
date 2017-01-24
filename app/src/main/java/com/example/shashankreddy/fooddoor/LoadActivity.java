package com.example.shashankreddy.fooddoor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.UserAddress;
import com.example.shashankreddy.fooddoor.models.UserInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoadActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener {
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    SharedPreferences sharedPreferences;
    private static final int INTERVAL = 60 * 1000;
    private static final int FASTINTERVAL = 60 * 500;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 102;
    private boolean mLocationPermissionGranted;
   // private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
    TextView deliveryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        sharedPreferences = getSharedPreferences("Food@Door", MODE_PRIVATE);
        deliveryAddress = (TextView) findViewById(R.id.deliveryAddress);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTINTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
            if (mGoogleApiClient.isConnected())
                requestLocationUpdates();
    }

    //on google api connected
    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        Log.d(LoadActivity.class.getSimpleName(), "requestUpdated");

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    //connection fail callbacks
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LoadActivity.class.getSimpleName(), "suspended" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LoadActivity.class.getSimpleName(), "failed" + connectionResult);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LoadActivity.class.getSimpleName(),location.getLatitude()+" "+ location.getLongitude());
        getClientAddress(location.getLatitude(), location.getLongitude());
    }

    private void getClientAddress(double latitude, double longitude) {
        Geocoder gc = new Geocoder(LoadActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(latitude,longitude,2);
            if(addresses.size()>0&&addresses != null){
                UserAddress userAddress = AppController.getInstance().getmUserAddress();
                if(userAddress.getCurrentAddress()) {
                    Log.d("AddressMapFragment",addresses.get(0).toString()+" is saved");
                    userAddress.setCurrentAddressObj(addresses.get(0));
                    userAddress.setAddress1(addresses.get(0).getAddressLine(0));
                    userAddress.setAddress2(addresses.get(0).getAddressLine(1));
                    userAddress.setCountry(addresses.get(0).getCountryCode());
                    userAddress.setPostalCode(addresses.get(0).getPostalCode());
                    userAddress.setmLatitude(latitude);
                    userAddress.setmLogitude(longitude);
                    deliveryAddress.setText(userAddress.getAddress1());
                    logincheckOnSpalsh();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mLocationPermissionGranted = true;
                break;
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }



    private void logincheckOnSpalsh(){
        String mobile = sharedPreferences.getString("mobile",null);
        String password = sharedPreferences.getString("password", null);
        String loginUrl = "http://rjtmobile.com/ansari/fos/fosapp/fos_login.php?&user_phone="+mobile+"&user_password="+password;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PersonalInfo",response);
                if(response.contains("success")){
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject user = jsonArray.getJSONObject(0);
                        UserInfo userInfo = AppController.getInstance().getmUserInfo();
                        userInfo.setUserName(user.getString("UserName"));
                        userInfo.setUserEmail(user.getString("UserEmail"));
                        userInfo.setUserPhone(user.getString("UserMobile"));
                        UserAddress address = AppController.getInstance().getmUserAddress();
                        address.setDefaultAddress(user.getString("UserAddress"));
                        Geocoder geocoder = new Geocoder(LoadActivity.this);
                        try {
                            List<Address> addressList = geocoder.getFromLocationName("1942 wessel ct,st charles,60174", 2);
                            Log.d("LoadActivity",addressList.toString());
                            if(addressList.size()>0) {
                                Log.d("PersonalInfo", addressList.get(0).toString());
                                address.setDefaultUserAddressObj(addressList.get(0));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(LoadActivity.class.getSimpleName(),userInfo.toString());
                        Log.d(LoadActivity.class.getSimpleName(),address.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(LoadActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(LoadActivity.class.getSimpleName(), response + "failed");
                    Intent intent = new Intent(LoadActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LoadActivity.class.getSimpleName(), error.getMessage());
            }
        });
        Log.d(LoadActivity.class.getSimpleName(), stringRequest.getUrl());
        AppController.getInstance().addRequestQueue(stringRequest);
    }

}
