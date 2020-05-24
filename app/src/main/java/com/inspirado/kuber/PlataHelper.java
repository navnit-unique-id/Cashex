package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.cash.CashRequest;
import com.inspirado.kuber.ecom.store.Store;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class PlataHelper {
    private final Context context;
    private View view;
    User user;
    final ProgressDialog progressDialog;
    protected String clientCode="";

    public PlataHelper(Context context, View view){
        this.context = context;
        this.view=view;
        progressDialog = new ProgressDialog(context);
    }
    public void bootStrap() {
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        clientCode = user.getClientCode();
    }

    public void showProgressBar(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressBar(){
        progressDialog.dismiss();
    }

    public void handleErrorScheme1(String errorMessage){
        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
        dismissProgressBar();
    }

    public void handleRetry(JsonRequest jsonRequest){
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonRequest);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

}

