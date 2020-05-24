package com.inspirado.kuber.cash;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CashRequestListAdapter extends RecyclerView.Adapter<CashRequestListAdapter.RequestHolder> {
    private Context context;
    ArrayList cashRequests;
    User user;


   public CashRequestListAdapter(Context context, ArrayList cashRequests, User user) {
        this.context = context;
        cashRequests = cashRequests;
        this.user = user;
   }

    public void setRequests(ArrayList Requests){
       this.cashRequests=Requests;
    }
    public void setUser(User user){
       this.user=user;
    }
    @Override
    public CashRequestListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View v =  LayoutInflater.from(context)
                .inflate(R.layout.request_list_item_view, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
       if(cashRequests!=null) {
           holder.mRequestIDView.setText(((CashRequest) cashRequests.get(position)).getId()+"");
           holder.mRequestAmountView.setText(((CashRequest) cashRequests.get(position)).getAmount()+"");
           String dateBy = (new SimpleDateFormat("dd-MMM-yyyy")).format(((CashRequest) cashRequests.get(position)).getRequestDate());
           String time = ((CashRequest) cashRequests.get(position)).getPaymentSlot();
           holder.mRequestDateView.setText( dateBy + " " +time);
           holder.status.setText(Util.getStatusDetail(((CashRequest) cashRequests.get(position)).getStatus()));
           holder.cashRequest = (CashRequest) cashRequests.get(position);
           String displayScore = "FRS "+holder.cashRequest.getRequestor().getOverallScore();
           if(user.getId().equals(holder.cashRequest.getRequesterId())){
                if( (holder.cashRequest.getStatus()!=1 ) && (holder.cashRequest.getLender()!=null)) {
                    displayScore = "FRS "+holder.cashRequest.getLender().getOverallScore();
                }else{
                    displayScore ="";
                }
           }
           holder.frs.setText(displayScore);
           holder.mRequester.setText(holder.cashRequest.getRequestor().getName());
           CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);
           card.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   Fragment fragment = new CashRequestDetailsFragment();
                   ((CashRequestDetailsFragment) fragment).setCashRequest(holder.cashRequest);
                   FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                   ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                   ft.commit();
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        if(cashRequests!=null){
            return cashRequests.size();
        }
        return 0;
    }

    private void showDetails(View view){
        Log.d("TAG", "showDetails: NEED TO OPEN DETAILS HERE");
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView mRequestIDView;
        public TextView mRequestAmountView;
        public TextView mRequestDateView;
        public TextView mRequester;
        public TextView status;
        public TextView frs;
        public CardView cardView;
        TextView gpsLabel;
        public TextView  optionsView;
        public CashRequest cashRequest;

        public RequestHolder(View v) {
            super(v);
            mRequestIDView = v.findViewById(R.id.name);
            mRequestAmountView = v.findViewById(R.id.requestAmount);
            mRequestDateView = v.findViewById(R.id.requestDate);
            status=  v.findViewById(R.id.km);
            mRequester=v.findViewById(R.id.txtName);
            frs=v.findViewById(R.id.kmLbl);
            cardView = v.findViewById(R.id.request_summary_card);
        }
    }

}