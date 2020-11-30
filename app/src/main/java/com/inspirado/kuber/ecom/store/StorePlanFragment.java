package com.inspirado.kuber.ecom.store;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cashfree.pg.CFPaymentService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.PlataHelper;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.order.Order;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cashfree.pg.CFPaymentService.PARAM_APP_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_CURRENCY;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_NOTE;

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
    Button refreshBtn;

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
       // Checkout.preload(getContext());
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
         refreshBtn = getActivity().findViewById(R.id.refreshBtn);
         refreshBtn.setVisibility(View.GONE);
         updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create payment object and send
                if(selectedPlan==null){
                    helper.handleErrorScheme1("Please choose a plan");
                    return ;
                }
                startMembershipFlow();
               // createPayment();
            }
        });

         refreshBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                doFinalWorks();
             }
         });
    }

    private void startMembershipFlow()  {
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
        currentMembership.setStatus(1);
        JSONObject postData = null;
        try {
            postData = new JSONObject(gson.toJson(currentMembership));
        } catch (JSONException e) {
            helper.handleErrorScheme1("System Exception: Error parsing JSON");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/customers/" + helper.getUser().getCustomerId() + "/memberships", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject orderObj) {
                try {
                    currentMembership = (new Gson()).fromJson(orderObj.toString(), Membership.class);
                    createPayment();
                   /* Fragment fragment = new StorePlanSetupSuccessFragment();
                    FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();*/

                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                    refreshBtn.setVisibility(View.VISIBLE);
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Failed to create membership.");
                refreshBtn.setVisibility(View.VISIBLE);
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }

    public void createPayment() {
        helper.showProgressBar(getContext().getString(R.string.creating_payment));
        currentPayment = new Payment();
        currentPayment.setAmount(selectedPlan.getGross());
        currentPayment.setStatus(1); //initiated
        currentPayment.setLedgerId(helper.getUser().getLedgerId());
        currentPayment.setPaymentDate(new Date());
        currentPayment.setMop(4);
        currentPayment.setOrgChain("/"+user.getClientCode());
        currentPayment.setRefType(4);
        currentPayment.setRefId(currentMembership.getId());

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
                  //  Checkout checkout = new Checkout();
                    currentPayment = (new Gson()).fromJson(orderObj.toString(), Payment.class);
                    JSONObject options = new JSONObject();
                    startCashFreePayment();
                    /*options.put("name", "Eunico Infotech Pvt Ltd");
                    options.put("description", "Membership Plan: " + selectedPlan.getDetails());
                    options.put("currency", "INR");
                    options.put("amount", currentPayment.getAmount() * 100);
                    options.put("order_id", currentPayment.getExtAttr2Value());
                    options.put("prefill.name", helper.getUser().getName());
                    options.put("prefill.contact", helper.getUser().getAddress());
                    options.put("theme.color", "#FFEC4F");
                    checkout.open(getActivity(), options);*/
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                    refreshBtn.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
                refreshBtn.setVisibility(View.VISIBLE);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void startCashFreePayment() {
        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();

        Map<String, String> params = new HashMap<>();

        params.put(PARAM_APP_ID, "58846ac3bef4480a7d69ba77064885");
        params.put(PARAM_ORDER_ID, currentPayment.getId()+"");
        params.put(PARAM_ORDER_AMOUNT, currentPayment.getAmount() + "");
        params.put(PARAM_ORDER_NOTE, "PLATA Membership");
        params.put(PARAM_CUSTOMER_NAME, store.getName());
        params.put(PARAM_CUSTOMER_PHONE, user.getMobileNumber());
        params.put(PARAM_CUSTOMER_EMAIL, user.getEmail().equalsIgnoreCase("")?"noemail@eunicoinfotech.com":user.getEmail());
        params.put(PARAM_ORDER_CURRENCY, "INR");
        cfPaymentService.doPayment(getActivity(), params, currentPayment.getExtAttr1Value(), "PROD", "#FFFFEC4F", "#000000", false);

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


    public void onCashFreePaymentSuccess( String signature, String referenceNumber) {

        currentPayment.setExtAttr2Name("cashfreeSignature");
        currentPayment.setExtAttr2Value(signature);
        currentPayment.setExtAttr3Name("cashfreePaymentId");
        currentPayment.setExtAttr3Value(referenceNumber);
        currentPayment.setMop(4);
        updatePaymentForSuccess();
    }

    public void onCashFreePaymentError(String signature, String referenceNumber) {
        updatePaymentForError();
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
        updatePaymentForError();
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
                    //createUpdateMembership();
                    completeMembershipFlow(); // this is not required anymore due to messaging implementation
                    // create membership and update plan ..save
                    // update MY-Ledger
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                    refreshBtn.setVisibility(View.VISIBLE);
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
                refreshBtn.setVisibility(View.VISIBLE);
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }

    public void updatePaymentForError() {
        helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
        currentPayment.setStatus(400);
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
                    helper.handleErrorScheme1("Payment failed");
                    completeMembershipFlow();
                    // create membership and update plan ..save
                    // update MY-Ledger
                } catch (Exception e) {
                    helper.handleErrorScheme1(e.getMessage());
                    refreshBtn.setVisibility(View.VISIBLE);
                }
                helper.dismissProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                helper.handleErrorScheme1("Sytem Error");
                refreshBtn.setVisibility(View.VISIBLE);
            }
        });
        helper.handleRetry(jsonObjectRequest);
    }



   private void completeMembershipFlow(){
       helper.showProgressBar(getContext().getString(R.string.registration3_progressbar_msg));
       if(currentPayment.getStatus()==2){
           currentMembership.setIntent(2);
           currentMembership.getPlanHistories().get(currentMembership.getPlanHistories().size()-1).setStatus(5);
       }
       currentMembership.setStatus(5);
       JSONObject postData = null;
       try {
           postData = new JSONObject(gson.toJson(currentMembership));
       } catch (JSONException e) {
           helper.handleErrorScheme1("System Exception: Error parsing JSON");
       }
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + helper.getClientCode() + "/customers/" + helper.getUser().getCustomerId() + "/memberships", postData, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject orderObj) {
               try {
                   if(currentPayment.getStatus()==2 && currentMembership.getStatus()==5){
                       Fragment fragment = new StorePlanSetupSuccessFragment();
                       FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                       ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                       ft.commit();
                   }

               } catch (Exception e) {
                   helper.handleErrorScheme1(e.getMessage());
                   refreshBtn.setVisibility(View.VISIBLE);
               }
               helper.dismissProgressBar();
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               helper.handleErrorScheme1("Failed to update membership post payment");
               refreshBtn.setVisibility(View.VISIBLE);
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

    private void doFinalWorks(){

        if((currentMembership.getStatus()==1)&& (currentPayment==null)){
            startMembershipFlow();
        } else if((currentMembership.getStatus()==1)&& (currentPayment.getStatus()==1)){
            startMembershipFlow();
        }else if((currentMembership.getStatus()==1)&& (currentPayment.getStatus()==2)){
            updatePaymentForSuccess();
        }else if((currentMembership.getStatus()==5)&& (currentPayment.getStatus()==2)){
            updatePaymentForSuccess();
        }

    }



    /*
        1. create membership new if doesnt exist else update existing membership with new plan  FAIL
        2. on response --> create payment   FAIL, UNKNOWN -->  --> M1 no P --> reset M5
        3. on success --> open cashfree     FAIL --> M1, P1
        3. on sucess --> update payment status  FAIL (M1,P1) --> this can only be done manually or api call from backend --> M2,P2 OR M5,P1
        4. on sucess --> update membership status   FAIL (M1, P2) --> M5,P2
        5. behind the scene --> update property membersjip FAIL -->
     */

}

