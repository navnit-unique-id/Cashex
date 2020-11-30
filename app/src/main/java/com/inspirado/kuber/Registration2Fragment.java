package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.cash.CashRequest;
import com.inspirado.kuber.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Belal on 18/09/16.
 */


public class Registration2Fragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.registration2_title);

        Button nextBtn = (Button) getActivity().findViewById(R.id.verifyBtn);
        ((TextView) getActivity().findViewById(R.id.otpPhone)).setText(user.getMobileNumber() + "");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getContext().getString(R.string.registration2_progressbar_msg));
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    user.setOtp(((EditText) getActivity().findViewById(R.id.otp)).getText().toString());
                    user.setStatus(2);
                    String clientCode=user.getClientCode();
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                       jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/cashrequest"
                            + "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new Registration3Fragment();
                                        ((Registration3Fragment) fragment).setUser((new Gson()).fromJson(responseObj.toString(), User.class));
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                   //     fragment.getMapAsync(getActivity());
                                        ft.replace(R.id.registration_frame, fragment).addToBackStack(null);;
                                        ft.commit();
                                        Util.updateSharedPref(getContext().getSharedPreferences("pref", 0), responseObj);
                                    } catch (Exception e) {
                                       // Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if ((error.networkResponse!=null)&& (error.networkResponse.statusCode == 401) ) {
                                        ((EditText) getActivity().findViewById(R.id.otp)).setError("Incorrect or expired OTP");
                                    } else {
                                        //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        Snackbar
                                                .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }
        });

        Button resendOTP = (Button) getActivity().findViewById(R.id.resendOTP);
        resendOTP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getContext().getString(R.string.registration2_progressbar_resendotp_msg));
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    user.setOtp(((EditText) getActivity().findViewById(R.id.otp)).getText().toString());
                    user.setStatus(1);
                    Gson gson = new Gson();
                    String clientCode=user.getClientCode();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/cashrequest"+ "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                       // Toast.makeText(getContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();
                                        Snackbar
                                                .make(getView(), "OTP resent successfully", Snackbar.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error.getClass().toString().equalsIgnoreCase("com.android.volley.TimeoutError")){
                                       // Toast.makeText(getContext(), "Problem with Internet Connection", Toast.LENGTH_SHORT).show();
                                        Snackbar
                                                .make(getView(), "Problem with Internet Connection", Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }
        });


    }


}

