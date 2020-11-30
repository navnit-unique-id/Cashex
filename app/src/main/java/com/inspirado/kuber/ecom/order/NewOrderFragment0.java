package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.OGCashListFragment;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 18/09/16.
 */


public class NewOrderFragment0 extends Fragment {
    public static GoogleMap mMap;
    private LinearLayoutManager linearLayoutManager;
    User user;
    Order order =null;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incoming_request_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(json, User.class);
        checkUnratedCompleteRequests();
    }

    public void checkUnratedCompleteRequests() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Getting orders ...");
        String query = "?status=1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders/buyer/" + user.getId()+"/unratedcomplete" + query,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray orderArr = null;
                orderArr=response;
                if(orderArr.length()>0){
                    askSeller4Rating(orderArr);
                }else{
                    Fragment fragment = new OGCashListFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void askSeller4Rating(JSONArray orders) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.outerLayout);

        try {
            if(orders.length()>0){
                order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orders.get(0).toString(), Order.class);
            }
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_ecom_order_confirm, null);
            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();
            CheckBox codRcvd = ((CheckBox) customView.findViewById(R.id.codConfirmChkBox));
            codRcvd.setVisibility(View.GONE);

            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.ratingBar);
                    TextView feedbackLabel = (TextView) customView.findViewById(R.id.rateText);
                    String rcvrFeedback = ((EditText) customView.findViewById(R.id.feedbackText)).getText().toString();
                    float rating = ratingBar.getRating();
                    if ((rating + "").equalsIgnoreCase("0")) {
                        feedbackLabel.requestFocus();
                        feedbackLabel.setError("Please rate the transaction");
                        return;
                    }
                    order.setSellerScore(rating);
                    order.setSellerFeedback(rcvrFeedback);
                    order.setPaymentStatus(2);
                    order.setStatus(7);
                    saveOrder();
                    popupWindow.dismiss();
                }
            });
            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Saving ...");
        progressDialog.show();
        JSONObject postData = null;
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders", postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(responseObj.toString(), Order.class);
                        checkUnratedCompleteRequests();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
}

