package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.MainActivity;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.payment.Ledger;
import com.inspirado.kuber.ecom.payment.Payment;
import com.inspirado.kuber.ecom.store.Store;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PaymentFragment extends Fragment implements PaymentResultWithDataListener {
    Order order;
    User user;
    Ledger storeOwnerLedger;
    View parentLayout;
    private Store store;

    public void setOrder(Order order){
       this.order=order;
   }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_order_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Checkout.preload(getContext());
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(json, User.class);

    //    setContentView(R.layout.activity_ecom_order_payment);
        parentLayout = getActivity().findViewById(android.R.id.content);
        showHidePaymentOptions();        
        Button paymentBtn = (Button) getActivity().findViewById(R.id.ecom_order_new_4_btn);
        RadioGroup paymentMethodGrp = (RadioGroup) getActivity().findViewById(R.id.payment_method);
       // order = (Order)getIntent().getSerializableExtra("order");
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton checkedOption = getActivity().findViewById(paymentMethodGrp.getCheckedRadioButtonId());
                String checkedOptionText = checkedOption.getText()+"";
                if(checkedOptionText.toLowerCase().startsWith("credit card")){
                    startRazorPayment();
                }else if(checkedOptionText.toLowerCase().startsWith("cash on delivery")){
                    startCODPayment();
                } else if(checkedOptionText.toLowerCase().startsWith("pay merchant directly")){
                    startDirectPayment();
                }
            }
        });

    }

    private void showHidePaymentOptions() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching payment options ..");
        progressDialog.show();
        Long ledgerId = order.getSeller().getSellerLedgerId();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/ledgers/" + ledgerId, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    storeOwnerLedger = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(response.toString(), Ledger.class);
                    if(storeOwnerLedger.isAcceptsCOD() && getActivity().findViewById(R.id.cod)!=null){
                        getActivity().findViewById(R.id.cod).setVisibility(View.VISIBLE);
                    }else{
                        getActivity().findViewById(R.id.cod).setVisibility(View.GONE);
                    }
                    if(storeOwnerLedger.isAcceptsDirectPayment()){
                        getActivity().findViewById(R.id.direct).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.directInstructions).setVisibility(View.VISIBLE);
                        ((TextView)getActivity().findViewById(R.id.directInstructions)).setText(storeOwnerLedger.getDirectPaymentNotes());
                    }else{
                        getActivity().findViewById(R.id.direct).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.directInstructions).setVisibility(View.GONE);

                    }
                    if(storeOwnerLedger.isAcceptsPaymentViaPlatform() && (storeOwnerLedger.getKycStatus()==3)){
                        getActivity().findViewById(R.id.razor).setVisibility(View.VISIBLE);
                    }else{
                        getActivity().findViewById(R.id.razor).setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String UIMessage = "System Exception";
                    Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPaymentSuccess(String s, PaymentData paymentData) {

        order.setExtAttr2Name("razorPaymentId");
        order.setExtAttr2Value(paymentData.getPaymentId());
        order.setExtAttr3Name("razorSignature");
        order.setExtAttr3Value(paymentData.getSignature());
        order.setMop(4);
        // update order status to payment success
        // request details page// update status to 3 ..with detaial from razor pay // update order status // verify signature
        updateOrderForPayment(4);
      //  createPayment(payment);
    }

    public void onPaymentError(int i, String s, PaymentData paymentData) {
        order.setExtAttr2Name("razorPaymentId");
        order.setExtAttr2Value(paymentData.getPaymentId());
        order.setExtAttr3Name("razorSignature");
        order.setExtAttr2Value(paymentData.getSignature());
        order.setMop(4);

        updateOrderForPayment(400);
        TextView newOrder4msg = (TextView) getActivity().findViewById(R.id.newOrder4msg);
        newOrder4msg.setText("Payment failed. Please try again");
    }


    public void startRazorPayment() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        order.setMop(4);
        JSONObject postData=null;
        try {
             postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/orders/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                Checkout checkout = new Checkout();
                try {
                    order= (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orderObj.toString(), Order.class);
                    JSONObject options = new JSONObject();
                    options.put("name", order.getSeller().getSellerName());
                    options.put("description", order.getBuyer().getBuyerName()+". "+order.getShippingAddress());
                    options.put("currency", "INR");
                    options.put("amount", order.getGrossAmount());
                 //   options.put("image", order.getGrossAmount());
                    options.put("order_id", order.getExtAttr1Value());
                    options.put("prefill.name", order.getBuyer().getBuyerName());
                    options.put("prefill.contact", order.getBuyer().getBuyerMobileNumber());
                    options.put("theme.color", "#FFEC4F");
                    checkout.open(getActivity(), options);
                    progressDialog.dismiss();
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting Razorpay Checkout", e);
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public void updateOrderForPayment( int status) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        order.setStatus(status);
        JSONObject postData=null;
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/orders/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orderObj.toString(), Order.class);
                    Payment payment= new Payment();
                    if(order.getStatus()==401){
                        payment.setStatus(401);
                    }else{
                        payment.setStatus(2);
                    }
                    payment.setLedgerId(order.getSeller().getSellerLedgerId());
                    payment.setAmount(order.getGrossAmount());
                    payment.setMop(4);
                    payment.setOrgChain("/"+user.getClientCode());
                    payment.setExtAttr1Value(order.getExtAttr1Value());
                    payment.setExtAttr2Value(order.getExtAttr2Value());
                    payment.setExtAttr3Value(order.getExtAttr3Value());
                    payment.setExtAttr1Name(order.getExtAttr1Name());
                    payment.setExtAttr2Name(order.getExtAttr2Name());
                    payment.setExtAttr3Name(order.getExtAttr3Name());
                    payment.setComments("Razorpay settlement amount for ledger id"+ storeOwnerLedger.getDisplayId() +" Name: "+ order.getSeller().getSellerName() + " phone: "+ order.getSeller().getSellerMobileNumber() + "  received from "+ user.getId() +" "+user.getName() +" Phone" + user.getMobileNumber() );
                    payment.setPaymentDate(new Date());
                    progressDialog.dismiss();
                    createPayment(payment);
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting Razorpay Checkout", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public void createPayment(Payment payment) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        JSONObject postData=null;
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(payment));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/payments/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    Fragment fragment = new OrderDetailsFragment();
                    ((OrderDetailsFragment) fragment).setOrder(order);
                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment,"orderDetailsFragment").addToBackStack(null);
                    ft.commit();
                    progressDialog.dismiss();
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting Razorpay Checkout", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    public void startCODPayment() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        order.setMop(5);
        JSONObject postData=null;
        order.setStatus(3);
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/orders/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orderObj.toString(), Order.class);
                    Payment payment= new Payment();
                    payment.setStatus(1);
                    payment.setLedgerId(order.getSeller().getSellerLedgerId());
                    payment.setAmount(order.getGrossAmount());
                    payment.setMop(5);
                    payment.setOrgChain("/"+user.getClientCode());
                    payment.setComments("COD committed by "+ user.getName() + " phone "+ user.getMobileNumber() + " to : Store: "+ order.getSeller().getSellerName() + " phone: "+ order.getSeller().getSellerMobileNumber() );
                    payment.setPaymentDate(new Date());
                    payment.setRefType(1);
                    payment.setRefNo(order.getId()+"");
                    progressDialog.dismiss();
                    createPayment(payment);
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting COD payment", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public void startDirectPayment() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        order.setMop(6);
        JSONObject postData=null;
        order.setStatus(3);
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/orders/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orderObj.toString(), Order.class);
                    Payment payment= new Payment();
                    payment.setStatus(1);
                    payment.setLedgerId(order.getSeller().getSellerLedgerId());
                    payment.setAmount(order.getGrossAmount());
                    payment.setMop(6);
                    payment.setOrgChain("/"+user.getClientCode());
                    payment.setRefNo(order.getId()+"");
                    payment.setRefType(1);
                    payment.setComments("Direct payment by "+ user.getName() + " Phone "+ user.getMobileNumber() + " to : Store: "+ order.getSeller().getSellerName() + " phone: "+ order.getSeller().getSellerMobileNumber() );
                    payment.setPaymentDate(new Date());
                    progressDialog.dismiss();
                    createPayment(payment);
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting Direct payment", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


}

