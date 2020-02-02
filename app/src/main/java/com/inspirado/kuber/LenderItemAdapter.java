package com.inspirado.kuber;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class LenderItemAdapter extends RecyclerView.Adapter<LenderItemAdapter.RequestHolder> {
    private Context context;
    Hashtable lenders;
    ArrayList<Lender> lenderList;
    User user;


    public LenderItemAdapter(Context context, Hashtable lenders, User user) {
        this.context = context;
        lenders = lenders;
        lenderList = new ArrayList<Lender>(lenders.values());
        this.user = user;
    }

    public void setLenders(Hashtable lenders) {
        this.lenders = lenders;
        lenderList = new ArrayList<Lender>(lenders.values());
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public LenderItemAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.lender_item_view, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
        if (lenders != null) {
            holder.mName.setText(((Lender) lenderList.get(position)).getBusinessName() + "");
            holder.mAddress.setText(((Lender) lenderList.get(position)).getAddress() + "");
            holder.km.setText(String.format("%.2f", ((Lender) lenderList.get(position)).getDistance()));
            holder.isSelected.setChecked(((Lender) lenderList.get(position)).isSelected());
            CheckBox isSelectedView = (CheckBox) holder.cardView.findViewById(R.id.isSelected);
            isSelectedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                      Lender lender = (Lender) lenderList.get(position);
                      Long id = lender.getId();
                      ((Lender)lenders.get(id)).setSelected(isChecked);
                  }
              }
            );
        }
    }

    @Override
    public int getItemCount() {
        if (lenders != null) {
            return lenders.size();
        }
        return 0;
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mAddress;
        public TextView km;
        public CheckBox isSelected;
        public CardView cardView;
        public User lender;

        public RequestHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.name);
            mAddress = v.findViewById(R.id.address);
            km = v.findViewById(R.id.km);
            cardView = v.findViewById(R.id.request_summary_card);
            isSelected = v.findViewById(R.id.isSelected);
        }
    }

}