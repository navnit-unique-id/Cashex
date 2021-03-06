package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class NewCashRequestFragment3 extends Fragment {

    CashRequest cashRequest;
    User user;
    EditText amount;
    TextView payableAmount;
    TextView charges;
    Spinner paymentSlot;
    Hashtable lenders = new Hashtable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_cash_req_3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        if(user==null){
            user = (new Gson()).fromJson(json, User.class);
        }
        getActivity().setTitle("New Request");
        RadioGroup pickupDelivery = ((RadioGroup)(getActivity().findViewById(R.id.pickupdelivery)));
        Button reqBtn = (Button) getActivity().findViewById(R.id.imageButton);
        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRequest();
            }
        });
        payableAmount = ((TextView) getActivity().findViewById(R.id.payableAmoutLabel));
        charges = ((TextView) getActivity().findViewById(R.id.charges));
        paymentSlot = (Spinner) getActivity().findViewById(R.id.paymentSlot);
        amount = ((EditText) getActivity().findViewById(R.id.amountLabel));

         final double pickupRate =user.getPickupRate();
        final double deliveryRate=user.getDeliveryRate();
        final double chargeCap=user.getChargeCap();

        if (user.isPickupServiceEnabled()) {
            pickupDelivery.setVisibility(View.VISIBLE);
            (getActivity().findViewById(R.id.textView33)).setVisibility(View.VISIBLE);
            pickupDelivery.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener()
            {
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    String requestTypeStr = ((RadioButton) getActivity().findViewById(((RadioGroup) getActivity().findViewById(R.id.pickupdelivery)).getCheckedRadioButtonId())).getText().toString();
                    String payableReceivableLbl = (requestTypeStr.equalsIgnoreCase("delivery"))? "Total Payable":"Total Receivable";
                    ((TextView) getActivity().findViewById(R.id.textView10)).setText(payableReceivableLbl);
                }
            });
        }

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if ((amount.getText().toString().equalsIgnoreCase(""))) {
                    amount.setText("0");
                }
                /*double amountVal = Double.parseDouble("0" + amount.getText());
                String formatted = (new DecimalFormat("##.##")).format(amountVal);
                amount.setText(formatted);*/

                String requestTypeStr = ((RadioButton) getActivity().findViewById(((RadioGroup) getActivity().findViewById(R.id.pickupdelivery)).getCheckedRadioButtonId())).getText().toString();
                double rate = requestTypeStr.equalsIgnoreCase("delivery") ? deliveryRate : pickupRate;
                double amt = Double.parseDouble(amount.getText().toString() + "");
                double charge =  (amt*rate /100 >chargeCap)?chargeCap:amt*rate /100 ;
                charge=(Math.round(charge)*100)/100;
                double total = charge + amt;
                total=(Math.round(total)*100)/100;
                charges.setText(charge + "");
                payableAmount.setText(total + "");
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
    private void saveRequest() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving...");
        JsonObjectRequest jsonObjectRequest = null;
        // JSONObject postData = new JSONObject();
        String paymentMode = "";
        CashRequest cashRequest = new CashRequest();
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            double amountVal = Double.parseDouble("0" + amount.getText());
            amountVal=(Math.round(amountVal)*100)/100;
            amount.setText(amountVal+"");
            cashRequest.setAmount(amountVal);
            cashRequest.setIncentive(Double.parseDouble(charges.getText().toString()));
            cashRequest.setPayableAmount(Double.parseDouble(payableAmount.getText().toString()));

            //      postData.put("amount", ((EditText) getActivity().findViewById(R.id.amountLabel)).getText());
            //      postData.put("payableAmout", ((EditText) getActivity().findViewById(R.id.payableAmoutLabel)).getText());
            if (((CheckBox) getActivity().findViewById(R.id.isBankPreferred)).isChecked()) {
                paymentMode = "1";
            }
            if (((CheckBox) getActivity().findViewById(R.id.isPhonePePreferred)).isChecked()) {
                paymentMode = paymentMode + "," + "2";
            }
            if (((CheckBox) getActivity().findViewById(R.id.isPayTMPreferred)).isChecked()) {
                paymentMode = paymentMode + "," + "3";
            }
            if (((CheckBox) getActivity().findViewById(R.id.isGpayPreferred)).isChecked()) {
                paymentMode = paymentMode + "," + "4";
            }
            if (((CheckBox) getActivity().findViewById(R.id.isBhimPreferred)).isChecked()) {
                paymentMode = paymentMode + "," + "5";
            }
            String requestTypeStr = ((RadioButton) getActivity().findViewById(((RadioGroup) getActivity().findViewById(R.id.pickupdelivery)).getCheckedRadioButtonId())).getText().toString();
            int requestType = requestTypeStr.equalsIgnoreCase("delivery") ? 1 : 2;

            cashRequest.setPaymentSlot(paymentSlot.getSelectedItem().toString());
            cashRequest.setPreferredPaymentMode(paymentMode);
            cashRequest.setRequestor(user);
            cashRequest.setRequesterId(user.getId());
            cashRequest.setRcvrLat(user.getLat());
            cashRequest.setRcvrLng(user.getLng());
            cashRequest.setClientCode(user.getClientCode());
            cashRequest.setRcvrLat(user.getLat());
            cashRequest.setRcvrLng(user.getLng());
            cashRequest.setRequestType(requestType);
            cashRequest.setStatus(1);
            ArrayList<Lender> lenderList = new ArrayList<Lender>(lenders.values());
            cashRequest.setPossibleLenders(lenderList);
            if (amountVal <= 0) {
                amount.setError(getString(R.string.error_invalid_amount));
                return;
            }
            if (paymentMode.equalsIgnoreCase("")) {
                ((TextView) getActivity().findViewById(R.id.textView5)).setError(getString(R.string.error_invalid_payment_option));
                return;
            }
            JSONObject postData = new JSONObject(gson.toJson(cashRequest, CashRequest.class));

            progressDialog.show();
            //new RestService().execute(getString(R.string.columbus_ms_url) + "/CashRequests", postData.toString());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/cashrequest/v2", postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObj) {
                            try {
                                Fragment fragment = new NewCashRequestSuccessFragment();
                                ((NewCashRequestSuccessFragment) fragment).setMessage("Cash request created. Please wait for someone to accept your request.");
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                                ft.commit();
                            } catch (Exception e) {
                                //   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                Snackbar
                                        .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
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
                                    //      Toast.makeText(getContext(), (errors.get(0)).toString(), Toast.LENGTH_LONG).show();

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
}

