package com.example.shashankreddy.fooddoor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.example.shashankreddy.fooddoor.models.FoodItemList;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class PaymentFragment extends Fragment {
    private TextView totalItems,totalPrice,responseMessage,responseMessage1,userDeliveryAddress;
    private  View orderView,orderPlaceLayoutInclude,returnView;
    Boolean paypalShiping = false;


    CardView backToCart,finalConformation;
    private static PayPalConfiguration mPayPalConfiguration ;
    private PayPalPayment itemsOrdered;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "Af37zdFx5_HMrPJqu09rjgCIqg4rfQRfVd9VztHEuQZr0-7vQhtjwfKPoI_zYDS5nlBO-ea_I3f1XWVZ";

    private static final int REQUEST_CODE_PAYMENT = 1;


    //private static PayPalConfiguration config = new PayPalConfiguration().environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID);


    PayPalPayment thingToBuy;
    String name;
    int final_price;
    private static final String TAG = "Checkout";

    public PaymentFragment(){
        mPayPalConfiguration = new PayPalConfiguration().
                environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId(CONFIG_CLIENT_ID)
                .merchantName("Food@Door");
    }

    @Override
    public void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // start service
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        returnView = inflater.inflate(R.layout.fragment_payment, container, false);
        totalItems = (TextView) returnView.findViewById(R.id.payment_total_items);
        totalPrice = (TextView) returnView.findViewById(R.id.payment_total_price);
        orderPlaceLayoutInclude = returnView.findViewById(R.id.orderPlaceLayoutInclude);
        orderView = returnView.findViewById(R.id.orderLayout);
        CardView paymentFinalConformationAddress = (CardView) returnView.findViewById(R.id.payment_final_conformation);
        backToCart = (CardView) returnView.findViewById(R.id.back_to_cart);
        finalConformation = (CardView) returnView.findViewById(R.id.final_check_out);
        userDeliveryAddress = (TextView) returnView.findViewById(R.id.user_delivery_location);
        userDeliveryAddress.setText(AppController.getInstance().getmUserAddress().getDeliveryAddress().getAddressLine(0));
        paymentFinalConformationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_replaceable, AddressMapFragment.newInstance("PaymentFragment")).commit();
            }
        });
        /*if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Payment");
        }*/

        getTotalValueOfCart();

        Log.d(TAG, "above back to cart");
        backToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d(TAG, "on click of back to cart");
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_replaceable, new CheckOutFragment()).commit();
                    fragmentTransaction.addToBackStack(null);
            }
        });
        Log.d(TAG, "above conformaton");
        finalConformation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG,"conformation clicked");
                itemsOrdered = orderStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
                if (paypalShiping)
                    itemsOrdered.enablePayPalShippingAddressesRetrieval(true);
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, itemsOrdered);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            }
        });
        Log.d(TAG,"onView created");
        return returnView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null)
                {
                    confirmOrderWith(confirm);
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                System.out.println("The order is canceled.");
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            {
                System.out.println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void registerOrder()
    {
        // use OrderRequest to place order
    }

    @Override
    public void onDestroy()
    {
        // Stop service when done
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }
    private PayPalPayment orderStuffToBuy(String paymentIntentSale) {
        ArrayList<FoodItemList> cartItemList = AppController.getInstance().getmCartItems().getCartItemsList();
        HashMap<FoodItemList, Integer> quantityMap = AppController.getInstance().getmCartItems().getItemQuantity();
        PayPalItem[] items = new PayPalItem[cartItemList.size()];
        for(int i =0; i<cartItemList.size();i++) {
            int quantity = 0;
            double price = (Double.parseDouble(cartItemList.get(i).getFoodPrice()))/67.5;
            if(quantityMap.containsKey(cartItemList.get(i)))
                quantity = quantityMap.get(cartItemList.get(i));
            items[i] = new PayPalItem(cartItemList.get(i).getFoodName(),quantity,new BigDecimal(price),"USD",cartItemList.get(i).getFoodId());
        }

        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("10.00");

        PayPalPaymentDetails payPalPaymentDetails = new PayPalPaymentDetails(subtotal,shipping,new BigDecimal("0.00"));
        BigDecimal amount = subtotal.add(shipping);
        PayPalPayment payment = new PayPalPayment(amount,"USD","OnlineBazzar",paymentIntentSale);
        payment.items(items).paymentDetails(payPalPaymentDetails);
        payment.custom("please contact 000 on fraud,have a good day");
        return payment;
    }
    private void getTotalValueOfCart() {
        double count = 0;
        int items = 0;
        ArrayList<FoodItemList> cartItems = AppController.getInstance().getmCartItems().getCartItemsList();
        for (FoodItemList selectedItem:cartItems){
            int quantity = AppController.getInstance().getmCartItems().getItemQuantity().get(selectedItem);
            items += quantity;
            count = count+quantity*Double.parseDouble(selectedItem.getFoodPrice());
        }
        totalItems.setText("Items :" + items);
        totalPrice.setText("Total : Rs" + count);
    }

    private void confirmOrderWith(final PaymentConfirmation confirmation) {
        ArrayList<FoodItemList> cartItemList = AppController.getInstance().getmCartItems().getCartItemsList();
        HashMap<FoodItemList, Integer> quantityMap = AppController.getInstance().getmCartItems().getItemQuantity();
        String itemCat ="";
        String itemNames="";
        final String item_cat;
        final String item_name;
        int quantity = 0;
        double count = 0;
        Date da = new Date();

        ArrayList<FoodItemList> cartItems = AppController.getInstance().getmCartItems().getCartItemsList();
        for (FoodItemList selectedItem:cartItems){
            itemCat +=selectedItem.getFoodCat()+",";
            itemNames += selectedItem.getFoodName().replaceAll("\\s+", "/")+",";
            quantity += AppController.getInstance().getmCartItems().getItemQuantity().get(selectedItem);
            count = count+quantity*Double.parseDouble(selectedItem.getFoodPrice());
        }

        item_cat = itemCat;
        item_name = itemNames;
        final String items = quantity+"";
        final String total = count+"";
        String addressLine = AppController.getInstance().getmUserAddress().getDeliveryAddress()
                .getAddressLine(0)+" "+AppController.getInstance().
                getmUserAddress().getDeliveryAddress().getAddressLine(1);

        addressLine= addressLine.replaceAll("\\s+", "/");
        String date = null;
        try {
             date = confirmation.toJSONObject().getJSONObject("response").getString("create_time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String time = date;
        String orderIdURL= "http://rjtmobile.com/ansari/fos/fosapp/order_request.php?&order_category="+item_cat+"&" +
        "order_name="+item_name+"&order_quantity="+quantity+"&total_order="+total+"&" +
                "order_delivery_add="+addressLine
                +"&order_date="+time+"&user_phone="+AppController.getInstance().getmUserInfo().getUserPhone();


        StringRequest orderPlaced = new StringRequest(Request.Method.GET, orderIdURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(PaymentFragment.class.getSimpleName(),response.toString());
                Log.d(PaymentFragment.class.getSimpleName(),response);
                        //orderId = orderIds.getJSONObject(0).getString("OrderId");
                String orderId = response.replace("order confirmed. order id is : ","");
                setConformation(confirmation,orderId);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(PaymentFragment.class.getSimpleName(),error.getMessage());
            }
        });
        Log.d(PaymentFragment.class.getSimpleName(),orderPlaced.getUrl());
        AppController.getInstance().addRequestQueue(orderPlaced);
    }

    private void setConformation(PaymentConfirmation confirmation, String orderId) {

        try {

            JSONObject conformationObject = confirmation.toJSONObject().getJSONObject("response");
            JSONObject paymentConformation = confirmation.getPayment().toJSONObject();
            Log.d(TAG,conformationObject.toString(4)+" test "+paymentConformation.toString(4));
            String state= conformationObject.getString("state");
            String time = conformationObject.getString("create_time");
            double amount = Double.parseDouble(paymentConformation.getString("amount"));
            amount = Math.round(amount);
            if(state.equals("approved")){
                orderPlaceLayoutInclude.setVisibility(View.VISIBLE);
                backToCart.setVisibility(View.GONE);
                finalConformation.setVisibility(View.GONE);
                orderView.setVisibility(View.GONE);

                TextView orderid = (TextView) orderPlaceLayoutInclude.findViewById(R.id.confirm_orderid);
                TextView date = (TextView) orderPlaceLayoutInclude.findViewById(R.id.confirm_order_date);
                TextView total = (TextView) orderPlaceLayoutInclude.findViewById(R.id.confirm_order_total);

                orderid.setText(orderId);
                date.setText(time);
                total.setText(amount+"");
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
