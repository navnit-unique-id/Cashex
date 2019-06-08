package com.crisprupee.cashex;

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

/**
 * Created by Belal on 18/09/16.
 */


public class NewCashRequestFragment extends Fragment {

    CashRequest cashRequest;
    User user;
    EditText amount;
    EditText payableAmount;
    EditText charges;
    Spinner paymentSlot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_cash_req, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle("Request Cash");
        Button reqBtn = (Button) getActivity().findViewById(R.id.imageButton);
        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRequest();
            }
        });
        amount = ((EditText) getActivity().findViewById(R.id.amountLabel));
        payableAmount = ((EditText) getActivity().findViewById(R.id.payableAmoutLabel));
        charges = ((EditText) getActivity().findViewById(R.id.charges));
        paymentSlot = (Spinner) getActivity().findViewById(R.id.paymentSlot);
        final double amountVal = Double.parseDouble("0" + amount.getText());

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
                if (s.length() != 0)
                    charges.setText((Double.parseDouble(s + "") * .01) + "");
                payableAmount.setText((Double.parseDouble(s + "") * 1.01) + "");
            }
        });

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
            cashRequest.setAmount(Double.parseDouble(amount.getText().toString()));
            cashRequest.setIncentive(Double.parseDouble(charges.getText().toString()));
            cashRequest.setPayableAmout(Double.parseDouble(payableAmount.getText().toString()));

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
            cashRequest.setPaymentSlot(paymentSlot.getSelectedItem().toString());
            cashRequest.setPreferredPaymentMode(paymentMode);
            cashRequest.setRequestor(user);
            cashRequest.setRequesterId(user.getId());
            cashRequest.setRcvrLat(user.getLat());
            cashRequest.setRcvrLng(user.getLng());
            cashRequest.setStatus(1);
            //     postData.put("preferredPaymentMode", paymentMode);
            //    postData.put("paymentSlot", ((Spinner) getActivity().findViewById(R.id.paymentSlot)).getSelectedItem().toString());
            //    postData.put("requesterId", user.getId());
            //   postData.put("incentive", charges.getText().toString());
            //    postData.put("status", 1);
            double amountVal = Double.parseDouble("0" + amount.getText());

            //   Log.d("TAG", "putData: " + postData.toString());
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
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObj) {
                            try {
                                Fragment fragment = new NewCashRequestSuccessFragment();
                                ((NewCashRequestSuccessFragment) fragment).setMessage("Your cash request has been registered. Wait for someone to accept your request.");
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
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

