package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class ProfileFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        setValues(user);
        getActivity().setTitle(R.string.profile_title);
        refreshUser();
    }

    public void setValues(User user){
        ((TextView) getActivity().findViewById(R.id.name)).setText(user.getName());
        ((TextView) getActivity().findViewById(R.id.kmLbl)).setText(user.getOverallScore()+"");
        ((TextView) getActivity().findViewById(R.id.address)).setText(user.getAddress());
        ((TextView) getActivity().findViewById(R.id.state)).setText(user.getState());
        ((TextView) getActivity().findViewById(R.id.city)).setText(user.getCity());
        ((TextView) getActivity().findViewById(R.id.pincode)).setText(user.getPinCode());
        ((TextView) getActivity().findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) getActivity().findViewById(R.id.referral)).setText(user.getMobileNumber());
        ((TextView) getActivity().findViewById(R.id.phone)).setText(user.getMobileNumber());
        ((TextView) getActivity().findViewById(R.id.CompanyCode)).setText(user.getClientCode());

        try{
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionTxt = "V "+pInfo.versionCode+"\n Last Updated Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(new Date(pInfo.lastUpdateTime));
            ((TextView) getActivity().findViewById(R.id.versionInfo)).setText(versionTxt);
        }catch(Exception e){
            Log.e("PRO", e.getMessage());
        }
    }

    protected Boolean refreshUser(Void... params) {
        JsonObjectRequest jsonObjectRequest = null;


        try {
            jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/"+user.getClientCode()+"/cashrequest/users?id=" + user.getId(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user", response.toString());
                    user = (new Gson()).fromJson(response.toString(), User.class);
                    editor.commit();
                    setValues( user);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String UIMessage = "System Exception";
                    Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

