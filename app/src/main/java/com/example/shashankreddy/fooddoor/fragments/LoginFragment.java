package com.example.shashankreddy.fooddoor.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.MainActivity;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.UserAddress;
import com.example.shashankreddy.fooddoor.models.UserInfo;
import com.example.shashankreddy.fooddoor.utils.PasswordValidator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;



public class LoginFragment extends Fragment {

    private final String TAG = "Login";

    TextInputEditText mobile, password;
    Button signInBtn, signUpBtn, resetBtn;
    String mobileNum, passWord;

    PasswordValidator validator;

    LoginButton loginButton;
    CallbackManager callbackManager;

    private AdView mAdView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        validator = new PasswordValidator();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_login,container,false);

        // login part
        mobile= (TextInputEditText) v.findViewById(R.id.mobile);
        mobile.requestFocus();
        password= (TextInputEditText) v.findViewById(R.id.password);
        signInBtn= (Button) v.findViewById(R.id.signInBtn);
        signUpBtn= (Button) v.findViewById(R.id.signUpBtn);
        resetBtn= (Button) v.findViewById(R.id.reset);
        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        // ads part
        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();
        mAdView.loadAd(adRequest);

        // navigate to login
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(submitForm())
                {
                    if(validator.validate(passWord))
                    {
                        Toast.makeText(getActivity(),"Checking...",Toast.LENGTH_LONG).show();
                        checkLogin(mobileNum, passWord);
                    }
                    else
                    {
                        password.setError(validator.PASSWORD_DISCRIPTION);
                    }
                }
                else
                {
                    checkValidation(mobileNum, passWord);
                }
            }
        });
        // navigate to registration activity
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment fragment = new RegisterFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_login, fragment).commit();
                ft.addToBackStack(null);
            }

        });
        // go to reset password screen
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                        ResetFragment fragment = new ResetFragment();
                        FragmentManager fm=getFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        ft.replace(R.id.activity_login,fragment).commit();
                        ft.addToBackStack(null);

            }
        });
        // use facebook login
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.d("facebook login",loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                try
                                {
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    // get phone number & store data
                                    showFacebookLoginAlert(name, email);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error)
            {
                error.printStackTrace();
            }
        });

        return v;
    }

    private void showFacebookLoginAlert(final String name, final String email) {
        final UserInfo userInfo = AppController.getInstance().getmUserInfo();
        userInfo.setUserName(name);
        userInfo.setUserEmail(email);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_get_mobile, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.dialog_button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText phoneText = (EditText) dialogView.findViewById(R.id.login_phone);
                String phone = phoneText.getText().toString();
                userInfo.setUserPhone(phone);
                // store data
                SharedPreferences setting=getActivity().getSharedPreferences("Food@Door",MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                editor.putString("loginBy","Facebook");
                editor.putString("name", name);
                editor.putString("email",email);
                editor.putString("mobile",phone);
                editor.apply();
                // go to main
                Intent homeIntent=new Intent(getActivity(),MainActivity.class);
                startActivity(homeIntent);
                getActivity().finish();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean submitForm()
    {
        mobileNum=mobile.getText().toString().trim();
        passWord=password.getText().toString().trim();
        if(mobileNum.isEmpty() || passWord.isEmpty())
            return false;
        else
            return true;
    }

    private void checkValidation(String mobileNum, String passWord)
    {
        if(mobileNum.isEmpty())
        {
            mobile.setError("Please enter a phone number");
        }
        if(passWord.isEmpty())
        {
            password.setError("Please enter a password");
        }
    }

    private void checkLogin(String mobile, final String password){
        String urlLogin="http://rjtmobile.com/ansari/fos/fosapp/fos_login.php?&user_phone="+mobile+"&user_password="+password;
        StringRequest req = new StringRequest(Request.Method.GET, urlLogin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                if(response.contains("success")){
                    try {
                        JSONArray array = new JSONArray(response);
                        JSONObject object = array.getJSONObject(0);
                        String name = object.getString("UserName");
                        String email = object.getString("UserEmail");
                        String mobile = object.getString("UserMobile");
                        String address = object.getString("UserAddress");
                        // store data
                        SharedPreferences setting=getActivity().getSharedPreferences("Food@Door",MODE_PRIVATE);
                        SharedPreferences.Editor editor = setting.edit();
                        editor.putString("loginBy","Registration");
                        editor.putString("name", name);
                        editor.putString("email",email);
                        editor.putString("mobile",mobile);
                        editor.putString("password",password);
                        editor.putString("address",address);
                        editor.apply();
                        UserInfo userInfo = AppController.getInstance().getmUserInfo();
                        userInfo.setUserName(name);
                        userInfo.setUserEmail(email);
                        userInfo.setUserPhone(mobile);
                        UserAddress uAddress = AppController.getInstance().getmUserAddress();
                        uAddress.setDefaultAddress(address);
                        Geocoder geocoder = new Geocoder(getContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocationName("1942 wessel ct st Charles,60174", 2);
                            Log.d("PersonalInfo",addressList.get(0).toString());
                            uAddress.setDefaultUserAddressObj(addressList.get(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent homeIntent=new Intent(getActivity(),MainActivity.class);
                        startActivity(homeIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    // invalid mobile / password
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Attention");
                    alertDialog.setMessage("Invalid mobile / password!");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel();
                        }
                    });
                    AlertDialog ad=alertDialog.create();
                    ad.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addRequestQueue(req);
    }

    @Override
    public void onPause()
    {
        if (mAdView != null)
        {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume()
    {
        if (mAdView != null)
        {
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        if (mAdView != null)
        {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
