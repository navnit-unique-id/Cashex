package com.crisprupee.cashex;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Belal on 18/09/16.
 */


public class CashRequestDetailsFragment extends Fragment {

    CashRequest cashRequest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cash_req_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Request Cash");

        Button acceptBtn = (Button) getActivity().findViewById(R.id.acceptBtn);
        ((TextView) getActivity().findViewById(R.id.amount)).setText(cashRequest.getAmount() + "");
        ((TextView) getActivity().findViewById(R.id.payableAmout)).setText(cashRequest.getPayableAmout() + "");
        StringTokenizer tokenizer = new StringTokenizer(cashRequest.getPreferredPaymentMode(), ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equalsIgnoreCase("1")) {
                (getActivity().findViewById(R.id.isBankPreferred)).setVisibility(1);
            }
            if (token.equalsIgnoreCase("2")) {
                (getActivity().findViewById(R.id.isPhonePePreferred)).setVisibility(1);
            }
            if (token.equalsIgnoreCase("3")) {
                (getActivity().findViewById(R.id.isPayTMPreferred)).setVisibility(1);
            }
        }
        ((TextView) getActivity().findViewById(R.id.paymentSlot)).setText(cashRequest.getPaymentSlot() + "");
        // accept button is visible if requester is not equal to user
        // status is not equal to 0
        if ((cashRequest.getRequesterId() != 420) && (cashRequest.getStatus() == 1)) {
            acceptBtn.setVisibility(View.VISIBLE);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptRequest();
                }
            });
        }
        Button completeBtn = (Button) getActivity().findViewById(R.id.completeBtn);
        if ((cashRequest.getRequesterId() != 420) && (cashRequest.getStatus() == 2)) {
            completeBtn.setVisibility(View.VISIBLE);
            completeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    completeRequest();
                }
            });
        }

        Button confirmBtn = (Button) getActivity().findViewById(R.id.completeBtn);
        if ((cashRequest.getRequesterId() == 420) && (cashRequest.getStatus() == 3)) {
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmRequest();
                }
            });
        }
    }

    public void setCashRequest(CashRequest cashRequest) {
        this.cashRequest = cashRequest;
    }


    private void acceptRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = null;
        JSONObject postData = new JSONObject();
        String paymentMode = "";
        try {
            postData.put("amount", cashRequest.getAmount());
            postData.put("payableAmout", cashRequest.getPayableAmout());
            postData.put("preferredPaymentMode", cashRequest.getPreferredPaymentMode());
            postData.put("paymentSlot", cashRequest.getPaymentSlot());
            postData.put("requesterId", cashRequest.getRequesterId());
            postData.put("requestDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getRequestDate()));
            postData.put("acceptanceDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(new Date()));
            postData.put("lenderId", 420);
            postData.put("id", cashRequest.getId());
            postData.put("status", 2);
            Log.d("TAG", "putData: " + postData.toString());
            //new RestService().execute(getString(R.string.columbus_ms_url) + "/CashRequests", postData.toString());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObj) {
                            try {
                                Fragment fragment = new NewCashRequestSuccessFragment();
                                ((NewCashRequestSuccessFragment) fragment).setMessage("You have successfully accepted the cash request");
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
                                ft.commit();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void completeRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Completing...");
        //   progressDialog.show();
        //   final JsonObjectRequest jsonObjectRequest = null;
        final JSONObject postData = new JSONObject();
      //  String paymentMode = "";
        Button showPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.constraintLayout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_cash_req_confirm, null);
            //customView.setBackgroundDrawable(new BitmapDrawable());

            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

            popupWindow.setFocusable(true);
            popupWindow.update();

            postData.put("amount", cashRequest.getAmount());
            postData.put("payableAmout", cashRequest.getPayableAmout());
            postData.put("preferredPaymentMode", cashRequest.getPreferredPaymentMode());
            postData.put("paymentSlot", cashRequest.getPaymentSlot());
            postData.put("requesterId", cashRequest.getRequesterId());
            postData.put("requestDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getRequestDate()));
            postData.put("acceptanceDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getAcceptanceDate()));
            postData.put("completionDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(new Date()));
            postData.put("lenderId", 420);
            postData.put("id", cashRequest.getId());
            postData.put("status", 3);
            Log.d("TAG", "putData: " + postData.toString());
            Spinner paymentMode = (Spinner) customView.findViewById(R.id.payment_mode);
            List list = Util.getPaymentOptionsDetails(cashRequest.getPreferredPaymentMode());
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            paymentMode.setAdapter(myAdapter);


            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    try {
                        String selectedValue = ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem() == null ? "" : ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem().toString();
                        String paymentOptionCode = Util.getPaymentOptionCode(selectedValue);
                        postData.put("lndrPaymentMode", paymentOptionCode);
                        String lndr_transaction_id = ((TextInputEditText) customView.findViewById(R.id.lndr_transaction_id)).getText().toString();
                        postData.put("lndrTransactionId", lndr_transaction_id);
                    } catch (Exception e) {

                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new NewCashRequestSuccessFragment();
                                        ((NewCashRequestSuccessFragment) fragment).setMessage("You have successfully completed the cash request");
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.commit();
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                    popupWindow.dismiss();
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(jsonObjectRequest);
                }
            });
            //new RestService().execute(getString(R.string.columbus_ms_url) + "/CashRequests", postData.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Confirming...");
        //   progressDialog.show();
        //   final JsonObjectRequest jsonObjectRequest = null;
        final JSONObject postData = new JSONObject();
        //  String paymentMode = "";
        Button showPopupBtn, closePopupBtn;
        final PopupWindow popupWindow;
        ConstraintLayout constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.constraintLayout);
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_cash_req_confirm, null);
            //customView.setBackgroundDrawable(new BitmapDrawable());

            closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
            popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

            popupWindow.setFocusable(true);
            popupWindow.update();

            postData.put("amount", cashRequest.getAmount());
            postData.put("payableAmout", cashRequest.getPayableAmout());
            postData.put("preferredPaymentMode", cashRequest.getPreferredPaymentMode());
            postData.put("paymentSlot", cashRequest.getPaymentSlot());
            postData.put("requesterId", cashRequest.getRequesterId());
            postData.put("requestDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getRequestDate()));
            postData.put("acceptanceDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getAcceptanceDate()));
            postData.put("completionDate", (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(cashRequest.getCompletionDate()));
            postData.put("lenderId", 420);
            postData.put("id", cashRequest.getId());
            postData.put("status", 4);
            postData.put("lndrPaymentMode", cashRequest.getLndrPaymentMode());
            postData.put("lndrTransactionId", cashRequest.getLndrTransactionId());

            Log.d("TAG", "putData: " + postData.toString());
            Spinner paymentMode = (Spinner) customView.findViewById(R.id.payment_mode);
            List list = Util.getPaymentOptionsDetails(cashRequest.getPreferredPaymentMode());
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            paymentMode.setAdapter(myAdapter);


            closePopupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    try {
                        String selectedValue = ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem() == null ? "" : ((Spinner) customView.findViewById(R.id.payment_mode)).getSelectedItem().toString();
                        String paymentOptionCode = Util.getPaymentOptionCode(selectedValue);
                        postData.put("rcvrPaymentMode", paymentOptionCode);
                        String lndr_transaction_id = ((TextInputEditText) customView.findViewById(R.id.lndr_transaction_id)).getText().toString();
                        postData.put("rcvrTransactionId", lndr_transaction_id);
                    } catch (Exception e) {

                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/CashRequests", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new NewCashRequestSuccessFragment();
                                        ((NewCashRequestSuccessFragment) fragment).setMessage("You have successfully completed the cash request");
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.commit();
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                    popupWindow.dismiss();
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(jsonObjectRequest);
                }
            });
            //new RestService().execute(getString(R.string.columbus_ms_url) + "/CashRequests", postData.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

