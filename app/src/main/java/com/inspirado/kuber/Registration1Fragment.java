package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

//import android.widget.Toast;

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
    ProgressDialog progressDialog = null;

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

        progressDialog = new ProgressDialog(getContext());


        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean cancel = validate();
                if (cancel) {
                    return;
                }

                User user = new User();
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                String clientCode = ((EditText) getActivity().findViewById(R.id.clientcode)).getText() + "";
                clientCode = clientCode.equalsIgnoreCase("") ? "51" : clientCode;

                try {
                    user.setUsername(((EditText) getActivity().findViewById(R.id.username2)).getText().toString());
                    user.setPassword(((EditText) getActivity().findViewById(R.id.password)).getText().toString());
                    user.setMobileNumber(((EditText) getActivity().findViewById(R.id.username2)).getText().toString());
                    user.setClientCode(clientCode);
                    user.setStatus(2);
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/cashrequest" + "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new Registration3Fragment();
                                        ((Registration3Fragment) fragment).setUser((new Gson()).fromJson(responseObj.toString(), User.class));
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.registration_frame, fragment).addToBackStack(null);
                                        ;
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
                                    String message = "";
                                    try {
                                        message = new String(error.networkResponse.data, "UTF-8");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (error.networkResponse.statusCode == 409) {
                                        Snackbar.make(getView(), "User name already taken !!", Snackbar.LENGTH_LONG).show();
                                    } else if ((error.networkResponse.statusCode == 400)) {
                                        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                                    } else if ((error.networkResponse.statusCode == 500) || ((error.networkResponse.statusCode == 502))) {
                                        Snackbar.make(getView(), "System Error.", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
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
                if ((username.getError() != null) || (password.getError() != null)) {
                    cancel = true;
                }
                return cancel;
            }

        });
    }


        /*public void checkUserExists(){
                User user = new User();
                EditText userName = (EditText) getActivity().findViewById(R.id.username2);
                progressDialog.setMessage("Checking Username...");
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String clientCode = ((EditText) getActivity().findViewById(R.id.clientcode)).getText()+"";

            try {
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/cashrequest"+ "/users?username=" + userName.getText().toString(), null,
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
                                                .make(getActivity().findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error.networkResponse==null){
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    if(error.networkResponse.statusCode==404){

                                    }else{
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
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
    }*/

    @Override
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }
}

