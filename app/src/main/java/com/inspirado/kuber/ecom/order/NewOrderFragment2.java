package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.order.inventory.InventoryListAdapter;
import com.inspirado.kuber.ecom.store.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class NewOrderFragment2 extends Fragment {

    Order storeRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;

    User user;
    Hashtable stores = new Hashtable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_order_new_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle(R.string.nearby_stores_title);
        // adapter = new StoreItemAdapter(getContext(), stores, user);
        showStores();

        ToggleButton allBtn = (ToggleButton) getActivity().findViewById(R.id.allBtn);
        ToggleButton favBtn = (ToggleButton) getActivity().findViewById(R.id.favBtn);

        allBtn.setChecked(true);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favBtn.setChecked(false);
                allBtn.setChecked(true);
                getDataLocal(null);
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favBtn.setChecked(true);
                allBtn.setChecked(false);
                getDataLocal("fav");
            }
        });


        Button nextBtn = (Button) getActivity().findViewById(R.id.newCashReq2Btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    Fragment fragment = new NewOrderFragment3();
                    ((NewOrderFragment3) fragment).setUser(user);
                    Store selectedStore = null;
                    Iterator storeItr = stores.values().iterator();
                    while (storeItr.hasNext()) {
                        Store store = (Store) storeItr.next();
                        if (store.isSelected()) {
                            selectedStore = store;
                        }
                    }
                    if (selectedStore != null) {
                        ((NewOrderFragment3) fragment).setStore(selectedStore);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                        ft.commit();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please select a store", Snackbar.LENGTH_LONG).show();
                    }
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
                    Fragment fragment = new NewOrderFragment1();
                    ((NewOrderFragment1) fragment).setUser(user);
                    ((NewOrderFragment1) fragment).setStores(stores);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDataLocal(String category) {
        Hashtable storesTemp = new Hashtable();
        if (category == null) {
            ((StoreItemAdapter) adapter).setStores(stores);
        } else {
            user.getUserPreferences().forEach(preference -> {
                if ( (preference.getType()==1)
                    && (stores.containsKey(preference.getTypeId()))
                        && ("favouritestore".equalsIgnoreCase(preference.getAttributeName()))
                        && ("true".equalsIgnoreCase(preference.getAttributValue()))){
                    storesTemp.put(preference.getTypeId(), stores.get(preference.getTypeId()));
                }
            });
            ((StoreItemAdapter) adapter).setStores(storesTemp);
        }
        adapter.notifyDataSetChanged();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Hashtable getStores() {
        return stores;
    }

    public void setStores(Hashtable stores) {
        this.stores = stores;
    }

    private void showStores() {
        mList = getActivity().findViewById(R.id.ic_store_list);
        adapter = new StoreItemAdapter(getContext(), stores, user);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);
        final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.ic_store_list);
        if (stores.isEmpty()) {
            getData(user.getLat(), user.getLng());
        } else {
            ((StoreItemAdapter) adapter).setStores(stores); //why ????
        }
        ((StoreItemAdapter) adapter).setStores(stores);
        adapter.notifyDataSetChanged();
        if (stores.isEmpty()) {
            list.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
        }

    }

    public void getData(Double lat, Double lng) {
        String resource = "";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Fetching nearby stores...");
        // final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.ic_cash_list);
        stores.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/properties/nearby?user=" + user.getId() + "&lat=" + lat + "&lng=" + lng, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        User store = (new Gson()).fromJson(jsonObject.toString(), User.class);
                        stores.put(store.getId(), store);
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


    private void showNoRecord() {
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

