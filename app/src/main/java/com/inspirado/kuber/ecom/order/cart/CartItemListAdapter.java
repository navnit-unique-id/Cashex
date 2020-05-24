package com.inspirado.kuber.ecom.order.cart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.order.Order;
import com.inspirado.kuber.ecom.order.OrderItem;
import com.inspirado.kuber.ecom.order.inventory.Inventory;
import com.inspirado.kuber.ecom.order.inventory.StoreInventoryDetailsFragment;

import java.util.List;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class CartItemListAdapter extends RecyclerView.Adapter<CartItemListAdapter.RequestHolder> {
    private Context context;
  //  List inventoryItems;
    User user;
    private RadioButton lastCheckedRB = null;
    Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CartItemListAdapter(Context context, List inventoryItems, User user) {
        this.context = context;
     //   this.inventoryItems = inventoryItems;
        this.user = user;
    }

    public void setRequests(List Requests) {
      //  this.inventoryItems = Requests;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public CartItemListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.listadapter_ecom_order_cart_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null) {
            OrderItem orderItem = (OrderItem) orderItems.get(position);
            String description = orderItem.getBrand() + " " + orderItem.getName() + " " + orderItem.getDescription();
            String price = "Rs " + orderItem.getPrice();
            holder.price.setText(price);
            holder.description.setText(description);
            holder.quantity.setText(orderItem.getQuantity()+"");
            holder.orderItem = orderItem;
            CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);

            holder.plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt("0" + holder.quantity.getText());
                    if (quantity+1 > holder.orderItem.getMaxQuantity()) {
                        Snackbar.make(v, "Exceeds inventory in stock", Snackbar.LENGTH_LONG).show();
                    } else {
                        int tQty = Integer.parseInt("0" + holder.quantity.getText());
                        holder.quantity.setText((tQty + 1) + "");
                        orderItem.setQuantity(tQty + 1);
                        order.updateOrderItem(orderItem);
                        broadCastOrder();
                    }
                }
            });

            holder.minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt("0" + holder.quantity.getText());
                    if (quantity == 1) {
                        holder.minusBtn.setVisibility(View.GONE);
                        holder.plusBtn.setVisibility(View.GONE);
                        holder.quantity.setVisibility(View.GONE);
                        holder.addBtn.setVisibility(View.VISIBLE);
                        order.removeOrderItem(orderItem);
                        notifyItemRemoved(position);
                    }
                    holder.quantity.setText((quantity - 1) + "");
                    orderItem.setQuantity(quantity - 1);
                    order.updateOrderItem(orderItem);
                    broadCastOrder();
                }
            });

            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.quantity.setText("1");
                    orderItem.setQuantity(1);
                    order.getOrderItems().add(orderItem);
                    holder.minusBtn.setVisibility(View.VISIBLE);
                    holder.plusBtn.setVisibility(View.VISIBLE);
                    holder.quantity.setVisibility(View.VISIBLE);
                    holder.addBtn.setVisibility(View.GONE);
                    broadCastOrder();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (order.getOrderItems() != null) {
            return order.getOrderItems().size();
        }
        return 0;
    }

    public void broadCastOrder() {
        Intent intent = new Intent("cart-message");
        intent.putExtra("order", order);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView price;
        public TextView quantity;
        public CardView cardView;
        public OrderItem orderItem;
        public Button plusBtn;
        public Button minusBtn;
        public Button addBtn;

        public RequestHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            price = v.findViewById(R.id.price);
            quantity = v.findViewById(R.id.quantity);
            plusBtn = v.findViewById(R.id.plusBtn);
            minusBtn = v.findViewById(R.id.minusBtn);
            addBtn = v.findViewById(R.id.addBtn);
            cardView = v.findViewById(R.id.request_summary_card);
        }
    }
}