package com.inspirado.kuber.ecom.order;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class OrderIncomingListAdapter extends RecyclerView.Adapter<OrderIncomingListAdapter.RequestHolder> {
    private Context context;
    ArrayList orders;
    User user;
    private RadioButton lastCheckedRB = null;


   public OrderIncomingListAdapter(Context context, ArrayList storeRequests, User user) {
        this.context = context;
       orders = orders;
        this.user = user;
   }

    public void setOrders(ArrayList orders){
       this.orders=orders;
    }
    public void setUser(User user){
       this.user=user;
    }
    @Override
    public OrderIncomingListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(context).inflate(R.layout.listadapter_ecom_order_incoming_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
       if(orders!=null) {
           holder.order=(Order) orders.get(position);
           holder.reqId.setText(((Order) orders.get(position)).getId()+"");
           holder.customerName.setText(((Order) orders.get(position)).getBuyer().getBuyerName()+"");
           holder.customerAddress.setText(((Order) orders.get(position)).getBuyer().getBuyerAddress()+"");
           Date dateOfOrder = ((Order) orders.get(position)).getDateOfOrder();
           String time ="";
           if(dateOfOrder!=null){
                time = (new SimpleDateFormat("dd-MMM-yyyy HH:mm")).format(dateOfOrder);
           }
           holder.time.setText(time);
           CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);
           card.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   Fragment fragment = new OrderDetailsFragment();
                   ((OrderDetailsFragment) fragment).setOrder(holder.order);
                   FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                   ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                   ft.commit();
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        if(orders!=null){
            return orders.size();
        }
        return 0;
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView reqId;
        public TextView customerName;
        public TextView customerAddress;
        public TextView time;
        public TextView status;
        public ImageView statusImage;
        public CardView cardView;
        public Order order;
         public RequestHolder(View v) {
            super(v);
             reqId = v.findViewById(R.id.reqId);
             customerName = v.findViewById(R.id.customerName);
             customerAddress = v.findViewById(R.id.customerAddress);
             time=  v.findViewById(R.id.time);
            cardView = v.findViewById(R.id.request_summary_card);
        }
    }

}