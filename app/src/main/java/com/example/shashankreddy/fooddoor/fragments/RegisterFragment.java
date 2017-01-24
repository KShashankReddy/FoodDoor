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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.UserAddress;
import com.example.shashankreddy.fooddoor.models.UserInfo;
import com.example.shashankreddy.fooddoor.utils.PasswordValidator;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


public class RegisterFragment extends Fragment {
    public static final String TAG = "Registration";
    TextInputEditText userName, userEmail, userMobile, userPassword;
    Button regButton;
    PasswordValidator validator;
    View shippingView;
    String shippingState;
    String country;
    Boolean addressEnterd = false;
    TextView userAddressError;
    TextInputLayout streetLayout,aptorSuiteLayout,cityLayout,zipcodeLayout;
    TextInputEditText shipmentstreet,shipmentApt,shipmentCity,shipmentZipcode;
    AppCompatSpinner shipmentState,shipmentCountry;
    String shipmentAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        validator = new PasswordValidator();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_register,container,false);
        userName = (TextInputEditText) v.findViewById(R.id.userName);
        userEmail = (TextInputEditText) v.findViewById(R.id.userEmail);
        userMobile = (TextInputEditText) v.findViewById(R.id.userMobile);
        userPassword = (TextInputEditText) v.findViewById(R.id.userPassword);
        shippingView = v.findViewById(R.id.shippingLayout);
        userAddressError = (TextView) v.findViewById(R.id.register_address_err);
        CardView defaultAddress = (CardView) v.findViewById(R.id.personal_info_default_address);
        regButton = (Button) v.findViewById(R.id.regButton);
        shippingLayoutDeatils(shippingView);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = userName.getText().toString();
                String uEmail = userEmail.getText().toString();
                String uMobile = userMobile.getText().toString();
                String uPassword = userPassword.getText().toString();
                if (uName.isEmpty() || uEmail.isEmpty() || uMobile.isEmpty() || uPassword.isEmpty() || !addressEnterd) {
                    if (uName.isEmpty())
                        userName.setError("Please enter your name");
                    if (uEmail.isEmpty())
                        userEmail.setError("Please enter your email");
                    if (uMobile.isEmpty())
                        userMobile.setError("Please enter your phone number");
                    if (uPassword.isEmpty())
                        userPassword.setError("Please enter your password");
                    if (!addressEnterd)
                        userAddressError.setText("please enter your address");
                } else if (!validator.validate(uPassword)) {
                    userPassword.setError(validator.PASSWORD_DISCRIPTION);
                } else
                    registerNewUser(uName, uEmail, uMobile, uPassword,shipmentAddress);
            }
        });
        defaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingView.isShown())
                    shippingView.setVisibility(View.GONE);
                else
                    shippingView.setVisibility(View.VISIBLE);
            }
        });

        return v;
    }

    private void registerNewUser(final String name, final String email, final String mobile, final String password, final String address)
    {
        String urlReg="http://rjtmobile.com/ansari/fos/fosapp/fos_reg.php?&user_name="+name
                +"&user_email="+email+"&user_phone="+mobile+"&user_password="+password+"&user_add="+address;
        StringRequest strReq = new StringRequest(Request.Method.GET, urlReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, response);
                if(response.contains("successfully registered"))
                {
                    Toast.makeText(getActivity(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                    // store register data
                    SharedPreferences setting=getActivity().getSharedPreferences("Food@Door",MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putString("loginBy","Registration");
                    editor.putString("name", name);
                    editor.putString("email",email);
                    editor.putString("mobile",mobile);
                    editor.putString("password",password);
                    editor.putString("address",address);
                    editor.apply();
                    // store user data
                    UserInfo userInfo = AppController.getInstance().getmUserInfo();
                    userInfo.setUserName(name);
                    userInfo.setUserEmail(email);
                    userInfo.setUserPhone(mobile);
                    UserAddress uAddress = AppController.getInstance().getmUserAddress();
                    uAddress.setDefaultAddress(address);
                    // go back to login
                    LoginFragment fragment = new LoginFragment();
                    FragmentManager fm=getFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    ft.replace(R.id.activity_login,fragment).commit();
                }
                else if(response.contains("Mobile Number already exsist"))
                {
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Attention!");
                    alertDialog.setMessage("Mobile Number already exsist!\nPlease login or try a new mobile number.");
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
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addRequestQueue(strReq);
    }


    private void shippingLayoutDeatils(final View shippingView) {

        Button confirmAddress;
        streetLayout = (TextInputLayout) shippingView.findViewById(R.id.streetAddressLayout);
        aptorSuiteLayout=(TextInputLayout) shippingView.findViewById(R.id.aptNoLayout);;
        cityLayout=(TextInputLayout) shippingView.findViewById(R.id.cityLayout);
        zipcodeLayout = (TextInputLayout) shippingView.findViewById(R.id.zipCodeLayout);
        shipmentstreet = (TextInputEditText) shippingView.findViewById(R.id.streetAddress);
        shipmentApt = (TextInputEditText) shippingView.findViewById(R.id.aptorSuit);
        shipmentCity= (TextInputEditText) shippingView.findViewById(R.id.shippingCity);
        shipmentZipcode = (TextInputEditText) shippingView.findViewById(R.id.shippingZipcode);
        shipmentState= (AppCompatSpinner) shippingView.findViewById(R.id.shippingState);
        shipmentCountry =(AppCompatSpinner) shippingView.findViewById(R.id.shippingCountry);
        confirmAddress = (Button) shippingView.findViewById(R.id.shippingAddressConfirm);

        ArrayAdapter<CharSequence> stateAdapter =ArrayAdapter.createFromResource(getContext(), R.array.states, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> countryAdapter =ArrayAdapter.createFromResource(getContext(), R.array.countries, android.R.layout.simple_spinner_dropdown_item);

        shipmentState.setAdapter(stateAdapter);
        shipmentCountry.setAdapter(countryAdapter);
        shipmentCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        shipmentState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shippingState = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        confirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!error()) {
                    String apt = "";
                    if (shipmentApt.getText() != null)
                        apt = shipmentApt.getText().toString();
                    shipmentAddress = "Address:" + shipmentstreet.getText().toString() + "\t"
                            + apt + "\n City:" + shipmentCity.getText().toString() + ", St:" + shippingState
                            + "\n country:" + country + " zipcode:" + shipmentZipcode.getText().toString();
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(shipmentAddress, 2);
                        AppController.getInstance().getmUserAddress().setDefaultUserAddressObj(addressList.get(0));
                        addressEnterd=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    shippingView.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean error() {
        if(shipmentstreet.getText().toString().isEmpty() || shipmentCity.getText().toString().isEmpty()
                || shipmentZipcode.getText().toString().isEmpty() ){
            if(shipmentstreet.getText().toString().isEmpty())
            {
                streetLayout.setError("please enter Street address");
            }
            if(shipmentCity.getText().toString().isEmpty())
            {
                aptorSuiteLayout.setError("please enter City");
            }
            if(shipmentZipcode.getText().toString().isEmpty())
            {
                zipcodeLayout.setError("please enter zipcode");
            }
            return true;
        }
        return false;
    }

}
