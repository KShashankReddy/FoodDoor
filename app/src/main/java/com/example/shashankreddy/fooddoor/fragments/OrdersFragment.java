package com.example.shashankreddy.fooddoor.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.adapters.OrderAdapter;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {


    private static String TAG = "Order";
    private ArrayList<OrderItem> list;
    TextInputEditText searchText;
    ImageButton searchBtn;
    CardView trackCard;
    TextView orderIdText, totalOrderText, orderStatusText, orderDateText;
    RecyclerView recyclerView;
    OrderAdapter adapter;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<OrderItem>();
        String mobile = AppController.getInstance().getmUserInfo().getUserPhone();
        sendJsonRequest(mobile);
    }

    private void sendJsonRequest(final String mobile) {
        String url = "http://rjtmobile.com/ansari/fos/fosapp/order_recent.php?&user_phone="+"55565454";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, response.toString());
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addRequestQueue(req);
    }

    private void parseJsonResponse(JSONObject response) {
        try {
            JSONArray result = response.getJSONArray("Order Detail");
            for(int i=0; i<result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                OrderItem item = new OrderItem();
                item.setOrderId(obj.getString("OrderId"));
                item.setOrderName(obj.getString("OrderName"));
                item.setOrderQuantity(obj.getString("OrderQuantity"));
                item.setTotalCost(obj.getString("TotalOrder"));
                String status=getStatus(obj.getString("OrderStatus"));
                item.setOrderStatus(status);
                item.setDeliverAdd(obj.getString("OrderDeliverAdd"));
                item.setOrderDate(obj.getString("OrderDate"));
                list.add(i, item);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStatus(String orderStatus) {
        String status = "";
        switch (orderStatus)
        {
            case "1":
                status = "Packing";
                break;
            case "2":
                status = "On the way";
                break;
            case "3":
                status = "Delivered";
                break;
            default:
                break;
        }
        return status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);
        // search Order by ID
        searchText = (TextInputEditText) v.findViewById(R.id.searchText);
        searchBtn = (ImageButton) v.findViewById(R.id.searchButton);
        trackCard = (CardView) v.findViewById(R.id.trackingCard);
        orderIdText = (TextView) v.findViewById(R.id.orderIdText);
        totalOrderText = (TextView) v.findViewById(R.id.totalOrderText);
        orderStatusText = (TextView) v.findViewById(R.id.orderStatusText);
        orderDateText = (TextView) v.findViewById(R.id.orderDateText);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                trackCard.setVisibility(View.GONE);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = searchText.getText().toString();
                if(!orderId.equals("") || !orderId.isEmpty())
                {
                    sendStringRequest(orderId);
                }
            }
        });
        // show Recent Orders
        recyclerView = (RecyclerView) v.findViewById(R.id.orderRecyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrderAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void sendStringRequest(final String orderId) {
        String url = "http://rjtmobile.com/ansari/fos/fosapp/order_track.php?&order_id="+orderId;
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
                        orderIdText.setText(order.getString("OrderId"));
                        totalOrderText.setText(order.getString("TotalOrder"));
                        orderStatusText.setText(getStatus(order.getString("OrderStatus")));
                        orderDateText.setText(order.getString("OrderDate"));
                        trackCard.setVisibility(View.VISIBLE);
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

}
