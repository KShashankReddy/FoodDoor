package com.example.shashankreddy.fooddoor.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shashankreddy.fooddoor.LoginActivity;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.utils.PasswordValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ResetFragment extends Fragment {

    private final String TAG = "Reset Password";
    TextInputEditText oldPswd, newPswd, confirmPswd;
    Button submitBtn;
    String mobile, oldP, newP, confirmP;

    PasswordValidator validator;

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
        View v=inflater.inflate(R.layout.fragment_reset,container,false);

        mobile = AppController.getInstance().getmUserInfo().getUserPhone();
        if(mobile==null)
        {
            getPhoneNumber();
        }

        oldPswd = (TextInputEditText) v.findViewById(R.id.oldPassword);
        newPswd = (TextInputEditText) v.findViewById(R.id.newPassword);
        confirmPswd = (TextInputEditText) v.findViewById(R.id.confirmPassword);
        submitBtn = (Button) v.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                oldP=oldPswd.getText().toString();
                newP=newPswd.getText().toString();
                confirmP=confirmPswd.getText().toString();
                if(!oldP.isEmpty() && !newP.isEmpty() && !confirmP.isEmpty())
                {
                    if(validator.validate(oldP) && validator.validate(newP) && validator.validate(confirmP))
                    {
                        if(newP.equals(confirmP))
                        {
                            checkPassword(mobile, oldP, newP);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Inconsistent new password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        if(!validator.validate(oldP))
                            oldPswd.setError(validator.PASSWORD_DISCRIPTION);
                        if(!validator.validate(newP))
                            newPswd.setError(validator.PASSWORD_DISCRIPTION);
                        if(!validator.validate(confirmP))
                            confirmPswd.setError(validator.PASSWORD_DISCRIPTION);
                    }
                }
                else
                {
                    if(oldP.isEmpty())
                    {
                        oldPswd.setError("Please enter the provisional password");
                    }
                    if(newP.isEmpty())
                    {
                        newPswd.setError("Please enter the new password");
                    }
                    if(confirmP.isEmpty())
                    {
                        confirmPswd.setError("Please enter the confirm new password");
                    }
                }
            }
        });

        return v;
    }

    private void checkPassword(String mobile, String oldP, String newP)
    {
        String urlReset="http://rjtmobile.com/ansari/fos/fosapp/fos_reset_pass.php?&user_phone="+mobile+"&user_password="+oldP+"&newpassword="+newP;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlReset, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, response.toString());
                try
                {
                    JSONArray array = response.getJSONArray("msg");
                    String message=array.getString(0);
                    if(message.equals("password reset successfully"))
                    {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else if(message.equals("old password mismatch"))
                    {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Attention");
                        alertDialog.setMessage("Old password mismatch!\nPlease try again!");
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
                    else if(message.equals("wrong mobile number"))
                    {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Attention");
                        alertDialog.setMessage("Wrong mobile number!");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                                getPhoneNumber();
                            }
                        });
                        AlertDialog ad=alertDialog.create();
                        ad.show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addRequestQueue(req);
    }

    public void getPhoneNumber()
    {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_get_mobile);
        final EditText phoneText = (EditText) dialog.findViewById(R.id.login_phone);
        Button saveBtn = (Button) dialog.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = phoneText.getText().toString();
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
