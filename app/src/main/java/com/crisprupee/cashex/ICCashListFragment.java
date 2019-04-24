package com.crisprupee.cashex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Belal on 18/09/16.
 */


public class ICCashListFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ic_cash_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        showICList();
    }


    private void showICList(){
        //  setContentView(R.layout.fragment_ic_cash_list);
        TabLayout tabLayout=(TabLayout)getActivity().findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Cash Request"));
        tabLayout.addTab(tabLayout.newTab().setText("Accepted Request"));
     //   tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mList = getActivity().findViewById(R.id.ic_cash_list);
        cashRequests = new ArrayList<>();
        adapter = new RequestListAdapter(getContext(), cashRequests);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);
        getData(false,1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==1){
                    getData(false,1);
                } if (tab.getPosition()==2){
                    getData(false,3);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void getData(boolean silent, int type) {
        String resource="";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Cash Requests...");
        if(!silent){
            progressDialog.show();
        }
        cashRequests.clear();
        //   JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/rest/devices?username="+user.getEmail(), new Response.Listener<JSONArray>() {
        if(type==1){
            resource="CashRequests";
        }
        if(type==2){
            resource="CashRequests";
        }
        if(type==3){
            resource="CashRequests";
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/" +resource+"?username="+"estelarconsultancy@gmail.com", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CashRequest cashRequest  = new CashRequest();
                        cashRequest.setAmount(Double.parseDouble(getStringFromJson(jsonObject, "amount")));
                        cashRequest.setId(Long.parseLong(getStringFromJson(jsonObject, "id")));
                        try{
                            cashRequest.setRequestDate((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")).parse(getStringFromJson(jsonObject, "requestDate")));
                        }catch (Exception e){
                        }
                        try{
                            cashRequest.setAcceptanceDate((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")).parse(getStringFromJson(jsonObject, "acceptanceDate")));
                        }catch (Exception e){
                        }
                        try{
                            cashRequest.setCompletionDate((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")).parse(getStringFromJson(jsonObject, "completionDate")));
                        }catch (Exception e){
                        }


                        //   cashRequest.setLenderDistance(Double.parseDouble(getStringFromJson(jsonObject, "lenderDistance")));
                        //   cashRequest.setLndrTransactionId(getStringFromJson(jsonObject, "lndrTransactionId"));
                        cashRequest.setPayableAmout(Double.parseDouble(getStringFromJson(jsonObject, "payableAmout")));
                        cashRequest.setLndrPaymentMode(getStringFromJson(jsonObject, "lndrPaymentMode"));
                        cashRequest.setLndrTransactionId(getStringFromJson(jsonObject, "lndrTransactionId"));
                        cashRequest.setPaymentSlot(getStringFromJson(jsonObject, "paymentSlot"));
                        cashRequest.setPreferredPaymentMode(getStringFromJson(jsonObject, "preferredPaymentMode"));
                        //   cashRequest.setRcvTransactionId(getStringFromJson(jsonObject, "rcvTransactionId"));
                        cashRequest.setRequesterId(Long.parseLong(getStringFromJson(jsonObject, "requesterId")));
                        cashRequest.setStatus(Integer.parseInt(getStringFromJson(jsonObject, "status")));
                        cashRequests.add(cashRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                ((RequestListAdapter) adapter).setRequests(cashRequests);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
                toast.show();
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
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

