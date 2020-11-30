package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
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
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.cash.CashRequest;
import com.inspirado.kuber.cash.CashRequestListAdapter;
import com.inspirado.kuber.cash.NewCashRequestFragment1;
import com.inspirado.kuber.ecom.order.NewOrderFragment1;
import com.inspirado.kuber.ecom.order.Order;
import com.inspirado.kuber.ecom.order.OrderOutgoingListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class OGCashListFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    ArrayList orders = new ArrayList();
    User user;
    ProgressDialog progressDialog;
    boolean isFABOpen = false;
    String path = "cashrequest";
    String reqFilter = "generated";
    String queryString = "";
    String reqType = "";
    ArrayList orderFilters = new ArrayList();
    ArrayList cashFilter = new ArrayList();
    TabLayout.Tab selectedTab;// cash, order
    Order order =null;

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
        user = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(json, User.class);
        getActivity().setTitle("My Requests");
     //   createFilterButtons();
        showTabs();
      //  showOGList();
        showFloatingButton();
        getData(false);
        IntentFilter filter = new IntentFilter("2");
        filter.addAction("3");
        filter.addAction("4");
        filter.addAction("11");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
    }

    private void showFloatingButton() {
        FloatingActionButton addRequestBtn = (FloatingActionButton) getActivity().findViewById(R.id.addRequestBtn);
        FloatingActionButton addCashRequestBtn = (FloatingActionButton) getActivity().findViewById(R.id.addCashRequestBtn);
        FloatingActionButton addOrderRequestBtn = (FloatingActionButton) getActivity().findViewById(R.id.addOrderRequestBtn);
        addRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }

            private void showFABMenu() {
                isFABOpen = true;
                addCashRequestBtn.show();
                addOrderRequestBtn.show();
                addRequestBtn.animate().rotation(90);
                addCashRequestBtn.animate().translationY(-getResources().getDimension(R.dimen.fab_cash_y)).alpha(1.0f);
                ;
                addOrderRequestBtn.animate().translationY(-getResources().getDimension(R.dimen.fab_order_y)).alpha(1.0f);
                ;
            }

            private void closeFABMenu() {
                isFABOpen = false;
                addRequestBtn.animate().rotation(-90);
                addCashRequestBtn.animate().translationY(28).alpha(0.0f);
                addOrderRequestBtn.animate().translationY(28).alpha(0.0f);
                addCashRequestBtn.hide();
                addOrderRequestBtn.hide();
            }
        });

        addCashRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NewCashRequestFragment1();
                ((NewCashRequestFragment1) fragment).setUser(user);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                ft.commit();
            }
        });

        addOrderRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUnratedCompleteRequests();
            }
        });
    }

    public void checkUnratedCompleteRequests() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Getting orders ...");
        String query = "?status=1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders/buyer/" + user.getId()+"/unratedcomplete" + query,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray orderArr = null;
                orderArr=response;
                if(orderArr.length()>0){
                    askBuyer4Rating(orderArr);
                }else{
                    Fragment fragment = new NewOrderFragment1();
                    ((NewOrderFragment1) fragment).setUser(user);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
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
                Snackbar snackbar = Snackbar.make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void askBuyer4Rating(JSONArray orders) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.OGOuterLayout);

        try {
            if(orders.length()>0){
                order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(orders.get(0).toString(), Order.class);
            }
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_ecom_order_confirm, null);
            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            ((TextView) customView.findViewById(R.id.rateText)).setText("Please rate your experience with "+ order.getSeller().getSellerName());

            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();
            CheckBox codRcvd = ((CheckBox) customView.findViewById(R.id.codConfirmChkBox));
            codRcvd.setVisibility(View.GONE);

            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.ratingBar);
                    TextView feedbackLabel = (TextView) customView.findViewById(R.id.rateText);
                    String rcvrFeedback = ((EditText) customView.findViewById(R.id.feedbackText)).getText().toString();
                    float rating = ratingBar.getRating();
                    if ((rating + "").equalsIgnoreCase("0")) {
                        feedbackLabel.requestFocus();
                        feedbackLabel.setError("Please rate the experience");
                        return;
                    }
                    order.setSellerScore(rating);
                    order.setSellerFeedback(rcvrFeedback);
                    order.setPaymentStatus(2);
                    order.setStatus(7);
                    saveOrder();
                    popupWindow.dismiss();
                }
            });
            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Saving ...");
        progressDialog.show();
        JSONObject postData = null;
        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(order));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/orders", postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(responseObj.toString(), Order.class);
                        checkUnratedCompleteRequests();
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

   /* private void createFilterButtons() {
        ArrayList orderFiltersText = new ArrayList<>();
        orderFiltersText.add("Active");
        orderFiltersText.add("Complete");
        ArrayList cashFiltersText = new ArrayList<>();
        cashFiltersText.add("myrequests");
        cashFiltersText.add("Accepted");
        String filter;
        ToggleButton filterBtn;
        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.categoryLayout);
        for (int i = 0; i < orderFiltersText.size(); i++) {
            filter = orderFiltersText.get(i) + "";
            filterBtn = new ToggleButton(getActivity());
            filterBtn.setText(filter);
            filterBtn.setTextOn(filter);
            filterBtn.setTextOff(filter);
            filterBtn.setBackground(getActivity().getDrawable(R.drawable.stylishbutton));
            filterBtn.setTextSize(12);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
            filterBtn.setPadding(0, 0, 0, 0);
            filterBtn.setVisibility(View.GONE);
            ll.addView(filterBtn, lp);
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll.getTouchables().stream().forEach(button -> ((ToggleButton) button).setChecked(false));
                    ((ToggleButton) view).setChecked(true);
                    reqFilter = ((ToggleButton) view).getText() + "";
                    getData(false);
                }
            });
            orderFilters.add(filterBtn);
        }

        for (int i = 0; i < cashFiltersText.size(); i++) {
            filter = cashFiltersText.get(i) + "";
            filterBtn = new ToggleButton(getActivity());
            filterBtn.setText(filter);
            filterBtn.setTextOn(filter);
            filterBtn.setTextOff(filter);
            filterBtn.setBackground(getActivity().getDrawable(R.drawable.stylishbutton));
            filterBtn.setTextSize(12);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);
            filterBtn.setPadding(0, 0, 0, 0);
            filterBtn.setVisibility(View.GONE);
            ll.addView(filterBtn, lp);
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll.getTouchables().stream().forEach(button -> ((ToggleButton) button).setChecked(false));
                    ((ToggleButton) view).setChecked(true);
                    reqFilter = ((ToggleButton) view).getText() + "";
                    getData(false);
                }
            });
            cashFilter.add(filterBtn);
        }
    }*/

    private void showFilterButtons() {
        if (selectedTab.getText().toString().equalsIgnoreCase("cash")) {
            for (int i = 0; i < cashFilter.size(); i++) {
                ToggleButton btn = ((ToggleButton) cashFilter.get(i));
                btn.setVisibility(View.VISIBLE);
                if (i == 0) btn.setChecked(true);
            }
            for (int i = 0; i < orderFilters.size(); i++) {
                ((ToggleButton) orderFilters.get(i)).setVisibility(View.GONE);
            }
        }
        if (selectedTab.getText().toString().equalsIgnoreCase("orders")) {
            for (int i = 0; i < cashFilter.size(); i++) {
                ((ToggleButton) cashFilter.get(i)).setVisibility(View.GONE);
            }
            for (int i = 0; i < orderFilters.size(); i++) {
                ToggleButton btn = ((ToggleButton) orderFilters.get(i));
                btn.setVisibility(View.VISIBLE);
                if (i == 0) btn.setChecked(true);
            }
        }
    }


    private void showTabs() {
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Cash"));
        tabLayout.addTab(tabLayout.newTab().setText("Orders"));
        mList = getActivity().findViewById(R.id.og_cash_requests);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String clientCode = user.getClientCode();
                if (tab.getPosition() == 0) {
                    path = "/100/" + clientCode + "/" + "cashrequest" + "/OGCashRequests/";
                    queryString = "?id=" + user.getId();
                    reqType = "cashrequest";
                    reqFilter = "myrequests";
                    selectedTab = tab;

                }
                if (tab.getPosition() == 1) {
                    path = "/100/" + clientCode + "/orders/buyer/"+user.getId()+"/" ;
                    queryString = "";
                    reqType = "order";
                    reqFilter = "active";
                    selectedTab = tab;

                }
                showFilterButtons();
                getData(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        selectedTab = tabLayout.getTabAt(0);
        path = "/100/" + user.getClientCode() + "/" + "cashrequest" + "/OGCashRequests/";
        queryString = "?id=" + user.getId();
        reqType = "cashrequest";
        reqFilter = "myrequests";
      //  showFilterButtons();
    }


    public void getData(boolean silent) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Requests...");
        if (!silent) {
            progressDialog.show();
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + path + reqFilter.toLowerCase() + queryString, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                transformAndPopulate(response);
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    private void transformAndPopulate(JSONArray response) {
        final RecyclerView list = (RecyclerView) getActivity().findViewById(R.id.og_cash_requests);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        if ((response.length() == 0) && (list != null)) {
            list.setBackground(getContext().getResources().getDrawable(R.drawable.not_found));
            // return;
        }else{
            list.setBackground(null);        }

        if (reqType.equalsIgnoreCase("cashrequest")) {
            adapter = new CashRequestListAdapter(getContext(), cashRequests, user);
            mList.setAdapter(adapter);
            cashRequests.clear();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    CashRequest cashRequest = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(jsonObject.toString(), CashRequest.class);
                    cashRequests.add(cashRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((CashRequestListAdapter) adapter).setRequests(cashRequests);
            adapter.notifyDataSetChanged();
        } else if (reqType.equalsIgnoreCase("order")) {
            adapter = new OrderOutgoingListAdapter(getContext(), orders, user);
            mList.setAdapter(adapter);
            orders.clear();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    Order order = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(jsonObject.toString(), Order.class);
                    orders.add(order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((OrderOutgoingListAdapter) adapter).setOrders(orders);
            adapter.notifyDataSetChanged();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");
            if (isAdded()) {
                getData(true);
            }
        }
    };

}

