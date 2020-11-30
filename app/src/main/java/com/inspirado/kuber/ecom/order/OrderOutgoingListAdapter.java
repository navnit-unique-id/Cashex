package com.inspirado.kuber.ecom.order;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class OrderOutgoingListAdapter extends RecyclerView.Adapter<OrderOutgoingListAdapter.RequestHolder> {
    private Context context;
    ArrayList orders;
    User user;


   public OrderOutgoingListAdapter(Context context, ArrayList storeRequests, User user) {
        this.context = context;
        this.user = user;
   }

    public void setOrders(ArrayList orders){
       this.orders=orders;
    }
    public void setUser(User user){
       this.user=user;
    }
    @Override
    public OrderOutgoingListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(context).inflate(R.layout.listadapter_ecom_order_outgoing_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
       if(orders!=null) {
           holder.order=(Order) orders.get(position);
           holder.storeName.setText(holder.order.getSeller().getSellerName()+"");
           holder.storeAddress.setText(holder.order.getSeller().getSellerAddress()+"");
           holder.status.setText(getStatusText((Order) orders.get(position)));
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

    private String getStatusText(Order order) {
        int mop = order.getMop();
        Date orderDate = order.getDateOfOrder();
        if ((order.getStatus() == 2))
            return "Pending Payment";
        if ((order.getStatus() == 3) || (order.getStatus() == 4))
            return "Waiting Acceptance";
        if ((order.getStatus() == 5))
            return "Preparation in progress";
        if ((order.getStatus() == 6))
            return "Despatched";
        if ((order.getStatus() == 7))
            return "Completed";
        if ((order.getStatus() == 11))
            return "Cancelled";
        if ((order.getStatus() == 12))
            return "Rejected";
   return "Unknown";
   }


    @Override
    public int getItemCount() {
        if(orders!=null){
            return orders.size();
        }
        return 0;
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView storeName;
        public TextView storeAddress;
        public TextView time;
        public TextView status;
        public ImageView statusImage;
        public CardView cardView;
        public Order order;
         public RequestHolder(View v) {
            super(v);
             storeName = v.findViewById(R.id.storeName);
             storeAddress = v.findViewById(R.id.storeAddress);
             time=  v.findViewById(R.id.time);
             status=  v.findViewById(R.id.status);
             cardView = v.findViewById(R.id.request_summary_card);
        }
    }

}