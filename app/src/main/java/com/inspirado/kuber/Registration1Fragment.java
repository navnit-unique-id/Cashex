package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Belal on 18/09/16.
 */


public class Registration1Fragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.registration1_title);
        Button nextBtn = (Button) getActivity().findViewById(R.id.next1);
        EditText userName = (EditText) getActivity().findViewById(R.id.username2);
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    User user = new User();
                    final EditText userName = ((EditText) v);
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Checking Username...");
                    progressDialog.show();
                    JsonObjectRequest jsonObjectRequest = null;

                    try {
                        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/users?username=" + userName.getText().toString(), null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject responseObj) {
                                        try {
                                            if(responseObj!=null){
                                                userName.setError("Username already taken !!");
                                            }
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
                                        if(error.networkResponse.statusCode==404){

                                        }else{
                                          //  Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean cancel = validate();
                if (cancel) {
                    return;
                }

                User user = new User();
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";

                try {
                    user.setUsername(((EditText) getActivity().findViewById(R.id.username2)).getText().toString());
                    user.setPassword(((EditText) getActivity().findViewById(R.id.password)).getText().toString());
                    user.setMobileNumber(((EditText) getActivity().findViewById(R.id.username2)).getText().toString());
                    user.setStatus(1);
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new Registration2Fragment();
                                        ((Registration2Fragment) fragment).setUser((new Gson()).fromJson(responseObj.toString(), User.class));
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.registration_frame, fragment);
                                        ft.commit();
                                        Util.updateSharedPref(getContext().getSharedPreferences("pref", 0), responseObj);
                                    } catch (Exception e) {
                                      //  Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                   // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Snackbar
                                            .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
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

            private boolean validate() {
                boolean cancel = false;
                EditText username = (EditText) getActivity().findViewById(R.id.username2);
                EditText password = (EditText) getActivity().findViewById(R.id.password);
            //    EditText retypePassword = (EditText) getActivity().findViewById(R.id.passwordRetype);
             //   EditText mobileNumber = (EditText) getActivity().findViewById(R.id.mobileNumber);

                if (username.getText().toString().equalsIgnoreCase("")) {
                    username.setError(getResources().getString(R.string.registration1_error_username_blank));
                    cancel = true;
                }
                if (password.getText().toString().equalsIgnoreCase("")) {
                    password.setError(getResources().getString(R.string.error_invalid_password));
                    cancel = true;
                }
               /* if (!password.getText().toString().equalsIgnoreCase(retypePassword.getText().toString())) {
                    retypePassword.setError(getResources().getString(R.string.registration1_error_password_notMatch));
                    cancel = true;
                }
                if (mobileNumber.getText().toString().equalsIgnoreCase("")) {
                    mobileNumber.setError(getResources().getString(R.string.registration1_error_mobile_invalid));
                    cancel = true;
                }*/
                if ((username.getError()!=null) ||(password.getError()!=null)) {
                    cancel = true;
                }
                return cancel;
            }

        });

    }


}

