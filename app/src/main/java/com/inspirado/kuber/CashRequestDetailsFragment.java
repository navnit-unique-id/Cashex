package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Belal on 18/09/16.
 */


public class CashRequestDetailsFragment extends Fragment {

    CashRequest cashRequest;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cash_req_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle(R.string.cr_details_title);
        IntentFilter filter = new IntentFilter("1");
        filter.addAction("2");
        filter.addAction("3");
        filter.addAction("4");
        filter.addAction("11");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        createScreen();
    }

    private void createScreen(){
        String payableLbl ="Total Recievable";
        Button acceptBtn = (Button) getActivity().findViewById(R.id.acceptBtn);
        ((TextView) getActivity().findViewById(R.id.name)).setText(cashRequest.getRequestor().getName() + "");
        ((TextView) getActivity().findViewById(R.id.frs)).setText("FRS "+cashRequest.getRequestor().getOverallScore() + "");

        ((TextView) getActivity().findViewById(R.id.incentive)).setText(cashRequest.getIncentive() + "");
        ((TextView) getActivity().findViewById(R.id.amount)).setText(cashRequest.getAmount() + "");
        ((TextView) getActivity().findViewById(R.id.payableAmout)).setText(cashRequest.getPayableAmout() + "");
        ((TextView) getActivity().findViewById(R.id.address)).setText(cashRequest.getRequestor().getAddress() + "\n" + cashRequest.getRequestor().getCity() + ", " + cashRequest.getRequestor().getState() + " -  " + cashRequest.getRequestor().getPinCode());
        ((TextView) getActivity().findViewById(R.id.phone)).setText(cashRequest.getRequestor().getMobileNumber());
        ((TextView) getActivity().findViewById(R.id.tranId)).setText(cashRequest.getLndrTransactionId());
        if(cashRequest.getLender()!=null){
            ((TextView) getActivity().findViewById(R.id.ldrName)).setText(cashRequest.getLender().getName() );
            ((TextView) getActivity().findViewById(R.id.lndrFRS)).setText("FRS "+cashRequest.getLender().getOverallScore() );
            ((TextView) getActivity().findViewById(R.id.ldraddress)).setText(cashRequest.getLender().getAddress() + "\n" + cashRequest.getRequestor().getCity() + ", " + cashRequest.getLender().getState() + " -  " + cashRequest.getLender().getPinCode());
            ((TextView) getActivity().findViewById(R.id.ldrphone)).setText(cashRequest.getLender().getMobileNumber());

        }

        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.payment_modes);
        StringTokenizer tokenizer = new StringTokenizer(cashRequest.getPreferredPaymentMode(), ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String paymentModeStr = "";
            if (token.equalsIgnoreCase("1")) {
                paymentModeStr = "Bank Transfer";
                // (getActivity().findViewById(R.id.isBankPreferred)).setVisibility(1);
            }
            if (token.equalsIgnoreCase("2")) {
                paymentModeStr = "PhonePe";
            }
            if (token.equalsIgnoreCase("3")) {
                paymentModeStr = "Paytm";
            }
            if (token.equalsIgnoreCase("4")) {
                paymentModeStr = "GPay ";
            }
            if (token.equalsIgnoreCase("5")) {
                paymentModeStr = "BHIM";
            }
            TextView paymentMode = new TextView(this.getActivity());
            (paymentMode).setText(paymentModeStr);
            linearLayout.addView(paymentMode);

        }
        ((TextView) getActivity().findViewById(R.id.paymentSlot)).setText(cashRequest.getPaymentSlot() + "");
        // accept button is visible if requester is not equal to user
        // status is not equal to 0
        if (( !cashRequest.getRequesterId().equals(user.getId()) ) && (cashRequest.getStatus() == 1)) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.GONE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.GONE);
          //  ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
            acceptBtn.setVisibility(View.VISIBLE);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptRequest();
                }
            });
        }
        Button completeBtn = (Button) getActivity().findViewById(R.id.completeBtn);
        if ((!cashRequest.getRequesterId().equals(user.getId())) && (cashRequest.getStatus() == 2)) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.VISIBLE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.GONE);
          //  ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
            completeBtn.setVisibility(View.VISIBLE);
            completeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    completeRequest();
                }
            });
        }

        if ((!cashRequest.getRequesterId().equals(user.getId())) && (cashRequest.getStatus() == 3)) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.VISIBLE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.GONE);
          //  ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
        }



        if ((cashRequest.getRequesterId().equals(user.getId())) && ((cashRequest.getStatus() == 1))) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.GONE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
        }

        if ((cashRequest.getRequesterId().equals(user.getId())) && ((cashRequest.getStatus() == 2))) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.GONE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.VISIBLE);
            ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
        }
        Button confirmBtn = (Button) getActivity().findViewById(R.id.comfirmBtn);
        if ((cashRequest.getRequesterId().equals(user.getId())) && (cashRequest.getStatus() == 3)) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.GONE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.VISIBLE);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmRequest();
                }
            });
            ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
        }
        if ((cashRequest.getRequesterId().equals(user.getId())) && (cashRequest.getStatus() == 4)) {
            ((ConstraintLayout) getActivity().findViewById(R.id.requester_contact_block)).setVisibility(View.GONE);
            ((ConstraintLayout) getActivity().findViewById(R.id.lender_contact_block)).setVisibility(View.VISIBLE);
            ((TextView) getActivity().findViewById(R.id.payableAmoutLabel)).setText("Total Payable");
        }
    }

    public void setCashRequest(CashRequest cashRequest) {
        this.cashRequest = cashRequest;
    }


    private void acceptRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Accepting...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = null;

        String paymentMode = "";
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            cashRequest.setAcceptanceDate(new Date());
            cashRequest.setLenderId(user.getId());
            cashRequest.setStatus(2);
            JSONObject postData = new JSONObject(gson.toJson(cashRequest, CashRequest.class));
            jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObj) {
                            try {
                                Fragment fragment = new NewCashRequestSuccessFragment();
                                ((NewCashRequestSuccessFragment) fragment).setMessage("You have successfully accepted the cash request");
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment).addToBackStack(null);;
                                ft.commit();
                            } catch (Exception e) {
                               // Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                Snackbar snackbar = Snackbar
                                        .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();

                            }
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String body = "";
                            JSONArray errors = null;
                            try {
                                if (error.networkResponse.statusCode == 412) {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    errors = new JSONArray(body);
                                  //  Toast.makeText(getContext(), (errors.get(0)).toString(), Toast.LENGTH_LONG).show();
                                    Snackbar snackbar = Snackbar
                                            .make(getView(), (errors.get(0)).toString(), Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                } else {
                                   // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                    Snackbar snackbar = Snackbar
                                            .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            } catch (Exception e) {
                                // exception
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

    private void completeRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Completing...");
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.constraintLayout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_cash_req_confirm, null);
            ((TextView) customView.findViewById(R.id.rateText)).setText(getString(R.string.rateLabel1) + " " +cashRequest.getRequestor().getName() +" " + getString( R.string.rateLabel2) );

            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);

            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

            popupWindow.setFocusable(true);
            popupWindow.update();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            cashRequest.setCompletionDate(new Date());
            cashRequest.setLenderId(user.getId());
            cashRequest.setStatus(3);
            final JSONObject postData = new JSONObject(gson.toJson(cashRequest, CashRequest.class));
            Spinner paymentMode = (Spinner) customView.findViewById(R.id.payment_mode);
            List list = Util.getPaymentOptionsDetails(cashRequest.getPreferredPaymentMode());
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            paymentMode.setAdapter(myAdapter);


            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.ratingBar);
                    TextView feedbackLabel = (TextView) customView.findViewById(R.id.rateText);
                    String rcvrFeedback = ((EditText) customView.findViewById(R.id.feedbackText)).getText().toString();

                    float rating = ratingBar.getRating();
                    Log.d("TAG", "onClick: "+rating);
                    if((rating+"").equalsIgnoreCase("0.0")){
                        feedbackLabel.setError("Please rate the transaction");
                        return;
                    }
                    progressDialog.show();
                    try {
                        String selectedValue = ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem() == null ? "" : ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem().toString();
                        String paymentOptionCode = Util.getPaymentOptionCode(selectedValue);
                        postData.put("lndrPaymentMode", paymentOptionCode);
                        String lndr_transaction_id = ((TextInputEditText) customView.findViewById(R.id.lndr_transaction_id)).getText().toString();
                        postData.put("lndrTransactionId", lndr_transaction_id);
                        postData.put("rcvrRating", rating);
                        postData.put("rcvrFeedback", rcvrFeedback);

                    } catch (Exception e) {

                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new NewCashRequestSuccessFragment();
                                        ((NewCashRequestSuccessFragment) fragment).setMessage("Request completed successfully");
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);;
                                        ft.commit();
                                    } catch (Exception e) {
                                      //  Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar snackbar = Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                  //  Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Snackbar snackbar = Snackbar
                                            .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    progressDialog.dismiss();
                                }
                            });
                    popupWindow.dismiss();
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(jsonObjectRequest);
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

    private void confirmRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Confirming...");
        Button showPopupBtn, submitPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.constraintLayout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_cash_req_confirm, null);
            submitPopupBtn = (Button) customView.findViewById(R.id.submitPopupBtn);
            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            ((TextView) customView.findViewById(R.id.rateText)).setText(getString(R.string.rateLabel1) + " " +cashRequest.getLender().getName() +" " + getString( R.string.rateLabel2) );

            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            cashRequest.setStatus(4);
            final JSONObject postData = new JSONObject(gson.toJson(cashRequest, CashRequest.class));
            Spinner paymentMode = (Spinner) customView.findViewById(R.id.payment_mode);
            List list = Util.getPaymentOptionsDetails(cashRequest.getPreferredPaymentMode());
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            paymentMode.setAdapter(myAdapter);


            submitPopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingBar ratingBar = (RatingBar) customView.findViewById(R.id.ratingBar);
                    TextView feedbackLabel = (TextView) customView.findViewById(R.id.rateText);
                    String lndrFeedback = ((EditText) customView.findViewById(R.id.feedbackText)).getText().toString();

                    float rating = ratingBar.getRating();
                    Log.d("TAG", "onClick: "+rating);
                    if((rating+"").equalsIgnoreCase("0.0")){
                        feedbackLabel.setError("Please rate the transaction");
                        return;
                    }
                    progressDialog.show();
                    try {
                        String selectedValue = ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem() == null ? "" : ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem().toString();
                        String paymentOptionCode = Util.getPaymentOptionCode(selectedValue);
                        postData.put("rcvrPaymentMode", paymentOptionCode);
                        String lndr_transaction_id = ((TextInputEditText) customView.findViewById(R.id.lndr_transaction_id)).getText().toString();
                        postData.put("rcvrTransactionId", lndr_transaction_id);
                        postData.put("lndrRating", rating);
                        postData.put("lndrFeedback", lndrFeedback);

                    } catch (Exception e) {

                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new NewCashRequestSuccessFragment();
                                        ((NewCashRequestSuccessFragment) fragment).setMessage("You have successfully completed the cash request");
                                        FragmentTransaction ft =  getFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);;
                                        ft.commit();
                                    } catch (Exception e) {
                                     //   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar snackbar = Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    String body = "";
                                    JSONArray errors = null;
                                    try {
                                        if (error.networkResponse.statusCode == 412) {
                                            body = new String(error.networkResponse.data, "UTF-8");
                                            errors = new JSONArray(body);
                                         //   Toast.makeText(getContext(), (errors.get(0)).toString(), Toast.LENGTH_LONG).show();
                                            Snackbar snackbar = Snackbar
                                                    .make(getView(), (errors.get(0)).toString(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                         //   Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                            Snackbar snackbar = Snackbar
                                                    .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    } catch (Exception e) {
                                        // exception
                                    }
                                    progressDialog.dismiss();                                }
                            });
                    popupWindow.dismiss();
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(jsonObjectRequest);
                }
            });

            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            //new RestService().execute(getString(R.string.columbus_ms_url) + "/CashRequests", postData.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getData(boolean silent) {
        String resource="";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Cash Requests...");
        if(!silent){
            progressDialog.show();
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/requests/" +cashRequest.getId(),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    try {
                         cashRequest = (new Gson()).fromJson(response.toString(), CashRequest.class);
                        createScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
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
           //     Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
           //     toast.show();
                Snackbar snackbar = Snackbar
                        .make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonRequest);
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
                getData(true);
               // createScreen();
            }
        }
    };
}

