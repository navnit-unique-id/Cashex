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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by Belal on 18/09/16.
 */


public class OGCashListFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;
    ProgressDialog progressDialog ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_og_cash_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle("My Requests");
        showOGList();
        showFloatingButton();
        IntentFilter filter = new IntentFilter("2");
        filter.addAction("3");
        filter.addAction("4");
        filter.addAction("11");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);

    }

    private void showFloatingButton(){
        FloatingActionButton fab = getActivity().findViewById(R.id.addCashRequest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NewCashRequestFragment1();
                ((NewCashRequestFragment1) fragment).setUser(user);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);;
                ft.commit();
            }
        });
    }
    private void showOGList( ){
        mList = getActivity().findViewById(R.id.og_cash_requests);
        cashRequests = new ArrayList<>();
        adapter = new RequestListAdapter(getContext(), cashRequests, user);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);
        progressDialog = new ProgressDialog(getContext());
        getData(false,3);

/*        FloatingActionButton fab = getActivity().findViewById(R.id.addCashRequest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NewCashRequestFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();

           }
        });*/
    }

    public void getData(final boolean  silent, int type) {
        String resource="";
      //  final ProgressDialog progressDialog =null;
        final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.og_cash_requests);
        String clientCode = user.getClientCode();
        if(!silent){
        //    final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Fetching Cash Requests...");
            progressDialog.show();
        }
        cashRequests.clear();
        //   JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/rest/devices?username="+user.getEmail(), new Response.Listener<JSONArray>() {
        if(type==1){
            resource="OGCashRequests/generated";
        }
        if(type==2){
            resource="OGCashRequests/accepted";
        }
        if(type==3){
            resource="OGCashRequests/myrequests";
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/cashrequest"+ "/" +resource+"?id="+user.getId(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              //  RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.og_cash_requests);
                if(list!=null)
                    list.setBackground(null);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CashRequest cashRequest = (new Gson()).fromJson(jsonObject.toString(), CashRequest.class);
                        cashRequests.add(cashRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(!silent){
                            progressDialog.dismiss();
                        }
                    }
                }
                ((RequestListAdapter) adapter).setRequests(cashRequests);
                ((RequestListAdapter) adapter).setUser(user);
                adapter.notifyDataSetChanged();
                if((response.length()==0) && (list!=null)){
                    list.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
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
            //    Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
             //   toast.show();
                Snackbar
                        .make(getView(), UIMessage, Snackbar.LENGTH_LONG).show();


                if(!silent){
                    progressDialog.dismiss();
                }
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void ShowIntro() {
        FloatingActionButton addButton = (FloatingActionButton) getActivity().findViewById(R.id.addCashRequest);
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(addButton)
                .setDismissText("GOT IT")
                .setMaskColour(Color.argb(150, 0, 0, 0))
                .setContentText("No Active Requests. Click this button to raise a new Cash Request")
                //    .setDelay(5000) // optional but starting animations immediately in onCreate can make them choppy
                //     .singleUse(1) // provide a unique ID used to ensure it is only shown once
                .show();
    }
    private String getStringFromJson(JSONObject jsonObject, String key) {
        String result = null;
        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
        }
        return result;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");
            if(isAdded()){
                getData(true,3);
            }
        }
    };

}

