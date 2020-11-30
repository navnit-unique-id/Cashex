package com.inspirado.kuber.ecom.order.inventory;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreInventoryListFragment extends Fragment {

    Inventory inventoryItem;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList inventoryItems = new ArrayList();
    User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_store_inventory_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle("Inventory List");
        mList = getActivity().findViewById(R.id.ic_inventory_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        adapter = new InventoryListAdapter(getContext(), inventoryItems, user);
        mList.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ((InventoryListAdapter.RequestHolder)viewHolder).destroy();
                toggleState(((InventoryListAdapter.RequestHolder)viewHolder).getInventory());
            }
        });

        itemTouchHelper.attachToRecyclerView(mList);


        showFloatingButton();
        showCategories();
        getData(false, null,"1");
        IntentFilter filter = new IntentFilter("1");
        filter.addAction("4");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
    }

    private void showFloatingButton() {
        FloatingActionButton addInventoryItemBtn = (FloatingActionButton) getActivity().findViewById(R.id.addInventoryItemBtn);
        addInventoryItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new StoreInventoryDetailsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void showCategories() {
        LinearLayout ll = (LinearLayout)getActivity().findViewById(R.id.categoryLayout);
        ToggleButton allBtn = (ToggleButton)getActivity().findViewById(R.id.allBtn);
        allBtn.setChecked(true);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll.getTouchables().stream().forEach(button -> ((ToggleButton)button).setChecked(false));
                ((ToggleButton) view).setChecked(true);
                getData(false, null,"1");
            }
        });
        ToggleButton inactiveBtn = (ToggleButton)getActivity().findViewById(R.id.inactiveBtn);
        inactiveBtn.setChecked(false);
        inactiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll.getTouchables().stream().forEach(button -> ((ToggleButton)button).setChecked(false));
                ((ToggleButton) view).setChecked(true);
                getData(false, null,"2");
            }
        });
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/inventories/orgs/" + user.getId()+"/categories", new Response.Listener<JSONArray>() {
          @Override
            public void onResponse(JSONArray categories) {
                try {
                    for (int j = 0; j < categories.length(); j++) {
                        try {
                            JSONObject category =  categories.getJSONObject(j);
                            ToggleButton categoryBtn = new ToggleButton(getActivity());
                            categoryBtn.setText(category.get("category")+" (" +category.get("count") + ")");
                            categoryBtn.setTextOn(category.get("category")+" (" +category.get("count") + ")");
                            categoryBtn.setTextOff(category.get("category")+" (" +category.get("count") + ")");
                            categoryBtn.setBackground(getActivity().getDrawable(R.drawable.stylishbutton));
                            categoryBtn.setTextSize(12);
                            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80);
                            categoryBtn.setPadding(0,0,0,0);
                           // categoryBtn.setHeight(30);
                            ll.addView(categoryBtn, lp);

                            categoryBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ll.getTouchables().stream().forEach(button -> ((ToggleButton)button).setChecked(false));
                                    String value=null;
                                    try {
                                       value =  category==null?null:category.get("category")+"";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ((ToggleButton) view).setChecked(true);
                                     getData(false, value,"1");
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);





    }

    private void transformAndPopulate(JSONArray response) {
        if ((response.length() == 0) && (mList != null)) {
            mList.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
        }
         inventoryItems.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                Inventory inventoryItem = (new Gson()).fromJson(jsonObject.toString(), Inventory.class);
                inventoryItems.add(inventoryItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ((InventoryListAdapter) adapter).setRequests(inventoryItems);
        adapter.notifyDataSetChanged();
    }


    public void getData(boolean silent, String category, String status) {
        String resource = "";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Getting inventory items ...");
        if (!silent) {
            progressDialog.show();
        }
        String query = "";
        if(category!=null){
            if(query.equalsIgnoreCase("")){
                query="?"+"category="+category;
            }else{
                query=query+"&"+"category="+category;
            }
        }
        if(status!=null){
            if(query.equalsIgnoreCase("")){
                query="?"+"status="+status;
            }else{
                query=query+"&"+"status="+status;
            }
    }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/inventories/orgs/" + user.getId()+query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray storeArr = null;
                try {
                    storeArr = (response.getJSONArray("content"));
                    transformAndPopulate(storeArr);

                } catch (JSONException e) {
                    e.printStackTrace();
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public void toggleState(Inventory inventory) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        JSONObject postData = null;
        if(inventory.getStatus()==1){
            inventory.setStatus(2);
            progressDialog.setMessage("Deactivating item ...");
        }else{
            inventory.setStatus(1);
            progressDialog.setMessage("Reactivating item ...");
        }
       progressDialog.setMessage("Getting inventory items ...");
        try {
            postData = new JSONObject((new Gson()).toJson(inventory));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/inventories/orgs/" + user.getId(), postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

}

