package com.inspirado.kuber.ecom.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.util.Util;
import com.inspirado.kuber.cash.CashRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Selector;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class StoreDetailsFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;
    Store store;
    Spinner spinner;
    ArrayAdapter<CharSequence> arrayAdapter;

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
        return inflater.inflate(R.layout.fragment_ecom_store_setup_2, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);

        callAll();
        setValues(store);
        getActivity().setTitle(R.string.store_details_title);
        Button nextBtn = (Button) getActivity().findViewById(R.id.updateBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                TextInputEditText delvieryRate = (TextInputEditText) getActivity().findViewById(R.id.deliveryCharge) ;

                try {
                    store.setAddress(((EditText) getActivity().findViewById(R.id.address)).getText().toString());
                    store.setName(((EditText) getActivity().findViewById(R.id.name)).getText().toString());
                    String clientCode = user.getClientCode();
                    int method = Request.Method.PUT;
                    if (store.getId()==null){
                        method= Request.Method.POST;
                    }
                    store.setOrgChain("/"+user.getClientCode());
                    store.setOwnerId(user.getId());
                    store.setOwnerLedgerId(user.getLedgerId());
                    store.setStatus(3);
                    store.setOpenTime( ((EditText) getActivity().findViewById(R.id.openingTime)).getText().toString());
                    store.setCloseTime(((EditText) getActivity().findViewById(R.id.closingTime)).getText().toString());
                    store.setHasDeliveryService( ((Switch) getActivity().findViewById(R.id.deliveryEnabled)).isChecked()) ;
                    store.setDeliveryCharge(Double.parseDouble("0"+delvieryRate.getText()));
                    Spinner spinner = ((Spinner)getActivity().findViewById(R.id.spinner));
                    store.setTypeTxt(  spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString());
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(store));
                    jsonObjectRequest = new JsonObjectRequest(method, getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/properties/orgs/"+ user.getId(), postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Util.updateStoreInSharedPref(getContext().getSharedPreferences("pref", 0), responseObj);
                                        Fragment fragment = new NewStoreRequestSuccessFragment();
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                                        ft.commit();

                                    } catch (Exception e) {
                                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(getView(), "System Error", Snackbar.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }


            private boolean validate() {
                String TIME24HOURS_PATTERN ="([01]?[0-9]|2[0-3]):[0-5][0-9]";
                Pattern pattern =Pattern.compile(TIME24HOURS_PATTERN);;
                Matcher matcher;

                String decimalPattern = "[0-9]?[0-9]?(\\.[0-9][0-9]?)?";
                Pattern DPattern =Pattern.compile(decimalPattern);;
                Matcher dMatcher;

                boolean cancel = false;
                EditText address = (EditText) getActivity().findViewById(R.id.address);
                EditText name = (EditText) getActivity().findViewById(R.id.name);
                TextInputEditText delvieryRate = (TextInputEditText) getActivity().findViewById(R.id.deliveryCharge) ;
                Switch deliveryEnabled = (Switch)getActivity().findViewById(R.id.deliveryEnabled);

                AutoCompleteTextView state = (AutoCompleteTextView) getActivity().findViewById(R.id.state);
                EditText openingTime = (EditText) getActivity().findViewById(R.id.openingTime);
                String openingTimeTxt = openingTime.getText()+"";
                EditText closingTime = (EditText) getActivity().findViewById(R.id.closingTime);
                String closingTimeTxt = closingTime.getText()+"";

                if (deliveryEnabled.isChecked()) {
                    String delRate = delvieryRate.getText()+"";
                    dMatcher = DPattern.matcher(delRate);
                    if(!dMatcher.matches()){
                        delvieryRate.setError(getResources().getString(R.string.store_delivery_charge_format_error));
                        cancel = true;
                    }
                }

                if (address.getText().toString().equalsIgnoreCase("")) {
                    address.setError(getResources().getString(R.string.registration3_error_address_blank));
                    cancel = true;
                }
                if (name.getText().toString().equalsIgnoreCase("")) {
                    name.setError(getResources().getString(R.string.registration3_error_name_blank));
                    cancel = true;
                }
                if (name.getText().toString().equalsIgnoreCase("")) {
                    name.setError(getResources().getString(R.string.registration3_error_name_blank));
                    cancel = true;
                }
                if (openingTime.getText().toString().equalsIgnoreCase("")) {
                    openingTime.setError(getResources().getString(R.string.store_opening_time_blank));
                    cancel = true;
                }
                if (closingTime.getText().toString().equalsIgnoreCase("")) {
                    closingTime.setError(getResources().getString(R.string.store_closing_time_blank));
                    cancel = true;
                }
                matcher = pattern.matcher(openingTimeTxt);
                if(!matcher.matches()){
                    openingTime.setError(getResources().getString(R.string.store_time_format_error));
                    cancel = true;
                }
                matcher = pattern.matcher(closingTimeTxt);
                if(!matcher.matches()){
                    closingTime.setError(getResources().getString(R.string.store_time_format_error));
                    cancel = true;
                }
                return cancel;
            }

        });
    }

    public void setValues(Store store){
        if(store !=null){
            ((EditText) getActivity().findViewById(R.id.address)).setText(store.getAddress());
            ((EditText) getActivity().findViewById(R.id.name)).setText(store.getName());
            ((EditText) getActivity().findViewById(R.id.openingTime)).setText(store.getOpenTime());
            ((EditText) getActivity().findViewById(R.id.closingTime)).setText(store.getCloseTime());

            Switch hasDeliveryService = ((Switch) getActivity().findViewById(R.id.deliveryEnabled));
            TextInputLayout delvieryRateL = (TextInputLayout) getActivity().findViewById(R.id.deliveryChargeLayout) ;
           ((TextInputEditText) getActivity().findViewById(R.id.deliveryCharge)).setText(store.getDeliveryCharge()+"");
            if(store.isHasDeliveryService()) {
                delvieryRateL.setVisibility(View.VISIBLE);
            }else{
                delvieryRateL.setVisibility(View.INVISIBLE);
            }

            hasDeliveryService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        delvieryRateL.setVisibility(View.VISIBLE);
                        ((TextInputEditText) getActivity().findViewById(R.id.deliveryCharge)).setText(""+store.getDeliveryCharge());
                    }else{
                        delvieryRateL.setVisibility(View.INVISIBLE);
                        ((TextInputEditText) getActivity().findViewById(R.id.deliveryCharge)).setText("0");
                    }
                }
            });
            hasDeliveryService.setChecked(store.isHasDeliveryService());

            ((RatingBar) getActivity().findViewById(R.id.ratingBar3)).setRating((float)store.getPropertyRatingScoreAvg());
            ((TextView) getActivity().findViewById(R.id.ratingTxt)).setText((new DecimalFormat("#.00")).format(store.getPropertyRatingScoreAvg()));

            Spinner spinner=(Spinner)getActivity().findViewById(R.id.spinner);
            spinner.setSelection(arrayAdapter.getPosition(store.getTypeTxt()));

        }
    }


    public void callAll()
    {
        addToSpinner();
    }

    // Get the content of cities.json from assets directory and store it as string



    // Add the data items to the spinner
    void addToSpinner()
    {
        spinner=(Spinner)getActivity().findViewById(R.id.spinner);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.store_category, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

}

