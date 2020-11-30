package com.inspirado.kuber.ecom.order;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;


import java.util.List;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemListAdapter.RequestHolder> {
    private Context context;
    List orderItems;
    User user;

    public OrderItemListAdapter(Context context, List orderItems, User user) {
        this.context = context;
        this.orderItems = orderItems;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public OrderItemListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.listadapter_ecom_order_list_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
        if (orderItems != null) {
            OrderItem orderItem = (OrderItem) orderItems.get(position);
            String description = orderItem.getBrand() + " " + orderItem.getName() + " " + orderItem.getDescription();
            String price = "Rs " + orderItem.getPrice();
            holder.price.setText(price);
            holder.description.setText(description);
            //assuming this inventoty item is not in order
            holder.quantity.setText(orderItem.getQuantity()+" "+ orderItem.getUom());
            holder.orderItem = orderItem;
            CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);
       }
    }

    @Override
    public int getItemCount() {
        if (orderItems != null) {
            return orderItems.size();
        }
        return 0;
    }


    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView price;
        public TextView quantity;
        public CardView cardView;
        public OrderItem orderItem;

        public RequestHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            price = v.findViewById(R.id.price);
            quantity = v.findViewById(R.id.quantity);
            cardView = v.findViewById(R.id.request_summary_card);
        }

    }


}