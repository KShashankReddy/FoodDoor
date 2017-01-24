package com.example.shashankreddy.fooddoor.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shashank reddy on 1/19/2017.
 */
public class OrderTrackFragment extends Fragment {

    private String orderId;
    private String TAG = "Track Order";
    String id, cost, date;
    int status;
    TextView OrderId, TotalCost, OrderDate, OrderStatus;
    ImageView StatusImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        orderId=bundle.getString("id");
        String url = "http://rjtmobile.com/ansari/fos/fosapp/order_track.php?&order_id="+orderId;
        sendStringRequest(url);
    }

    private void sendStringRequest(String url) {
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                if(response.equals("Incorrect order Number"))
                {
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Attention");
                    alertDialog.setMessage("Incorrect order number!");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            FragmentManager fm = getFragmentManager();
                            OrdersFragment fragment = new OrdersFragment();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fragment_replaceable,fragment).commit();
                        }
                    });
                    AlertDialog ad=alertDialog.create();
                    ad.show();
                }
                else
                {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray result = obj.getJSONArray("Order Detail");
                        JSONObject order = result.getJSONObject(0);
                        id = order.getString("OrderId");
                        cost = order.getString("TotalOrder");
                        status = Integer.parseInt(order.getString("OrderStatus"));
                        date = order.getString("OrderDate");
                        setText();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addRequestQueue(req);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_track, container, false);
        // get View
        OrderId = (TextView) v.findViewById(R.id.tOrderId);
        TotalCost = (TextView) v.findViewById(R.id.tOrderCost);
        OrderDate = (TextView) v.findViewById(R.id.tOrderDate);
        OrderStatus = (TextView) v.findViewById(R.id.status);
        StatusImage = (ImageView) v.findViewById(R.id.statusImage);
        setText();
        return v;
    }

    private void setText() {
        // set data
        String t1 = "Order #"+id;
        String t2 = "$ "+cost;
        String t3 = "Date: "+date;
        OrderId.setText(t1);
        TotalCost.setText(t2);
        OrderDate.setText(t3);
        switch (status)
        {
            case 1:
                OrderStatus.setText("Packing");
                StatusImage.setImageResource(R.drawable.image_packing);
                break;
            case 2:
                OrderStatus.setText("On the way");
                StatusImage.setImageResource(R.drawable.image_on_the_way);
                break;
            case 3:
                OrderStatus.setText("Delivered");
                StatusImage.setImageResource(R.drawable.image_delivered);
                break;
            default:
                break;
        }
    }
}

