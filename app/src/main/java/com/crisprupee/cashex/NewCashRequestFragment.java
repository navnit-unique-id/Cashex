package com.crisprupee.cashex;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Belal on 18/09/16.
 */


public class NewCashRequestFragment extends Fragment {

    CashRequest cashRequest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_cash_req, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Request Cash");

        ImageButton reqBtn = (ImageButton) getActivity().findViewById(R.id.imageButton);

        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRequest();
            }
        });
    }

    private void saveRequest() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = null;
        JSONObject postData = new JSONObject();
        String paymentMode="";
        try {
            postData.put("amount", ((EditText) getActivity().findViewById(R.id.amountLabel)).getText());
            postData.put("payableAmout", ((EditText) getActivity().findViewById(R.id.payableAmoutLabel)).getText());
            if(((CheckBox) getActivity().findViewById(R.id.isBankPreferred)).isChecked()){
                paymentMode="1";
            }
            if(((CheckBox) getActivity().findViewById(R.id.isPhonePePreferred)).isChecked()){
                paymentMode=paymentMode+","+"2";
            }
            if(((CheckBox) getActivity().findViewById(R.id.isPayTMPreferred)).isChecked()){
                paymentMode=paymentMode+","+"3";
            }
            postData.put("preferredPaymentMode", paymentMode);
            postData.put("paymentSlot", ((Spinner) getActivity().findViewById(R.id.paymentSlot)).getSelectedItem().toString());
            postData.put("requesterId", 420);
            postData.put("status", 1);

            Log.d("TAG", "putData: " + postData.toString());
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
}

