package com.inspirado.kuber.ecom.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.ecom.store.Store;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.util.Util;

import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.RequestHolder> {
    private Context context;
    Hashtable stores;
    ArrayList<Store> storeList;
    User user;


    public StoreItemAdapter(Context context, Hashtable stores, User user) {
        this.context = context;
        stores = stores;
        storeList = new ArrayList<Store>(stores.values());
        storeList.sort(Comparator.comparingDouble(l1 -> l1.getRefDistance()));
        this.user = user;
    }

    public void setStores(Hashtable stores) {
        this.stores = stores;
        storeList = new ArrayList<Store>(stores.values());
        storeList.sort(Comparator.comparingDouble(l1 -> l1.getRefDistance()));
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public StoreItemAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.listadapter_ecom_order_store, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
        if (stores != null) {
            holder.mName.setText(((Store) storeList.get(position)).getName() + "");
            holder.mAddress.setText(((Store) storeList.get(position)).getAddress() + "");
            holder.km.setText(String.format("%.2f", ((Store) storeList.get(position)).getRefDistance()));
            holder.isSelected.setChecked(((Store) storeList.get(position)).isSelected());
            holder.store = (Store) storeList.get(position);
            holder.ratingBar2.setRating((float)holder.store.getPropertyRatingScoreAvg());
            holder.ratingText.setText((new DecimalFormat("#.00")).format(holder.store.getPropertyRatingScoreAvg()) );

            holder.imageView9.setBackgroundResource(holder.store.isHasDeliveryService()?R.mipmap.ic_delivery:R.mipmap.ic_no_delivery);
            holder.category.setText(holder.store.getTypeTxt());
            UserPreference thisStoreIsMyFavDetails = getMatchingPreferenceForStore(holder.store.getId());
            holder.store.setFavouriteStorePreferenceEntry(thisStoreIsMyFavDetails);
            holder.toggleButton.setChecked("true".equalsIgnoreCase(thisStoreIsMyFavDetails==null?"":thisStoreIsMyFavDetails.getAttributValue()) ?true:false);
            holder.toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( ((ToggleButton)v).isChecked()){
                        UserPreference userPreference = getMatchingPreferenceForStore(holder.store.getId()) ; //holder.store.getFavouriteStorePreferenceEntry();
                        if(userPreference==null) userPreference = new  UserPreference(null, user.getId(), 1, holder.store.getId(), "favouritestore", "true", "boolean");
                        userPreference.setAttributValue("true");
                        updatePreference(v,userPreference);
                    }else{
                        UserPreference userPreference = getMatchingPreferenceForStore(holder.store.getId());//holder.store.getFavouriteStorePreferenceEntry();
                        if(userPreference==null) userPreference = new  UserPreference(null, user.getId(), 1, holder.store.getId(), "favouritestore", "true", "boolean");
                        userPreference.setAttributValue("false");
                        updatePreference(v,userPreference);
                    }
                }
            });
            RadioButton isSelectedView = (RadioButton) holder.cardView.findViewById(R.id.isSelected);
            isSelectedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeList.stream().forEach(store -> store.setSelected(false));
                    Store store = (Store) storeList.get(position);
                    Long id = store.getId();
                    ((Store)stores.get(id)).setSelected(true);
                }
              }
            );
        }
    }


    private UserPreference getMatchingPreferenceForStore(Long storeId){
        if(user.getUserPreferences()==null) return null;
        List<UserPreference> thisStoreIsMyFavouriteEntry = user.getUserPreferences().stream().filter(userPreference -> {
            if ( (userPreference.type==1)
                    && userPreference.getTypeId()!=null && (userPreference.getTypeId().equals(storeId))
                    && ("favouritestore".equalsIgnoreCase(userPreference.getAttributeName())) ){
                return true;
            }
            return false;
        } ).collect(Collectors.toList());
        if( (thisStoreIsMyFavouriteEntry!=null)&& (thisStoreIsMyFavouriteEntry.size()!=0)){
            return thisStoreIsMyFavouriteEntry.get(0);
        }
        return null;
    }

    private List<UserPreference> updateMatchingPreferenceForStore(List<UserPreference> preferences, UserPreference preferenceToBeUpdted){
        boolean newRecord = true;
        for (int i = 0; i < preferences.size(); i++) {
            UserPreference userPreference = preferences.get(i);
            if ( userPreference.getId().equals(preferenceToBeUpdted.getId())){
                userPreference.setTypeId(preferenceToBeUpdted.getTypeId());
                userPreference.setUserId(preferenceToBeUpdted.getUserId());
                userPreference.setAttributeName(preferenceToBeUpdted.getAttributeName());
                userPreference.setAttributeType(preferenceToBeUpdted.getAttributeType());
                userPreference.setAttributValue(preferenceToBeUpdted.getAttributValue());
                userPreference.setType(preferenceToBeUpdted.getType());
                newRecord=false;
            }
        }
        if(newRecord) preferences.add(preferenceToBeUpdted);
        user.setUserPreferences(preferences);
        SharedPreferences pref = context.getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user", (new Gson()).toJson(user));
        editor.commit();
         return preferences;
    }


    private void updatePreference(View v, UserPreference userPreference) {
        int method = Request.Method.PUT;
        final ProgressDialog progressDialog = new ProgressDialog(context);
        JSONObject postData = null;
        try {
            postData = new JSONObject(new Gson().toJson(userPreference));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, context.getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/cashrequest/userpreferences", postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObj) {
                        UserPreference updatedUserPreference = (new Gson()).fromJson(responseObj.toString(), UserPreference.class);
                        updateMatchingPreferenceForStore(user.getUserPreferences(),updatedUserPreference );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar.make(v, UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        if (stores != null) {
            return stores.size();
        }
        return 0;
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mAddress;
        public TextView km;
        public RadioButton isSelected;
        public ToggleButton toggleButton;
        public CardView cardView;
        public RatingBar ratingBar2;
        public TextView ratingText;
        public ToggleButton imageView9;
        public TextView category;
        public Store store;

        public RequestHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.name);
            mAddress = v.findViewById(R.id.address);
            km = v.findViewById(R.id.km);
            cardView = v.findViewById(R.id.request_summary_card);
            isSelected = v.findViewById(R.id.isSelected);
            toggleButton=v.findViewById(R.id.toggleButton);
            ratingBar2=v.findViewById(R.id.ratingBar2);
            ratingText=v.findViewById(R.id.ratingText);
            imageView9=v.findViewById(R.id.imageView9);
            category=v.findViewById(R.id.category);
        }
    }

}