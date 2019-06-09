package com.inspirado.kuber;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestHolder> {
    private Context context;
    ArrayList cashRequests;

   public RequestListAdapter(Context context, ArrayList Requests) {
        this.context = context;
        Requests = Requests;
    }

    public void setRequests(ArrayList Requests){
       this.cashRequests=Requests;
    }

    @Override
    public RequestListAdapter.RequestHolder onCreateViewHolder(ViewGroup parent,
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

           holder.mRequester.setText(holder.cashRequest.getRequestor().getName());
           CardView card = (CardView) holder.cardView.findViewById(R.id.request_summary_card);
           card.setOnClickListener(new View.OnClickListener() {

               public void onClick(View v) {
                   Fragment fragment = new CashRequestDetailsFragment();
                   ((CashRequestDetailsFragment) fragment).setCashRequest(holder.cashRequest);
                   FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                   ft.replace(R.id.content_frame, fragment);
                   ft.commit();
               }
           });


/*           boolean isOnline = holder.cashRequest.isOnLine();
           boolean isEngineOn = holder.cashRequest.isEngineOn();
           String message="";
           if (!isOnline) {
               gpsStatus = "Offline since ";
               Date dte = holder.cashRequest.getGpsTime();
               long duration = TimeUnit.MILLISECONDS.toMinutes((new Date()).getTime() - dte.getTime() );
               if(duration>60){
                   duration = TimeUnit.MILLISECONDS.toHours((new Date()).getTime() - dte.getTime() );
                   if(duration > 24){
                       duration = TimeUnit.MILLISECONDS.toDays((new Date()).getTime() - dte.getTime() );
                       message=duration+" days";
                   }else{
                       message= duration+ " hours" ;
                   }
               } else{
                   message = duration+" minutes";
               }
               gpsStatus=gpsStatus+message;
               holder.gpsLabel.setTextColor(Color.RED);
           }
           holder.mRequestStatusView.setText(gpsStatus);

           if(isEngineOn){
               engineStatus="On";
           }
           holder.mRequestEngineStatusView.setText(engineStatus);
*/

       //    final String imei = holder.cashRequest.getImei();
      //     Button trackBtn = (Button) holder.cardView.findViewById(R.id.trackBtn);
     /*      trackBtn.setOnClickListener(new View.OnClickListener() {

               public void onClick(View v) {
                   Intent intent = new Intent(context, MapsActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   intent.putExtra("Request", holder.Request );
                   context.startActivity(intent);
               }
           });*/


 /*          holder.optionsView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   final CashRequest v = holder.cashRequest;
                   Log.d("TAG", "onMenuItemClick1: " + v.getRequesterId());

                   //creating a popup menu
                   PopupMenu popup = new PopupMenu(context, holder.optionsView);
                   //inflating menu from xml resource
                   popup.inflate(R.menu.Request_options_menu);
                   //adding click listener
 *//*                  popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                       @Override
                       public boolean onMenuItemClick(MenuItem item) {
                           switch (item.getItemId()) {
                               case R.id.details:
                                 //  Log.d("TAG", "onMenuItemClick: " + v.getRequestName());
                                   Intent intent = new Intent(context, RequestSettingActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.putExtra("Request", v);
                                   context.startActivity(intent);

                                   //handle menu1 click
                                   break;
                               case R.id.geofence:
                                   //handle menu2 click
                                   break;
                               case R.id.playback:
                                *//**//*   Log.d("TAG", "onMenuItemClick: " + v.getRequestName());
                                   intent = new Intent(context, ReplayActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.putExtra("Request", v);
                                   context.startActivity(intent);
                                   break;*//**//*
                           }
                           return false;
                       }
                   });*//*
                   //displaying the popup
                   popup.show();

               }
           });*/
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
        public CardView cardView;
        TextView gpsLabel;
        public TextView  optionsView;
        public CashRequest cashRequest;

        public RequestHolder(View v) {
            super(v);
            mRequestIDView = v.findViewById(R.id.requestId);
            mRequestAmountView = v.findViewById(R.id.requestAmount);
            mRequestDateView = v.findViewById(R.id.requestDate);
            status=  v.findViewById(R.id.status);
            mRequester=v.findViewById(R.id.txtName);
            //   mRequestEngineStatusView = v.findViewById(R.id.engineStatus);
          //  gpsLabel = v.findViewById(R.id.gpsLabel);

            cardView = v.findViewById(R.id.request_summary_card);
         //   optionsView = v.findViewById(R.id.textViewOptions);
        }
    }

}