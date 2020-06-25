package com.inspirado.kuber.ecom.order.inventory;

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

import java.util.List;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.RequestHolder> {
    private Context context;
    List inventoryItems;
    User user;
    private RadioButton lastCheckedRB = null;
    Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public InventoryListAdapter(Context context, List inventoryItems, User user) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.user = user;
    }

    public void setRequests(List Requests) {
        this.inventoryItems = Requests;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public InventoryListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.listadapter_ecom_order_inventory_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
        if (inventoryItems != null) {
            Inventory inventory = (Inventory) inventoryItems.get(position);
            String description = inventory.getBrand() + " " + inventory.getName() + " " + inventory.getDescription();
            String mrp = "Rs " + inventory.getMrp();
            String price = "Rs " + inventory.getPrice();
            String qtyLeft = inventory.getQuantity() + " " + inventory.getUom() + " left";

            holder.description.setText(description);
            if (mrp.equalsIgnoreCase(price)) {
                holder.mrp.setText(mrp);
                holder.mrp.setVisibility(View.GONE);
            } else {
                holder.mrp.setText(mrp);
                holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mrp.setVisibility(View.VISIBLE);
            }
            holder.qty.setText(qtyLeft);
            holder.price.setText(price);
            //assuming this inventoty item is not in order
            holder.quantity.setText("0");
            holder.minusBtn.setVisibility(View.GONE);
            holder.plusBtn.setVisibility(View.GONE);
            holder.quantity.setVisibility(View.GONE);
            holder.addBtn.setVisibility(View.VISIBLE);
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(orderItem -> {
                    if (orderItem.getInventoryId().equals(inventory.getId())) {
                        holder.quantity.setText(orderItem.getQuantity() + "");
                        holder.minusBtn.setVisibility(View.VISIBLE);
                        holder.plusBtn.setVisibility(View.VISIBLE);
                        holder.quantity.setVisibility(View.VISIBLE);
                        holder.addBtn.setVisibility(View.GONE);
                    }
                });
            }
            //    holder.quantity.setText(inventory.getQuantity()+"");
            holder.inventory = inventory;
            CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);

            holder.plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt("0" + holder.quantity.getText());
                    if (quantity+1 > holder.inventory.getQuantity()) {
                        Snackbar.make(v, "Exceeds inventory in stock", Snackbar.LENGTH_LONG).show();
                    } else {
                        int tQty = quantity;
                        holder.quantity.setText((tQty + 1) + "");
                        order.addItemAsPerInventory(holder.inventory, (tQty + 1));
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
                    }
                    holder.quantity.setText((quantity - 1) + "");
                    order.removeItemAsPerInventory(holder.inventory, quantity - 1);
                    broadCastOrder();
                }
            });

            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = Integer.parseInt("0" + holder.quantity.getText());
                    if (holder.inventory.getQuantity() < 1) {
                        Snackbar.make(v, "Exceeds inventory in stock", Snackbar.LENGTH_LONG).show();
                    } else {
                        holder.quantity.setText("1");
                        order.addItemAsPerInventory(holder.inventory, 1);
                        holder.minusBtn.setVisibility(View.VISIBLE);
                        holder.plusBtn.setVisibility(View.VISIBLE);
                        holder.quantity.setVisibility(View.VISIBLE);
                        holder.addBtn.setVisibility(View.GONE);
                        broadCastOrder();
                    }
                }
            });

/*            card.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new StoreInventoryDetailsFragment();
                    ((StoreInventoryDetailsFragment) fragment).setInventory(holder.inventory);
                    FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        if (inventoryItems != null) {
            return inventoryItems.size();
        }
        return 0;
    }

    public void broadCastOrder() {
        Intent intent = new Intent("order-message");
        intent.putExtra("order", order);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView mrp;
        public TextView price;
        public TextView quantity;
        public TextView qty;
        public CardView cardView;
        public Inventory inventory;
        public Button plusBtn;
        public Button minusBtn;
        public Button addBtn;

        public RequestHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            mrp = v.findViewById(R.id.mrp);
            price = v.findViewById(R.id.price);
            quantity = v.findViewById(R.id.quantity);
            qty = v.findViewById(R.id.qty);
            plusBtn = v.findViewById(R.id.plusBtn);
            minusBtn = v.findViewById(R.id.minusBtn);
            addBtn = v.findViewById(R.id.addBtn);
            cardView = v.findViewById(R.id.request_summary_card);
        }

        public void destroy() {
            inventoryItems.remove(inventory);
            notifyDataSetChanged();
        }

        public Inventory getInventory() {
            return inventory;
        }
    }


}