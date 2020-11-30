package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.order.cart.CartItemListAdapter;
import com.inspirado.kuber.ecom.order.inventory.Inventory;
import com.inspirado.kuber.ecom.order.inventory.InventoryListAdapter;
import com.inspirado.kuber.ecom.store.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NewOrderFragment3 extends Fragment {

    Inventory inventoryItem;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private InventoryListAdapter inventoryListAdapter;
    private RecyclerView.Adapter cartItemAdapter;
    List<Inventory> inventoryItemsAll = new ArrayList<Inventory>();
    List<Inventory> inventoryItems = new ArrayList<Inventory>();
    View cartView;

    User user;
    Store store;
    Order order = new Order();
    private String shipmentAddress;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_order_inventory_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle(store.getName());
        mList = getActivity().findViewById(R.id.ic_inventory_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        inventoryListAdapter = new InventoryListAdapter(getContext(), inventoryItems, user);
        ((InventoryListAdapter) inventoryListAdapter).setOrder(order);
        mList.setAdapter(inventoryListAdapter);

        showAddressDetails();
        showBuyerDetails();
        showCategories();
        showCart();
        showOrderButton();
        buildSearch();
        getData(false, null, "1");
        fetchDraftOrders();
        IntentFilter filter = new IntentFilter("order-message");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(orderMessageReciever, filter);

        IntentFilter filter1 = new IntentFilter("cart-message");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mCartMessageReceiver, filter1);

        IntentFilter filter2 = new IntentFilter("address-message");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAddressMessageReceiver, filter2);

        IntentFilter filter3 = new IntentFilter("order-server-message");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mServerOrderReceiver, filter3);

    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(orderMessageReciever);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mCartMessageReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAddressMessageReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mServerOrderReceiver);
    }
    private void showOrderButton() {
        Button newOrderBtn = (Button) getActivity().findViewById(R.id.newOrderBtn);
        newOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidToOrder())return;
                order.setStatus(2);
                saveOrder();
            }
        });

    }

    public void buildSearch() {

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) getActivity().findViewById(R.id.action_search);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
//        getActivity().getActionBar().setCustomView(getActivity().findViewById(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inventoryListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                inventoryListAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    private void showAddressDetails() {
        Button changeAddressBtn = (Button) getActivity().findViewById(R.id.changeAddressBtn);
        TextView addressTxtView = (TextView) getActivity().findViewById(R.id.address);
        String add = order.getShippingAddress();
        if (add == null) {
            add = user.getAddress();
        }
        if (add != null) addressTxtView.setText(add + "");
        shipmentAddress = add;

        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow;
                ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.ecom_order_3_layout);
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addressView = layoutInflater.inflate(R.layout.popup_ecom_order_address, null);
                popupWindow = new PopupWindow(addressView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                TextView address = addressView.findViewById(R.id.addressTxt);
                String add = order.getShippingAddress();
                if (add == null) {
                    add = user.getAddress();
                }
                address.setText(add + "");
                popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
                popupWindow.update();
                Button closePopupBtn = (Button) addressView.findViewById(R.id.closePopupBtn);
                closePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                Button savePopupBtn = (Button) addressView.findViewById(R.id.savePopupBtn);
                savePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String add = address.getText() + "";
                        order.setShippingAddress(address.getText() + "");
                        Intent intent = new Intent("address-message");
                        intent.putExtra("address", add);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    private void showCart() {
        Button summaryBtn = (Button) getActivity().findViewById(R.id.summaryBtn);
        summaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow;
                ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.ecom_order_3_layout);
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cartView = layoutInflater.inflate(R.layout.popup_ecom_order_cart, null);
                popupWindow = new PopupWindow(cartView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                RecyclerView oList = cartView.findViewById(R.id.ic_order_item_list);
                linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                oList.setHasFixedSize(true);
                oList.setLayoutManager(linearLayoutManager);
                cartItemAdapter = new CartItemListAdapter(getContext(), inventoryItems, user);
                ((CartItemListAdapter) cartItemAdapter).setOrder(order);
                oList.setAdapter(cartItemAdapter);
                cartItemAdapter.notifyDataSetChanged();
                ((TextView)cartView.findViewById(R.id.total)).setText(order.getTotalAmount()+"");
                ((TextView)cartView.findViewById(R.id.deliveryCharge)).setText(order.getDeliveryCharge()+"");
                ((TextView)cartView.findViewById(R.id.gross)).setText(order.getGrossAmount()+"");

                popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
                popupWindow.update();
                Button closePopupBtn = (Button) cartView.findViewById(R.id.closePopupBtn);
                closePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

    }

    private void showBuyerDetails() {
        TextView storeLine1 = (TextView) getActivity().findViewById(R.id.address);
        storeLine1.setText("Delivery address: " + user.getAddress());
    }


    private void showCategories() {
        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.categoryLayout);
        ToggleButton allBtn = (ToggleButton) getActivity().findViewById(R.id.allBtn);
        allBtn.setChecked(true);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll.getTouchables().stream().forEach(button -> ((ToggleButton) button).setChecked(false));
                ((ToggleButton) view).setChecked(true);
                getDataLocal(null);
            }
        });

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/inventories/stores/" + store.getId() + "/categories", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray categories) {
                try {
                    for (int j = 0; j < categories.length(); j++) {
                        try {
                            JSONObject category = categories.getJSONObject(j);
                            ToggleButton categoryBtn = new ToggleButton(getActivity());
                            categoryBtn.setText(category.get("category") + " (" + category.get("count") + ")");
                            categoryBtn.setTextOn(category.get("category") + " (" + category.get("count") + ")");
                            categoryBtn.setTextOff(category.get("category") + " (" + category.get("count") + ")");
                            categoryBtn.setBackground(getActivity().getDrawable(R.drawable.stylishbutton));
                            categoryBtn.setTextSize(12);
                            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80);
                            categoryBtn.setPadding(0, 0, 0, 0);
                            // categoryBtn.setHeight(30);
                            ll.addView(categoryBtn, lp);

                            categoryBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ll.getTouchables().stream().forEach(button -> ((ToggleButton) button).setChecked(false));
                                    String value = null;
                                    try {
                                        value = category == null ? null : category.get("category") + "";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ((ToggleButton) view).setChecked(true);
                                    getDataLocal(value);
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

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void getDataLocal(String category) {
        if (category == null) {
            this.inventoryItems = inventoryItemsAll;
        } else {
            this.inventoryItems = this.inventoryItemsAll.stream().filter(inventory -> {
                if (inventory.getCategory().equalsIgnoreCase(category)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        ((InventoryListAdapter) inventoryListAdapter).setRequests(inventoryItems);
        //  ((InventoryListAdapter) adapter).setOrder(new Order());
        inventoryListAdapter.notifyDataSetChanged();

    }

    private void transformAndPopulateAll(JSONArray response) {
        if ((response.length() == 0) && (mList != null)) {
            mList.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
        }
        inventoryItemsAll = inventoryItems;
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
        ((InventoryListAdapter) inventoryListAdapter).setRequests(inventoryItems);
    //    ((InventoryListAdapter) inventoryListAdapter).setOrder(order);
        inventoryListAdapter.notifyDataSetChanged();
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
        if (category != null) {
            if (query.equalsIgnoreCase("")) {
                query = "?" + "category=" + category;
            } else {
                query = query + "&" + "category=" + category;
            }
        }
        if (status != null) {
            if (query.equalsIgnoreCase("")) {
                query = "?" + "status=" + status;
            } else {
                query = query + "&" + "status=" + status;
            }
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/inventories/stores/" + store.getId() + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray storeArr = null;
                try {
                    storeArr = (response.getJSONArray("content"));
                    transformAndPopulateAll(storeArr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error getting inventory items";
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

    public void fetchDraftOrders() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Getting orders ...");
        String query = "?status=1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders/buyer/" + user.getId()+"/draft" + query,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray orderArr = null;
                  //  orderArr = (response.getJSONArray("content"));
                    orderArr=response;
                    processExistingDraftOrders(orderArr);

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error fetching draft orders";
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

    private void processExistingDraftOrders(JSONArray orderArr) {
        if (orderArr.length() == 0) {
            createNewOrder();
        } else {
            try {
                Order order = (new Gson()).fromJson(orderArr.get(0).toString(), Order.class);
                if (!order.getSeller().getSellerSourceId().equals(store.getId())) {
                    final PopupWindow popupWindow;
                    ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.ecom_order_3_layout);
                    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View customView = layoutInflater.inflate(R.layout.popup_ecom_order_cart_another_store, null);
                    TextView messageTxtView = (TextView) customView.findViewById(R.id.messageTxtView);
                    messageTxtView.setText("Your cart has items from the store " + store.getName() + ". Do you want to discard items and start afresh?");
                    popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    Button yesBtn = (Button) customView.findViewById(R.id.yesBtn);
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Seller seller = new Seller();
                            seller.setOwnerId(store.getOwnerId());
                            seller.setSellerSourceId(store.getId());
                            seller.setSellerAddress(store.getAddress());
                            seller.setSellerName(store.getName());
                            seller.setSellerLat(store.getLat());
                            seller.setSellerLng(store.getLng());
                            seller.setSellerCity(store.getCity());
                            seller.setSellerState(store.getState());
                            seller.setSellerEmail(store.getEmail());
                            seller.setSellerLedgerId(store.getOwnerLedgerId());
                            seller.setSellerMobileNumber(store.getPhone());
                            order.setSeller(seller);
                            order.setOrderItems(null);
                            order.setDeliveryCharge(store.getDeliveryCharge());
                            order.calculateSummary();
                            publishOrderFromServer(order);
                        }
                    });
                    Button noBtn = (Button) customView.findViewById(R.id.noBtn);
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Fragment fragment = new NewOrderFragment1();
                            FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                            ft.commit();
                        }
                    });
                } else {
                    publishOrderFromServer(order);
                }
                // check if seller is the same..if not show message if he wants to discard...if no...go back to teh listing page..if yes, update the seller
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishOrderFromServer(Order order) {
        Intent intent = new Intent("order-server-message");
        intent.putExtra("order", order);
        order.setDeliveryCharge(store.getDeliveryCharge());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void createNewOrder() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Creating new order ...");
        Order order = new Order();
        Buyer buyer = new Buyer();
        buyer.setBuyerSourceId(user.getId());
        buyer.setBuyerAddress(user.getAddress());
        buyer.setBuyerName(user.getName());
        buyer.setBuyerLat(user.getLat());
        buyer.setBuyerLng(user.getLng());
        buyer.setBuyerCity(user.getCity());
        buyer.setBuyerState(user.getState());
        buyer.setBuyerEmail(user.getEmail());
        buyer.setBuyerMobileNumber(user.getMobileNumber());
        Seller seller = new Seller();
      //  seller.setSellerSourceId(store.getId());
        seller.setSellerAddress(store.getAddress());
        seller.setSellerName(store.getName());
        seller.setSellerLat(store.getLat());
        seller.setSellerLng(store.getLng());
        seller.setSellerCity(store.getCity());
        seller.setSellerState(store.getState());
        seller.setSellerEmail(store.getEmail());
        seller.setSellerMobileNumber(store.getPhone());
        seller.setOwnerId(store.getOwnerId());
        seller.setSellerLedgerId(store.getOwnerLedgerId());
        seller.setSellerSourceId(store.getId());
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setShippingAddress(shipmentAddress);
        order.setShippingLat(store.getRefLat());
        order.setShippingLng(store.getRefLng());
        order.setOrgChain("/" + user.getClientCode());
        order.setDeliveryCharge(store.getDeliveryCharge());
        order.setStatus(1);
        JSONObject postData = null;
        try {
            postData = new JSONObject(new Gson().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders", postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        Order order = (new Gson()).fromJson(responseObj.toString(), Order.class);
                        publishOrderFromServer(order);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Could not create new draft order";
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

    private void saveOrder() {
        if(!isValid()){
          return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Saving ...");
        progressDialog.show();
        JSONObject postData = null;
        try {
            order.setDateOfOrder(new Date());
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders", postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        Order order = (new Gson()).fromJson(responseObj.toString(), Order.class);
                        publishOrderFromServer(order);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Could not save order";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                if(getView()!=null){
                    Snackbar snackbar = Snackbar.make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                progressDialog.dismiss();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isValid() {
        if(order.getId()==null) {
            Snackbar.make(getView(), "Oops!!! Let's retry ..", Snackbar.LENGTH_LONG).show();
            fetchDraftOrders();
            return false;
        }
      //  Snackbar.make(getView(), "Request is OK. id = " + order.getId(), Snackbar.LENGTH_LONG).show();
        return true;
    }

    private boolean isValidToOrder() {
        if(order.getId()==null) {
            Snackbar.make(getView(), "ID is null", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(  (order.getOrderItems()==null) || (order.getOrderItems()!=null && order.getOrderItems().size()<1)){
            Snackbar.make(getView(), "Cart is empty", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private BroadcastReceiver orderMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Order orderFromInv = (Order) intent.getSerializableExtra("order");
            if (getActivity() != null) {
                Button summaryBtn = (Button) getActivity().findViewById(R.id.summaryBtn);
                summaryBtn.setText("Rs " + orderFromInv.getGrossAmount());
                shipmentAddress = orderFromInv.getShippingAddress();
                order.setShippingAddress(shipmentAddress);
                order.setOrderItems(orderFromInv.getOrderItems());
                saveOrder();
            }
        }
    };

    private BroadcastReceiver mServerOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order orderSrv = (Order) intent.getSerializableExtra("order");
            if ((orderSrv==null)|| (orderSrv!=null && orderSrv.getId()==null) ) return; //dont process junk data
            if(orderSrv!=null) {
                if (orderSrv.getOrderItems() != null) {
                    for (OrderItem orderItem : orderSrv.getOrderItems()) {
                        if (order != null && order.getOrderItems() != null) {
                            order.getOrderItems().forEach(orderItem1 -> {
                                if (orderItem1.getId() != null && orderItem1.getId().equals(orderItem.getId())) {
                                    orderItem.setMaxQuantity(orderItem1.getMaxQuantity());
                                }
                            });
                        }
                    }
                }
                order = orderSrv; //above is in a bid to retain the maxQuantity which is not sent to server
            }

           if (getActivity() != null) {
                if (order.getStatus() == 1) {
                    Button summaryBtn = (Button) getActivity().findViewById(R.id.summaryBtn);
                    summaryBtn.setText("Rs " + order.getGrossAmount());
                    if(order.getShippingAddress()!=null && !order.getShippingAddress().equalsIgnoreCase("")){

                        TextView addressTxtView = (TextView) getActivity().findViewById(R.id.address);
                        addressTxtView.setText(order.getShippingAddress() + "");
                        shipmentAddress = order.getShippingAddress();
                    }
                    ((InventoryListAdapter) inventoryListAdapter).setOrder(order);
                    inventoryListAdapter.notifyDataSetChanged();
                } else if (order.getStatus() == 2) {
                    Fragment fragment = new PaymentFragment();
                    ((PaymentFragment) fragment).setOrder(order);
                    ((PaymentFragment) fragment).setStore(store);

                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment,"paymentFragment").addToBackStack(null);
                    ft.commit();

                   /* Intent myIntent = new Intent(context, PaymentActivity.class);
                    myIntent.putExtra("order", order);
                    startActivity(myIntent);*/
                }
            }
           getActivity().setTitle(order.getId()+" - " +store.getName());
        }
    };

    private BroadcastReceiver mCartMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            order = (Order) intent.getSerializableExtra("order");
            if (getActivity() != null) {
                Button summaryBtn = (Button) getActivity().findViewById(R.id.summaryBtn);
                summaryBtn.setText("Rs " + order.getGrossAmount());
                ((InventoryListAdapter) inventoryListAdapter).setOrder(order);
                inventoryListAdapter.notifyDataSetChanged();
                ((TextView)cartView.findViewById(R.id.total)).setText(order.getTotalAmount()+"");
                ((TextView)cartView.findViewById(R.id.deliveryCharge)).setText(order.getDeliveryCharge()+"");
                ((TextView)cartView.findViewById(R.id.gross)).setText(order.getGrossAmount()+"");
            }
            saveOrder();
        }
    };

    private BroadcastReceiver mAddressMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address") + "";
            TextView addressTxtView = (TextView) getActivity().findViewById(R.id.address);
            addressTxtView.setText(address + "");
            shipmentAddress = address;
            order.setShippingAddress(shipmentAddress);
            saveOrder();
        }
    };
}

