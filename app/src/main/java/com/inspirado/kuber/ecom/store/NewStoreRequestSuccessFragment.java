package com.inspirado.kuber.ecom.store;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inspirado.kuber.IncomingRequestListFragment;
import com.inspirado.kuber.R;

/**
 * Created by Belal on 18/09/16.
 */


public class NewStoreRequestSuccessFragment extends Fragment {

    String message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_store_setup_success, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.cr_details_title);
      //  ((TextView)view.findViewById(R.id.txtMessage)).setText(message);
        Button  homeBtn = (Button) getActivity().findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        Button  paymentBtn = (Button) getActivity().findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPaymentSetup();
            }
        });
    }

    private void goHome() {
        Fragment   fragment = new IncomingRequestListFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
        ft.commit();
    }

    private void gotoPaymentSetup() {
        Fragment   fragment = new StoreAccountFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
        ft.commit();
    }
    public void setMessage(String message){
        this.message=message;
    }
}

