package com.inspirado.kuber.ecom.store;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

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
import com.inspirado.kuber.PlataHelper;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.payment.Ledger;
import com.inspirado.kuber.ecom.payment.Payment;
import com.inspirado.kuber.util.Util;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StorePlanFragment extends Fragment implements PaymentResultWithDataListener {

    private LinearLayoutManager linearLayoutManager;
    Store store;
    Ledger ledger;
    User user;
    PlataHelper helper;
    Gson gson = new Gson();
    List<Membership> activeMemberships;
    List<Membership> draftMemberships;
    List<Plan> plans;

    Membership currentMembership;
    Plan selectedPlan;
    Payment currentPayment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_store_setup_4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        Checkout.preload(getContext());
        helper = new PlataHelper(getContext(), getView());
        store = Util.getStoreFromSharedPref(getContext());
        helper.bootStrap();
        getActivity().setTitle(R.string.store_membership_title);
        getAndFillActiveMembership();
        getActivePlans();
        renderPaymentButton();
    }

    private void renderPaymentButton() {
        Button updateBtn = getActivity().findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create payment object and send
                if(selectedPlan==null){
                    helper.handleErrorScheme1("Please choose a plan");
                    return ;
                }
                if (currentPayment == null) currentPayment = new Payment();
                currentPayment.setStatus(1); //initiated
                currentPayment.setLedgerId(helper.getUser().getLedgerId());
                currentPayment.setAmount(selectedPlan.getGross());
                currentPayment.setPaymentDate(new Date());
                currentPayment.setMop(4);
                currentPayment.setOrgChain("/"+user.getClientCode());
                createPayment();
            }
        });
    }

    public void createPayment() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        JSONObject postData = null;
        try {
            postData = new JSONObject(gson.toJson(currentPayment));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/accounting/payments/", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    helper.dismissProgressBar();
                    Checkout checkout = new Checkout();
                    currentPayment = (new Gson()).fromJson(orderObj.toString(), Payment.class);
                    JSONObject options = new JSONObject();
                    options.put("name", "Eunico Infotech Pvt Ltd");
                    options.put("description", "Membership Plan: " + selectedPlan.getDetails());
                    options.put("currency", "INR");
                    options.put("amount", currentPayment.getAmount() * 100);
                    // options.put("image", order.getGrossAmount());
                    options.put("order_id", currentPayment.getExtAttr2Value());
                    options.put("prefill.name", helper.getUser().getName());
                    options.put("prefill.contact", helper.getUser().getAddress());
                    options.put("theme.color", "#FFEC4F");
                    checkout.open(getActivity(), options);
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void getActivePlans() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/customers/membership/plans/active", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseObj) {
                        try {
                            plans = Arrays.asList(gson.fromJson(responseObj.toString(), Plan[].class));
                            populateActivePlanList();
                        } catch (Exception e) {
                            helper.handleErrorScheme1(e.getMessage());
                        }
                        helper.dismissProgressBar();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        helper.handleErrorScheme1("Sytem Error");
                    }
                });
        helper.handleRetry(jsonObjectRequest);
    }

    private void populateActivePlanList() {
        ConstraintLayout newPlanLayout = getActivity().findViewById(R.id.newPlanLayout);
        RadioGroup rg = new RadioGroup(getActivity());
        for (int i = 0; i < plans.size(); i++) {
            Plan plan = plans.get(i);
            String duration = getDuration(plan.getValidityUnit());
            RadioButton radioButton = new RadioButton(getActivity());
            String details = "Rs " + plan.getGross() + " " + plan.getDetails() + ": validity - " + plan.getValidity() + "  " + duration;
            radioButton.setText(details);
            radioButton.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
            radioButton.setId(i);
            rg.addView(radioButton);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buttonText = "Pay Rs. " + plan.getGross();
                    Button updateBtn = getActivity().findViewById(R.id.updateBtn);
                    updateBtn.setText(buttonText);
                    selectedPlan = plan;
                }
            });
        }
        newPlanLayout.addView(rg);
    }

    private String getDuration(int validityUnit) {
        if (validityUnit == 1) return "Days";
        if (validityUnit == 3) return "Weeks";
        if (validityUnit == 2) return "Months";
        if (validityUnit == 4) return "Year";
        return "";
    }


    private void getAndFillActiveMembership() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/customers/" + helper.getUser().getCustomerId() + "/memberships/active", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            if (jsonArray != null && jsonArray.length() > 0) {
                                activeMemberships = Arrays.asList(gson.fromJson(jsonArray.get(0).toString(), Membership.class));;
                            }

                            populateActiveMembershipSection();
                        } catch (Exception e) {
                            helper.handleErrorScheme1(e.getMessage());
                        }
                        helper.dismissProgressBar();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        helper.handleErrorScheme1("Sytem Error");
                    }
                });
        helper.handleRetry(jsonObjectRequest);
    }

    private void populateActiveMembershipSection() {
        if (activeMemberships != null && activeMemberships.size() > 0) {
            currentMembership = activeMemberships.get(0);
            currentMembership.getPlanHistories().forEach(planHistory -> {
                if(planHistory.getStatus()==1){
                    LinearLayout newPlanLayout = getActivity().findViewById(R.id.planHistoriesLayout);
                    CardView card = new CardView(getActivity());
                    TextView planDetails = new TextView(getActivity());
                    String details = (new SimpleDateFormat("dd/MM/yy")).format(planHistory.getPsd()) + " - "+ (new SimpleDateFormat("dd/MM/yy")).format(planHistory.getPed()) + " :  "+planHistory.getDetails() ;
                    planDetails.setText(details);
                    card.addView(planDetails);
                    planDetails.setTextAppearance(getContext(), android.R.style.TextAppearance_Holo_Small);
                    newPlanLayout.addView(card);
                }
            });
          //  ((TextView) getActivity().findViewById(R.id.planStartDate)).setText((new SimpleDateFormat("dd/MM/yy")).format(membership.getPsd()));
            ((TextView) getActivity().findViewById(R.id.planEndDate)).setText((new SimpleDateFormat("dd/MM/yy")).format(currentMembership.getPed()));
            String status = "Active";
            if (currentMembership.getStatus() == 11) status = "Inactive";
            ((TextView) getActivity().findViewById(R.id.status)).setText(status);

        } else {
           // ((TextView) getActivity().findViewById(R.id.planStartDate)).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.planEndDate)).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.status)).setText("No active membership");
          //  ((TextView) getActivity().findViewById(R.id.planStartLbl)).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.planEndLbl)).setVisibility(View.GONE);
        }


    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        currentPayment.setExtAttr2Name("razorPaymentId");
        currentPayment.setExtAttr2Value(paymentData.getPaymentId());
        currentPayment.setExtAttr3Name("razorSignature");
        currentPayment.setExtAttr3Value(paymentData.getSignature());
        currentPayment.setMop(4);
        updatePaymentForSuccess();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("TAG", "onPaymentSuccess: ");
    }

    public void updatePaymentForSuccess() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        currentPayment.setStatus(2);
        JSONObject postData = null;
        try {
            postData = new JSONObject(gson.toJson(currentPayment));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/accounting/payments/", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    currentPayment = (new Gson()).fromJson(orderObj.toString(), Payment.class);
                    createUpdateMembership();
                    // create membership and update plan ..save
                    // update MY-Ledger
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }

    private void createUpdateMembership() throws JSONException {
        helper.showProgressBar(getContext().getString(R.string.extending_membership_msg));
        Date startDate = new Date();
        int method = Request.Method.PUT;
        if (currentMembership == null) {
            currentMembership = new Membership();
            method = Request.Method.POST;
        } else {
            startDate = currentMembership.getPed().before(startDate)?startDate:currentMembership.getPed();
            currentMembership.setIntent(2);
        }
        currentMembership.setPrimaryCustomerId(helper.getUser().getCustomerId());
        Date endDate = new Date();
        int validity = selectedPlan.getValidity();
        int validityUnit = selectedPlan.getValidityUnit();
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        if (validityUnit == 1) {
            c.add(Calendar.DATE, validity);
        } else if (validityUnit == 2) {
            c.add(Calendar.MONTH, validity);
        } else if (validityUnit == 3) {
            c.add(Calendar.YEAR, validity);
        }
        Privilege privilege = new Privilege();
        privilege.setOrgChain("/"+user.getClientCode());
        privilege.setType("PROPERTY");
        privilege.setDescription("Property visibility on marketplace");
        privilege.setReferenceId(store.getId());
        privilege.setAttributeImpacted("marketplace_visibility_end_date");
        privilege.setMeasure(c.getTime()+"");
        privilege.setDataType(1);
        privilege.setDataTypeDescription("date");
        List<Privilege> privs = Arrays.asList(privilege);

        currentMembership.setPrivileges(privs);
 //       currentMembership.setMsd(startDate);
        currentMembership.setPsd(startDate);
        currentMembership.setPed(c.getTime());
        currentMembership.setMed(c.getTime());
        currentMembership.setValidity(selectedPlan.getValidity());
        currentMembership.setValidityUnit(selectedPlan.getValidityUnit());
        currentMembership.setPlanId(selectedPlan.getId());
        currentMembership.setDetails(selectedPlan.getDetails());
        currentMembership.setDiscount(selectedPlan.getDiscount());
        currentMembership.setCover(selectedPlan.getCover());
        currentMembership.setDiscountPercent(selectedPlan.getDiscountPercent());
        currentMembership.setGross(selectedPlan.getGross());
        currentMembership.setNet(selectedPlan.getNet());
        currentMembership.setTax(selectedPlan.getTax());
        currentMembership.setTax1(selectedPlan.getTax1());
        currentMembership.setTax2(selectedPlan.getTax2());
        currentMembership.setTax3(selectedPlan.getTax3());
        currentMembership.setTax1Percent(selectedPlan.getTax1Percent());
        currentMembership.setTax1Percent(selectedPlan.getTax1Percent());
        currentMembership.setTax1Percent(selectedPlan.getTax1Percent());
        currentMembership.setDiscount(selectedPlan.getDiscount());
        currentMembership.setOrgChain("/"+user.getClientCode());
        JSONObject postData = new JSONObject(gson.toJson(currentMembership));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/customers/" + helper.getUser().getCustomerId() + "/memberships", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    currentMembership = (new Gson()).fromJson(orderObj.toString(), Membership.class);
                   /* getAndFillActiveMembership();
                    helper.handleErrorScheme1("Membership updated successfully");
                    getActivity().findViewById(R.id.newPlanCard).setVisibility(View.GONE);
                   getActivity().findViewById(R.id.updateBtn).setVisibility(View.GONE);*/

                    Fragment fragment = new StorePlanSetupSuccessFragment();
                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();

                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }


    public void updatePaymentFailure() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        currentPayment.setStatus(2);
        JSONObject postData = null;
        try {
            postData = new JSONObject(gson.toJson(currentPayment));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/accounting/payments/", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    currentPayment = (new Gson()).fromJson(orderObj.toString(), Payment.class);
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }


}

