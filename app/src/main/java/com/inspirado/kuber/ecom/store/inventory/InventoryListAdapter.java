package com.inspirado.kuber.ecom.store.inventory;

import android.content.Context;
import android.graphics.Movie;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import java.util.ArrayList;
import java.util.List;

//import com.inspirado.kuber.tracking.MapsActivity;
//import com.inspirado.kuber.tracking.ReplayActivity;


public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.RequestHolder> implements Filterable {
    private Context context;
    ArrayList<Inventory> inventoryItems;
    ArrayList<Inventory> inventoryItemsFiltered;
    User user;
    private RadioButton lastCheckedRB = null;


   public InventoryListAdapter(Context context, ArrayList inventoryItems, User user) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.inventoryItemsFiltered=inventoryItems;
        this.user = user;
   }

    public void setRequests(ArrayList Requests){
       this.inventoryItems=Requests;
        this.inventoryItemsFiltered=inventoryItems;
    }
    public void setUser(User user){
       this.user=user;
    }
    @Override
    public InventoryListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v =  LayoutInflater.from(context)
                .inflate(R.layout.listadapter_ecom_store_inventory_item, parent, false);
        RequestHolder vh = new RequestHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestHolder holder, int position) {
       if(inventoryItemsFiltered!=null) {
           Inventory inventory = (Inventory) inventoryItemsFiltered.get(position);
           String description = inventory.getBrand()+" "+inventory.getName()+" "+inventory.getDescription();
           String mrp="Rs "+inventory.getMrp();
           String price = "Rs "+ inventory.getPrice();
           String qtyLeft = inventory.getQuantity()+" " + inventory.getUom() +" left";

           holder.description.setText(description);
           if(mrp.equalsIgnoreCase(price)){
               holder.mrp.setVisibility(View.GONE);
           }else{
               holder.mrp.setText(mrp);
               holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
           }
           holder.qty.setText(qtyLeft);
           holder.price.setText(price);
           holder.quantity.setText(inventory.getQuantity()+"");
           holder.inventory = inventory;
           CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);
           card.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   Fragment fragment = new StoreInventoryDetailsFragment();
                   ((StoreInventoryDetailsFragment) fragment).setInventory(holder.inventory);
                   FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                   ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                   ft.commit();
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        if(inventoryItemsFiltered!=null){
            return inventoryItemsFiltered.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                   inventoryItemsFiltered = inventoryItems;
                } else {
                    ArrayList<Inventory> filteredList = new ArrayList<>();
                    for (Inventory inventory : inventoryItems) {
                        if ( (inventory.getBrand().toLowerCase().contains(charString.toLowerCase())) || (inventory.getCategory().toLowerCase().contains(charString.toLowerCase())) || (inventory.getName().toLowerCase().contains(charString.toLowerCase())) ||   inventory.getDescription().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(inventory);
                        }
                    }
                    inventoryItemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inventoryItemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
               inventoryItemsFiltered = (ArrayList<Inventory>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    private void showDetails(View view){
        Log.d("TAG", "showDetails: NEED TO OPEN DETAILS HERE");
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView mrp;
        public TextView price;
        public TextView quantity;
        public TextView qty;
        public CardView cardView;
        public Inventory inventory;
        public RequestHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            mrp=  v.findViewById(R.id.mrp);
            price=v.findViewById(R.id.price);
            quantity=v.findViewById(R.id.quantity);
            qty=v.findViewById(R.id.qty);
            cardView = v.findViewById(R.id.request_summary_card);
        }

        public void destroy(){
            inventoryItems.remove(inventory);
            notifyDataSetChanged();
        }

        public Inventory getInventory(){
            return inventory;
        }
    }
}