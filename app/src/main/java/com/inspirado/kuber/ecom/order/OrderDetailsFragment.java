package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.payment.Payment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDetailsFragment extends Fragment {
    Order order;
    User user;
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH = 500;
    TabLayout.Tab selectedTab;
    private IntentIntegrator qrScan;
    private RecyclerView.Adapter adapter;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_order_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()).fromJson(json, User.class);
        getActivity().setTitle(R.string.order_details_title);
        if (savedInstanceState != null) {
            order = (Order) savedInstanceState.getSerializable("order");
        }
        if (order != null) {
            createScreen();
        }
        showTabs();
        mViewPager = getActivity().findViewById(R.id.viewPager);
        mList = getActivity().findViewById(R.id.ic_order_item_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemListAdapter(getContext(), order.getOrderItems(), user);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("order", (Order) order);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            order = (Order) savedInstanceState.getSerializable("order");
        }
    }


    private void showTabs() {
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Items"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mList = getActivity().findViewById(R.id.ic_order_item_list);
                String clientCode = user.getClientCode();
                if (tab.getPosition() == 0) {
                    getActivity().findViewById(R.id.ic_order_item_list).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.ecomOrderAddressLayout).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.orderStatusLayout).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.orderDetailsCard).setVisibility(View.VISIBLE);
                    mList.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 1) {
                    getActivity().findViewById(R.id.ic_order_item_list).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.ecomOrderAddressLayout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.orderStatusLayout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.orderDetailsCard).setVisibility(View.GONE);
                    mList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
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

    private void createScreen() {
        int mop = order.getMop();
        Date orderDate = order.getDateOfOrder();
        if ((order.getStatus() == 2) )
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Pending Payment");
        if ((order.getStatus() == 3) || (order.getStatus() == 4))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Waiting Acceptance");
        if ((order.getStatus() == 5))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Preparation in progress");
        if ((order.getStatus() == 6))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Despatched");
        if ((order.getStatus() == 7))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Completed");
        if ((order.getStatus() == 11))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Cancelled");
        if ((order.getStatus() == 12))
            ((TextView) getActivity().findViewById(R.id.orderStatus)).setText("Rejected");

        if (mop == 4)
            ((TextView) getActivity().findViewById(R.id.mop)).setText("Payment Gateway");
        if (mop == 5)
            ((TextView) getActivity().findViewById(R.id.mop)).setText("Cash on delivery");
        if (mop == 6)
            ((TextView) getActivity().findViewById(R.id.mop)).setText("Direct payment to store");
        //  ((TextView) getActivity().findViewById(R.id.orderStatus)).setText(order.getStatus() + "");
        ((TextView) getActivity().findViewById(R.id.ecomOrderAddress)).setText(order.getShippingAddress() + "");
        ((TextView) getActivity().findViewById(R.id.ecomOrderPhone)).setText(order.getBuyer()==null?"":order.getBuyer().getBuyerMobileNumber() + "");
        ((TextView) getActivity().findViewById(R.id.orderNumber)).setText(order.getId() + "");
        ((TextView) getActivity().findViewById(R.id.orderItems)).setText(order.getTotalQuantity()+"");
        ((TextView) getActivity().findViewById(R.id.orderValue)).setText(order.getTotalAmount() + "");
        ((TextView) getActivity().findViewById(R.id.deliveryCharge)).setText(order.getDeliveryCharge() + "");
        ((TextView) getActivity().findViewById(R.id.orderTime)).setText(orderDate == null ? "" : (new SimpleDateFormat("dd-MMM-yyyy HH:mm")).format(orderDate));
        ((TextView) getActivity().findViewById(R.id.totalLbl)).setText(order.getGrossAmount() + "");
        handleButton();
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    private void handleButton() {
        Button btn1 = ((Button) getActivity().findViewById(R.id.ecom_order_details_btn1));
        Button btn2 = ((Button) getActivity().findViewById(R.id.ecom_order_details_btn2));
        Button btn3 = ((Button) getActivity().findViewById(R.id.ecom_order_details_btn3));

        if (getUserRole().equalsIgnoreCase("seller")) {
            if ((order.status == 3) || (order.status == 4)) {
                btn1.setText("Accept");
                btn1.setVisibility(View.VISIBLE);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setStatus(5);
                        if(order.getMop()==6){
                            askSeller4DirectPayment();
                        }else{
                            saveOrder();
                        }
                    }
                });
                btn2.setText("Reject");
                btn2.setVisibility(View.VISIBLE);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setStatus(12);
                        saveOrder();
                    }
                });
                btn3.setVisibility(View.GONE);
            }

            if (order.status == 5) {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setText("Despatch");
                btn3.setVisibility(View.VISIBLE);
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setStatus(6);
                        saveOrder();
                    }
                });
            }

            if (order.status == 6) {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setText("Complete");
                btn3.setVisibility(View.VISIBLE);
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setStatus(7);
                        askSeller4RatingNCod();
                        //saveOrder();
                    }
                });
            }

            if (order.status == 7) {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
            }
            if (order.status == 12) {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
            }
        }

        if (getUserRole().equalsIgnoreCase("buyer")) {
            if ((order.status == 2) || (order.getStatus() == 400) || (order.getStatus() == 401)) {
                btn1.setText("Make Payment");
                btn1.setVisibility(View.VISIBLE);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new PaymentFragment();
                        ((PaymentFragment) fragment).setOrder(order);
                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment, "paymentFragment").addToBackStack(null);
                        ft.commit();
                    }
                });
                btn2.setText("Cancel Order");
                btn2.setVisibility(View.VISIBLE);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn2.setVisibility(View.GONE);
                        btn1.setVisibility(View.GONE);
                        order.setStatus(11);
                        saveOrder();
                    }
                });
                btn3.setVisibility(View.GONE);
            }
            if ((order.status == 4) || (order.getStatus() == 5)) {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setText("Cancel Order");
                btn3.setVisibility(View.GONE); // make this visible if cancellation is to be allowed after acceptance
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setStatus(11);
                        saveOrder();
                    }
                });
            }
        }
    }

    private void askSeller4RatingNCod() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.ecom_order_details_layout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_ecom_order_confirm, null);
            ((TextView) customView.findViewById(R.id.rateText)).setText(getString(R.string.rateLabel1) + " " + order.getBuyer().getBuyerName() + " " + getString(R.string.rateLabel2));
            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();

            CheckBox codRcvd = ((CheckBox) customView.findViewById(R.id.codConfirmChkBox));
            codRcvd.setText("Did you recieve cash amount Rs " + order.getGrossAmount() + " from "+order.getBuyer().getBuyerName()+"?");
            codRcvd.setVisibility(View.VISIBLE);
            if (order.getMop() != 5) {
                codRcvd.setVisibility(View.GONE);
            }
            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.ratingBar);
                    TextView feedbackLabel = (TextView) customView.findViewById(R.id.rateText);
                    String rcvrFeedback = ((EditText) customView.findViewById(R.id.feedbackText)).getText().toString();
                    float rating = ratingBar.getRating();
                    if ((rating + "").equalsIgnoreCase("0.0")) {
                        feedbackLabel.requestFocus();
                        feedbackLabel.setError("Please rate the transaction");
                        return;
                    }
                    if ( (order.getMop()==5) && (!codRcvd.isChecked()) ) {
                        codRcvd.requestFocus();
                        codRcvd.setError("Please collect Rs " + order.getGrossAmount() + " from "+order.getBuyer().getBuyerName());
                        return;
                    }
                    order.setBuyerFeebackScore(rating);
                    order.setBuyerFeedback(rcvrFeedback);
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

    private void askSeller4DirectPayment() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.ecom_order_details_layout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_ecom_order_confirm, null);
            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();

            CheckBox codRcvd = ((CheckBox) customView.findViewById(R.id.codConfirmChkBox));
            codRcvd.setText("Did you recieve amount Rs " + order.getGrossAmount() + " from "+order.getBuyer().getBuyerName()+" directly to your account?");
            (customView.findViewById(R.id.ratingBar)).setVisibility(View.GONE);
            (customView.findViewById(R.id.rateText)).setVisibility(View.GONE);
            (customView.findViewById(R.id.feedbackText)).setVisibility(View.GONE);
            (customView.findViewById(R.id.ratingBox)).setVisibility(View.GONE);

            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if (!codRcvd.isChecked()) {
                        codRcvd.requestFocus();
                        codRcvd.setError("Please confirm you received Rs " + order.getGrossAmount() + " from "+order.getBuyer().getBuyerName() +"to your account not via PLATA");
                        return;
                    }
                    order.setPaymentStatus(2);
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


    private String getUserRole() {
        if (user.getId().equals(order.getBuyer().getBuyerSourceId())) {
            return "buyer";
        } else {
            return "seller";
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
                        createScreen();
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



    public void updatePayment(Payment payment) {
        JSONObject postData=null;
        // get payment ..update status

        try {
            postData = new JSONObject(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create().toJson(payment));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/payments/", postData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    Fragment fragment = new OrderDetailsFragment();
                    ((OrderDetailsFragment) fragment).setOrder(order);
                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment,"orderDetailsFragment").addToBackStack(null);
                    ft.commit();
                } catch(Exception e) {
                    Log.e("TAG", "Error in starting Razorpay Checkout", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
             //   Snackbar snackbar = Snackbar.make(parentLayout, UIMessage, Snackbar.LENGTH_LONG);
             //   snackbar.show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
}

