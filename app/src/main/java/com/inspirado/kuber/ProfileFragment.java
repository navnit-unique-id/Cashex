package com.inspirado.kuber;

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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}

