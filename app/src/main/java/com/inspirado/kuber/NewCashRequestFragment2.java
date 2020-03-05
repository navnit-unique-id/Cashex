package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class NewCashRequestFragment2 extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;

    User user;
    Hashtable lenders = new Hashtable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_cash_req_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle("Nearby Lenders ..");
       // adapter = new LenderItemAdapter(getContext(), lenders, user);
        showLenders();
        Button nextBtn = (Button) getActivity().findViewById(R.id.newCashReq2Btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    Fragment fragment = new NewCashRequestFragment3();
                    ((NewCashRequestFragment3) fragment).setUser(user);
                    ((NewCashRequestFragment3) fragment).setLenders(lenders);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button newCashReq1ViewListBtn = (Button) getActivity().findViewById(R.id.newCashReq1ViewMapBtn);
        newCashReq1ViewListBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    Fragment fragment = new NewCashRequestFragment1();
                    ((NewCashRequestFragment1) fragment).setUser(user);
                    ((NewCashRequestFragment1) fragment).setLenders(lenders);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Hashtable getLenders() {
        return lenders;
    }

    public void setLenders(Hashtable lenders) {
        this.lenders = lenders;
    }

    private void showLenders() {
        mList = getActivity().findViewById(R.id.ic_lender_list);
        adapter = new LenderItemAdapter(getContext(), lenders, user);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);
        final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.ic_lender_list);
        if(lenders.isEmpty()){
            getData(false, 1);
        }else{
            ((LenderItemAdapter) adapter).setLenders(lenders);
        }
        ((LenderItemAdapter) adapter).setLenders(lenders);
        adapter.notifyDataSetChanged();
        if(lenders.isEmpty()){
            list.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
        }

    }

    public void getData(boolean silent, int type) {
        String resource = "";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Fetching nearby lenders...");
       // final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.ic_cash_list);
        if (!silent) {
            progressDialog.show();
        }
        lenders.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url)+"/100/"+clientCode+"/cashrequest" + "/users/" + user.getId() + "/nearbylenders" , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        User lender = (new Gson()).fromJson(jsonObject.toString(), User.class);
                        lenders.put(lender.getId(), lender);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
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
                Snackbar snackbar = Snackbar
                        .make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    private void showNoRecord(){
        RecyclerView noList = (RecyclerView) getActivity().findViewById(R.id.ic_cash_list);
        noList.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.not_found));
    }

    private String getStringFromJson(JSONObject jsonObject, String key) {
        String result = null;
        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
        }
        return result;
    }


}

