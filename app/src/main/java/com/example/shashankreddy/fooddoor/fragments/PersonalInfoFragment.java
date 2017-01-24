package com.example.shashankreddy.fooddoor.fragments;


import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.LoginActivity;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.UserAddress;
import com.example.shashankreddy.fooddoor.models.UserInfo;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;



/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoFragment extends Fragment {


    TextView userName, userEmail, userMobile, userAddress;
    UserInfo mUserInfo;
    UserAddress mUserAddress;
    View shippingView;
    String shippingState;
    String country;
    String shipmentAddress;

    TextInputLayout streetLayout,aptorSuiteLayout,cityLayout,zipcodeLayout;
    TextInputEditText shipmentstreet,shipmentApt,shipmentCity,shipmentZipcode;
    AppCompatSpinner shipmentState,shipmentCountry;

    public PersonalInfoFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = AppController.getInstance().getmUserInfo();
        mUserAddress = AppController.getInstance().getmUserAddress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_info, container, false);
        // get view component
        userName = (TextView) v.findViewById(R.id.nameText);
        userEmail = (TextView) v.findViewById(R.id.emailText);
        userMobile = (TextView) v.findViewById(R.id.mobileText);
        userAddress = (TextView) v.findViewById(R.id.user_default_location);
        shippingView = v.findViewById(R.id.shippingLayout);
        CardView persoalInfoReset = (CardView) v.findViewById(R.id.personal_info_reset);
        CardView persoalInfoUpdate = (CardView) v.findViewById(R.id.personal_info_update_address);
        CardView persoalInfoLogout = (CardView) v.findViewById(R.id.personal_info_logout);
        shippingLayoutDeatils(shippingView);
        persoalInfoReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_replaceable, new ResetFragment()).commit();
            }
        });

        persoalInfoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shippingView.isShown())
                    shippingView.setVisibility(View.GONE);
                else
                    shippingView.setVisibility(View.VISIBLE);
            }
        });

        persoalInfoLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Food@Door", MODE_PRIVATE);
                String status = sharedPreferences.getString("loginBy",null);
                if(status.equals("Facebook")) {
                    // facebook logout
                    FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                if(editor.commit())
                    Toast.makeText(getActivity(), "Log out successfully", Toast.LENGTH_SHORT).show();
                // close app
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // set text
        userName.setText(mUserInfo.getUserEmail());
        userEmail.setText(mUserInfo.getUserEmail());
        userMobile.setText(mUserInfo.getUserPhone());
        userAddress.setText(mUserAddress.getDefaultUserAddressObj().getAddressLine(0));

        return v;
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
                    String shipmentAddress = "Address:" + shipmentstreet.getText().toString() + "\t"
                            + apt + "\n City:" + shipmentCity.getText().toString() + ", St:" + shippingState
                            + "\n country:" + country + " zipcode:" + shipmentZipcode.getText().toString();
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(shipmentAddress, 2);
                        AppController.getInstance().getmUserAddress().setDefaultUserAddressObj(addressList.get(0));
                        userAddress.setText(AppController.getInstance().
                                getmUserAddress().getDefaultUserAddressObj().getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateAddressRequest(shipmentAddress);
                    shippingView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateAddressRequest(String shipmentAddress) {
        String password = getActivity().getSharedPreferences("Food@Door",MODE_PRIVATE).getString("password","");
        if(password.equals(""))
            password=getPassword();
        String updateUrl = "http://rjtmobile.com/ansari/fos/fosapp/fos_update_address.php?&user_phone="
        +AppController.getInstance().getmUserInfo().getUserPhone()+"&user_password="+password +"&user_add="+shipmentAddress;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean error() {
        if(shipmentstreet.getText()==null){
            streetLayout.setError("please enter Street address");
            return true;
        }else if(shipmentCity.getText()==null){
            aptorSuiteLayout.setError("please enter City");
            return true;
        }else if(shipmentZipcode.getText()==null){
            zipcodeLayout.setError("please enter zipcode");
            return true;
        } else {
            try {
                Integer.parseInt(shipmentZipcode.getText().toString());
            }
            catch (Exception e) {
                zipcodeLayout.setError("please enter valid zipcode");
                return true;
            }
        }
        return false;
    }

    public String getPassword()
    {
        // custom dialog
        final String[] password = new String[1];
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_get_password);
        final EditText passwordText = (EditText) dialog.findViewById(R.id.login_password);
        Button saveBtn = (Button) dialog.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passwordText.getText().toString();
                password[0] = pass;
                dialog.cancel();
            }
        });
        dialog.show();
        return password[0];
    }

}
